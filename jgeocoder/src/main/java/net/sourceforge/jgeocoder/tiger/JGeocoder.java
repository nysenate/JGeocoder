package net.sourceforge.jgeocoder.tiger;
import static net.sourceforge.jgeocoder.AddressComponent.CITY;
import static net.sourceforge.jgeocoder.AddressComponent.COUNTY;
import static net.sourceforge.jgeocoder.AddressComponent.LAT;
import static net.sourceforge.jgeocoder.AddressComponent.LON;
import static net.sourceforge.jgeocoder.AddressComponent.POSTDIR;
import static net.sourceforge.jgeocoder.AddressComponent.PREDIR;
import static net.sourceforge.jgeocoder.AddressComponent.STATE;
import static net.sourceforge.jgeocoder.AddressComponent.TLID;
import static net.sourceforge.jgeocoder.AddressComponent.TYPE;
import static net.sourceforge.jgeocoder.AddressComponent.ZIP;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jgeocoder.AddressComponent;
import net.sourceforge.jgeocoder.CommonUtils;
import net.sourceforge.jgeocoder.GeocodeAcuracy;
import net.sourceforge.jgeocoder.JGeocodeAddress;
import net.sourceforge.jgeocoder.us.AddressParser;
import net.sourceforge.jgeocoder.us.AddressStandardizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
public class JGeocoder{
  private static final Log LOGGER = LogFactory.getLog(JGeocoder.class);
  private ZipCodesDb _zipDb;
  private ZipCodeDAO _zipDao;
  private TigerLineDao _tigerDao;
  public JGeocoder(){
    this(JGeocoderConfig.DEFAULT);
  }

  private TigerLineHit getTigerLineHitByZip(Map<AddressComponent, String> normalizedAddr, String zip) throws TigerQueryFailedException, DatabaseException{
	  
	  //I removed the "!" from before _zipDao (Vincent Hueber)
      if(zip == null || _zipDao.fillInCSByZip(normalizedAddr, zip)){
          return null;
      }
      normalizedAddr.put(ZIP, zip);
      return _tigerDao.getTigerLineHit(normalizedAddr);
  }
  
  private List<ZipCode> getZips(String city, String state) throws DatabaseException{
      if(city == null || state == null){
          return Collections.emptyList();
      }
      List<ZipCode> ret = new ArrayList<ZipCode>();
      Location loc = new Location();
      loc.setCity(city.replaceAll("\\s+", ""));
      loc.setState(state);
      EntityCursor<ZipCode> zips = null;
      try{
        zips = _zipDao.getZipCodeByLocation().subIndex(loc).entities();
        for(ZipCode zip : zips){
          ret.add(zip);
        }
      }finally{
        if(zips != null){
          zips.close();
        }
      }
      return ret;
  }
  
  
  private TigerLineHit getTigerLineHit(Map<AddressComponent, String> normalizedAddr) throws DatabaseException{
    Map<AddressComponent, String> myMap = new EnumMap<AddressComponent, String>(normalizedAddr);
    TigerLineHit hit = null;
    Set<String> attemptedZips = new HashSet<String>();
    try { //try the parsed zip
    	
      hit = getTigerLineHitByZip(normalizedAddr, normalizedAddr.get(ZIP));
      if(normalizedAddr.get(ZIP)!=null){
          attemptedZips.add(normalizedAddr.get(ZIP));
      }
      if(hit != null){
          return hit;
      }
      if(myMap.get(CITY)==null || myMap.get(STATE) == null){ //use the zip's city, state if the input does not have one
          myMap.put(CITY, normalizedAddr.get(CITY));
          myMap.put(STATE, normalizedAddr.get(STATE));
      }
      List<TigerLineHit> zipHits = new ArrayList<TigerLineHit>();
      
      for(ZipCode zipcode : getZips(myMap.get(CITY), myMap.get(STATE))){
          if(!attemptedZips.contains(zipcode.getZip())){
              hit = getTigerLineHitByZip(myMap, zipcode.getZip());
              if(hit != null){
                  zipHits.add(hit);
              }
              attemptedZips.add(zipcode.getZip());
          }
      }
      if(CollectionUtils.isNotEmpty(zipHits)){
          hit = TigerLineDao.findBest(myMap, zipHits);
      }else{
          County county = _zipDao.getCounty(normalizedAddr.get(CITY), normalizedAddr.get(STATE));
          if(county != null){
              for(String s : county.getZips()){
                  if(!attemptedZips.contains(s)){
                      hit = getTigerLineHitByZip(myMap, s);
                  }
                  if(hit != null){
                      zipHits.add(hit);
                  }
                  attemptedZips.add(s); //
              }
          }
          if(CollectionUtils.isNotEmpty(zipHits)){
              hit = TigerLineDao.findBest(myMap, zipHits);
          }
      }
      if(hit != null){
          String zip = CommonUtils.nvl(hit.zipL, hit.zipR);
          _zipDao.fillInCSByZip(myMap, zip);
          normalizedAddr.putAll(myMap);
          return hit;
      }
      
    } catch (TigerQueryFailedException e) {
        LOGGER.warn("Tiger/Line DB query failed, street level geocoding will be skipped: "+e.getMessage());
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("", e);
        }
        return null;
    }
    return null;
  }
  
  public JGeocodeAddress geocodeAddress(String addrLine){
    JGeocodeAddress ret = new JGeocodeAddress();
    Map<AddressComponent, String> m  = AddressParser.parseAddress(addrLine);
    ret.setParsedAddr(m);
    if(m == null) return ret;//FIXME: throw exception instead
    
    m = AddressStandardizer.normalizeParsedAddress(m);
    ret.setNormalizedAddr(m);
    
    if(m.get(ZIP) == null &&  //if zip is missing
        (m.get(STATE) == null || m.get(CITY)==null)){ //city or state is missing 
      return ret;
    }
    
    GeocodeAcuracy acuracy = GeocodeAcuracy.STREET;
    m = new EnumMap<AddressComponent, String>(m);
    TigerLineHit hit = null;
    try {
    	//PROBLEM IS HERE(VIN)
      hit = getTigerLineHit(m);
    } catch (DatabaseException e) {
      throw new RuntimeException("Unable to query tiger/line database "+e.getMessage());
    }
    if(hit != null){
      acuracy = GeocodeAcuracy.STREET;
      Geo geo = Geocoder.geocodeFromHit(Integer.parseInt(hit.streetNum), hit);
      m.put(ZIP, String.valueOf(geo.zip));
      m.put(PREDIR, hit.fedirp);
      m.put(POSTDIR, hit.fedirs);
      m.put(TYPE, hit.fetype);
      m.put(TLID, String.valueOf(hit.tlid));
      m.put(LAT, String.valueOf(geo.lat));
      m.put(LON, String.valueOf(geo.lon));
      ret.setGeocodedAddr(m);
    }else if(_zipDao.geocodeByZip(m)){
      acuracy = GeocodeAcuracy.ZIP;
      ret.setGeocodedAddr(m);
    }else if(_zipDao.geocodeByCityState(m)){
      acuracy = GeocodeAcuracy.CITY_STATE;
      ret.setGeocodedAddr(m);
    }else{
      return ret;
    }
    
    if(ret.getGeocodedAddr()!=null && 
       ret.getGeocodedAddr().get(COUNTY) == null &&
       ret.getGeocodedAddr().get(ZIP) != null){
      try {
        _zipDao.fillInCSByZip(ret.getGeocodedAddr(), ret.getGeocodedAddr().get(ZIP));
      } catch (DatabaseException e) {
        LOGGER.warn("Unable to query zip code", e);
      }
    }
    
    ret.setAcuracy(acuracy);
    return ret;
  }
  
  public JGeocoder(JGeocoderConfig config){
    _zipDb = new ZipCodesDb();
    _tigerDao = new TigerLineDao(config.getTigerDataSource());
    try {
      _zipDb.init(new File(config.getJgeocoderDataHome()), false, false);
      _zipDao = new ZipCodeDAO(_zipDb.getStore());
    } catch (Exception e) {
      throw new RuntimeException("Unable to create zip db, make sure your system property 'jgeocoder.data.home' is correct"
          +e.getMessage());
    }
    
  }
  
  public void cleanup(){
    if(_zipDb != null){
      try {
        _zipDb.shutdown();
      } catch (DatabaseException e) {
        throw new RuntimeException("Unable to shutdown zip db, "+e.getMessage());
      }
      _zipDb = null;
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    cleanup();
  }
}