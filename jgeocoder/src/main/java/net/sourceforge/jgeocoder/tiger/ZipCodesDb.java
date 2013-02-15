package net.sourceforge.jgeocoder.tiger;
import static net.sourceforge.jgeocoder.AddressComponent.CITY;
import static net.sourceforge.jgeocoder.AddressComponent.COUNTY;
import static net.sourceforge.jgeocoder.AddressComponent.LAT;
import static net.sourceforge.jgeocoder.AddressComponent.LON;
import static net.sourceforge.jgeocoder.AddressComponent.STATE;
import static net.sourceforge.jgeocoder.AddressComponent.ZIP;

import java.io.File;
import java.util.Map;

import net.sourceforge.jgeocoder.AddressComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
class CityWithSpaces{
  @PrimaryKey
  private String _noSpace;
  private String _withSpace;
  public String getNoSpace() {
    return _noSpace;
  }
  public String getWithSpace() {
    return _withSpace;
  }
  public void setNoSpace(String noSpace) {
    _noSpace = noSpace;
  }
  public void setWithSpace(String withSpace) {
    _withSpace = withSpace;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
}

@Entity
class CityStateGeo{
  @PrimaryKey
  private Location _location;
  private float _lat;
  private float _lon;
  public Location getLocation() {
    return _location;
  }
  public void setLocation(Location location) {
    _location = location;
  }
  public float getLat() {
    return _lat;
  }
  public void setLat(float lat) {
    _lat = lat;
  }
  public float getLon() {
    return _lon;
  }
  public void setLon(float lon) {
    _lon = lon;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
}

@Entity
class County{
    @PrimaryKey
    private Location _location;
    private String[] _zips;
    private float _lat;
    private float _lon;
    public Location getLocation() {
        return _location;
    }
    public void setLocation(Location _location) {
        this._location = _location;
    }
    public String[] getZips() {
        return _zips;
    }
    public void setZips(String[] _zips) {
        this._zips = _zips;
    }
    public float getLat() {
        return _lat;
    }
    public void setLat(float _lat) {
        this._lat = _lat;
    }
    public float getLon() {
        return _lon;
    }
    public void setLon(float _lon) {
        this._lon = _lon;
    }
    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }
}

@Entity
class ZipCode{
  @PrimaryKey
  private String _zip;
  @SecondaryKey(relate=Relationship.MANY_TO_ONE)
  private Location _location;
  @SecondaryKey(relate=Relationship.MANY_TO_ONE)
  private String _county;
  private float _lat;
  private float _lon;
  private String _zipClass;
  public String getZip() {
    return _zip;
  }
  public void setZip(String zip) {
    _zip = zip;
  }
  public Location getLocation() {
    return _location;
  }
  public void setLocation(Location location) {
    _location = location;
  }
  public String getCounty() {
    return _county;
  }
  public void setCounty(String county) {
    _county = county;
  }
  public float getLat() {
    return _lat;
  }
  public void setLat(float lat) {
    _lat = lat;
  }
  public float getLon() {
    return _lon;
  }
  public void setLon(float lon) {
    _lon = lon;
  }
  public String getZipClass() {
    return _zipClass;
  }
  public void setZipClass(String zipClass) {
    _zipClass = zipClass;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
}
@Persistent
class Location{ 
  @KeyField(1)
  private String _city;
  @KeyField(2)
  private String _state;

  public String getCity() {
    return _city;
  }
  public void setCity(String city) {
    _city = city;
  }
  public String getState() {
    return _state;
  }
  public void setState(String state) {
    _state = state;
  }
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
}
/**
 * TODO javadocs me
 * @author jliang
 *
 */
class ZipCodeDAO{
  private static final Log LOGGER = LogFactory.getLog(ZipCodeDAO.class);
  private PrimaryIndex<String, ZipCode> _zipCodeByZip;
  private SecondaryIndex<Location, String, ZipCode> _zipCodeByLocation;
  private PrimaryIndex<Location, CityStateGeo> _cityStateGeoByLocation;
  private PrimaryIndex<String, CityWithSpaces> _cityWithSpaceByNoSpace;
  private PrimaryIndex<Location, County> _countyByLocation;
  public ZipCodeDAO(EntityStore store) throws DatabaseException{
    _zipCodeByZip = store.getPrimaryIndex(String.class, ZipCode.class);
    _zipCodeByLocation = store.getSecondaryIndex(_zipCodeByZip, Location.class, "_location");
    _cityStateGeoByLocation = store.getPrimaryIndex(Location.class, CityStateGeo.class);
    _cityWithSpaceByNoSpace = store.getPrimaryIndex(String.class, CityWithSpaces.class);
    _countyByLocation = store.getPrimaryIndex(Location.class, County.class);
  }
  
  public PrimaryIndex<Location, County> getCountyByLocation() {
    return _countyByLocation;
  }

  public PrimaryIndex<String, CityWithSpaces> getCityWithSpaceByNoSpace() {
    return _cityWithSpaceByNoSpace;
  }
  
  public PrimaryIndex<Location, CityStateGeo> getCityStateGeoByLocation() {
    return _cityStateGeoByLocation;
  }
  public SecondaryIndex<Location, String, ZipCode> getZipCodeByLocation() {
    return _zipCodeByLocation;
  }
  public PrimaryIndex<String, ZipCode> getZipCodeByZip() {
    return _zipCodeByZip;
  }
  
  public boolean fillInCSByZip(Map<AddressComponent, String> m, String zip) throws DatabaseException{
	  System.out.println(m.toString());
    return fillInCSByZip(m, _zipCodeByZip.get(zip));
  }
  
  private boolean fillInCSByZip(Map<AddressComponent, String> m, ZipCode zipcode) throws DatabaseException{
      if(zipcode == null){
          return false;
      }
    String city = zipcode.getLocation().getCity();
    CityWithSpaces cws = getCityWithSpaceByNoSpace().get(city);
    if(cws != null){
      m.put(CITY, cws.getWithSpace());
    }else{
      m.put(CITY, city);
    }
    m.put(COUNTY, zipcode.getCounty());
    m.put(STATE, zipcode.getLocation().getState());
    return true;
  }
  
  private County getCounty(Location loc){
     try {
          return _countyByLocation.get(loc);
     } catch (DatabaseException e) {
          if(LOGGER.isDebugEnabled()){
              LOGGER.debug("Unable to get county by city state", e);
          }
          return null;
     }
  }
  
  public County getCounty(String city, String state){
      if(StringUtils.isBlank(city)||StringUtils.isBlank(state)){
          return null;
      }
      city = city.replaceAll("\\s+|\\bCOUNTY$|\\bPARISH$|\\bBOROUGH$", "");
      Location loc = new Location();
      loc.setCity(city); loc.setState(state);
      return getCounty(loc);
  }
  
  public boolean geocodeByCityState(Map<AddressComponent, String> m){
    String city = m.get(AddressComponent.CITY), state = m.get(AddressComponent.STATE);
    if(StringUtils.isBlank(city)||StringUtils.isBlank(state)){
      return false;
    }
    boolean onlyCounty = city.endsWith("COUNTY");
    city = city.replaceAll("\\s+|\\bCOUNTY$|\\bPARISH$|\\bBOROUGH$", "");
    try {
      Location loc = new Location();
      loc.setCity(city); loc.setState(state);
      CityStateGeo geo = onlyCounty? null : _cityStateGeoByLocation.get(loc);
      County county = getCounty(loc);
      if(geo!= null || county != null){
          if(onlyCounty && county != null){
              m.put(LAT, String.valueOf(county.getLat()));
              m.put(LON, String.valueOf(county.getLon()));
              return true;
          }else if(geo != null){
              m.put(LAT, String.valueOf(geo.getLat()));
              m.put(LON, String.valueOf(geo.getLon()));
              return true;   
          }else if(county != null){
              m.put(LAT, String.valueOf(county.getLat()));
              m.put(LON, String.valueOf(county.getLon()));
              return true;
          }
      }
    } catch (DatabaseException e) {
      if(LOGGER.isDebugEnabled()){
        LOGGER.debug("Unable to geocode with city state", e);
      }
      return false;
    }
    return false;
  }
  
  public boolean geocodeByZip(Map<AddressComponent, String> m){
    String zip = m.get(ZIP);
    if(StringUtils.isBlank(zip)){
      return false;
    }
    try {
      ZipCode zipcode = _zipCodeByZip.get(zip);
      if(zipcode != null){
        if(m.get(LAT) == null){
          m.put(LAT, String.valueOf(zipcode.getLat()));
        }
        if(m.get(LON) == null){
          m.put(LON, String.valueOf(zipcode.getLon()));
        }
        return true;
      }
    } catch (Exception e) {
      if(LOGGER.isDebugEnabled()){
        LOGGER.debug("Unable to geocode with zip", e);
      }
      return false;
    }
    return false;
  }
}

class ZipCodesDb{
  private Environment _env = null;
  private EntityStore _store = null;
  public Environment getEnv() {
    return _env;
  }
  public EntityStore getStore() {
    return _store;
  }
  
  public void init(JGeocoderConfig jgconfig, File envHome, boolean readOnly, boolean transactional) throws DatabaseException{
    
    EnvironmentConfig config = new EnvironmentConfig();
    config.setAllowCreate(!readOnly);
    config.setReadOnly(readOnly);
    config.setTransactional(transactional);
    if(jgconfig != null){
      if(jgconfig.getBerkeleyDbCachePercent() >= 0 ){
        config.setCacheSize(jgconfig.getBerkeleyDbCacheSize());
      }
      if(jgconfig.getBerkeleyDbCachePercent() >= 0){
        config.setCachePercent(jgconfig.getBerkeleyDbCachePercent());
      }
    }
    _env = new Environment(envHome, config);
    StoreConfig config2 = new StoreConfig();
    config2.setAllowCreate(!readOnly);
    config2.setReadOnly(readOnly);
    config2.setTransactional(transactional);
    _store = new EntityStore(_env, "ZipCodeEntityStore", config2);
  }
  
  public void init(File envHome, boolean readOnly, boolean transactional) throws DatabaseException{
    init(null, envHome, readOnly, transactional);
  }
  
  public void shutdown() throws DatabaseException{
    if(_store != null){
      _store.close();
    }
    if(_env != null){
      _env.close();
    }
  }
  
}