import net.sourceforge.jgeocoder.tiger.County
import net.sourceforge.jgeocoder.tiger.CityWithSpaces
import net.sourceforge.jgeocoder.tiger.JGeocoderConfig
import net.sourceforge.jgeocoder.tiger.CityStateGeo
import org.apache.commons.lang.StringUtils
import com.sleepycat.persist.EntityStore
import net.sourceforge.jgeocoder.tiger.Location
import net.sourceforge.jgeocoder.tiger.ZipCode
import com.sleepycat.persist.PrimaryIndex
import net.sourceforge.jgeocoder.tiger.ZipCodeDAO
import net.sourceforge.jgeocoder.tiger.ZipCodesDb

import java.util.zip.ZipFile

def db = new ZipCodesDb()
def zip, r, f
try{
//  def myhome = JGeocoderConfig.DEFAULT.getJgeocoderDataHome();
 def myhome = /C:\Users\Bobby\Documents\jgeocoder\jgeocoder-import\src\data/
  db.init(new File(myhome), false, false)
  EntityStore store = db.getStore()
  ZipCodeDAO dao = new ZipCodeDAO(store)

  //return; //comment out to run
/*
    PrimaryIndex idx = dao.getCountyByLocation()
    new File(Thread.currentThread().getContextClassLoader().getResource(/county.txt/).getFile()).eachLine{
        def items = it.split('\\s*[|]\\s*')
        def state = items[0].toUpperCase()
        def county = items[1].replaceAll('\\s', '').toUpperCase()
        def lat = Float.valueOf(items[2])
        def lon = Float.valueOf(items[3])
        String[] zips = items[4].split('\\s+')
        Location loc = new Location('city': county, 'state':state)
        County c = new County()
        c.setLat(lat); c.setLon(lon); c.setLocation(loc); c.setZips(zips)
        idx.put(c)
    }

    Location loc = new Location('city': 'BUCKS', 'state': 'PA')
    println idx.get(loc)
*/
  zip = new ZipFile(Thread.currentThread().getContextClassLoader().getResource(/zip_codes.zip/).getFile())
  f = zip.entries().nextElement()
  r = new BufferedReader(new InputStreamReader(zip.getInputStream(f)))
  PrimaryIndex idx = dao.getZipCodeByZip()
  PrimaryIndex idx2 = dao.getCityWithSpaceByNoSpace()
  ///////////zip_codes.zip///////////
  println "Processing ${zip.name} ..."
  r.eachLine{
    def items = it.split(',')
    if(items[3].contains(' ')){
      idx2.put(new CityWithSpaces(_noSpace:StringUtils.upperCase(items[3].replaceAll('["\\s]','')),
      _withSpace: StringUtils.upperCase(items[3].replaceAll('"',''))))
    }
    items.eachWithIndex() {t, i ->
      items[i] = StringUtils.upperCase(t).replaceAll('["]', '').trim()
    }
    Location loc = new Location(_city:items[3].replaceAll('["\\s]',''), _state:items[4])
    def lat = StringUtils.isBlank(items[1]) ? -1f : Float.valueOf(items[1].trim())
    def lon = StringUtils.isBlank(items[2]) ? -1f : Float.valueOf(items[2].trim())
    ZipCode z = new ZipCode(_zip:items[0],  _location:loc,
        _lat:lat, _lon:lon, _county:items[5],  _zipClass:items[6])
    idx.put(z)
  }

  println idx.get('19148')
  println idx2.get('KINGOFPRUSSIA')

  zip = new ZipFile(Thread.currentThread().getContextClassLoader().getResource(/city_state.zip/).getFile())
  f = zip.entries().nextElement()
  r = new BufferedReader(new InputStreamReader(zip.getInputStream(f)))
  idx = dao.getCityStateGeoByLocation()
  ///////////city_state.zip///////////
  println "Processing ${zip.name} ..."
  r.eachLine{
    def items = it.split('[|]')
    items.eachWithIndex() {t, i ->
      items[i] = StringUtils.upperCase(t.replaceAll('["\\s]',''))
    }
    def citystate = items[0].split(',')
    Location loc = new Location(_city:citystate[0], _state:citystate[1])
    def lat = StringUtils.isBlank(items[1]) ? -1f : Float.valueOf(items[1].trim())
    def lon = StringUtils.isBlank(items[2]) ? -1f : Float.valueOf(items[2].trim())
    CityStateGeo csg = new CityStateGeo(_location: loc, _lat: lat, _lon:lon)
    idx.put(csg)
  }
  println idx.get(new Location(_city:'KINGOFPRUSSIA', _state:'PA'))

}finally{
  if(r!= null) r.close()
  if(zip!=null) zip.close()
  if(db!=null) db.shutdown()
}

