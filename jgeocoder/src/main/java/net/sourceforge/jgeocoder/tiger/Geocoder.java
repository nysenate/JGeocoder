package net.sourceforge.jgeocoder.tiger;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/*
 * geocoding estimation functions are ported from sql codes found on
 * http://www.johnsample.com/
 *
 *
 *
 */

class Geo{
  public float lat, lon;
  public int zip;
  public long tlid;
  public Geo(){}
  public Geo(float lat, float lon, int zip, long tlid) {
    this.lat = lat;
    this.lon = lon;
    this.zip = zip;
    this.tlid = tlid;
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

class Distance{
  public float totalLat=0f, totalLon=0f;
}
/**
 * TODO javadocs me
 * @author jliang
 *
 */
class Geocoder {

  static Geo geocodeFromHit(int streetnum, TigerLineHit hit){
    return geocode(streetnum, hit.tlid, Integer.valueOf(hit.frAddR), Integer.valueOf(hit.frAddL),
        Integer.valueOf(hit.toAddR), Integer.valueOf(hit.toAddL),
        Integer.valueOf(hit.zipL), Integer.valueOf(hit.zipR), hit.toLat, hit.toLon, hit.frLon, hit.frLat,
        hit.longArray, hit.latArray);

  }

  static Geo geocode(int streetnum, long tlid, int fraddr, int fraddl, int toaddr, int toaddl,
    int zipL, int zipR, float tolat, float tolong, float frlong, float frlat,
    float[] latArray, float[] longArray ){

    Distance distance = getDistance(tolat, tolong, frlong, frlat, latArray, longArray);
    int addrStart, addrEnd, zip;
    if(!isLeft(streetnum, fraddr, fraddl, toaddr, toaddl) && (fraddr%2 == streetnum%2)){
      addrStart = fraddr; addrEnd = toaddr; zip = zipR;
    }else{
      addrStart = fraddl; addrEnd = toaddl; zip = zipL;
    }

    int dec1 = addrEnd - streetnum, dec2 = addrEnd - addrStart;
    float rel = dec1*1f / dec2;
    if(rel == 1f){ //return start
      return new Geo(frlat, frlong, zip, tlid);
    }
    if(rel == 0f){ //return end
      return new Geo(tolat, tolong, zip, tlid);
    }
    //straight line estimate
//    float lat = frlat + (rel * distance.totalLat), lon = frlong + (rel * distance.totalLon);

    float tempEndLat = frlat + distance.totalLat, tempEndLon = frlong + distance.totalLon;
    float totalDist = getLineDistance(frlong, frlat, tempEndLon, tempEndLat);
    Geo ret = pointFromChainRatio(totalDist, rel, tolat, tolong, frlong, frlat, latArray, longArray);
    ret.zip = zip; ret.tlid = tlid;
    return ret;
  }

  private static Geo pointFromChainRatio(float totalLength, float ratio,
          float tolat, float tolong, float frlong, float frlat,
          float[] latArray, float[] longArray
          ){
    float lastLat = frlat, lastLon = frlong;
    boolean found = false;
    float totalRatio=0f, totalTravel=0f, travelTarget=ratio*totalLength;
//    ---For each lng/lat pair that isn't empty, calculate distance from last non empty pair.
//    ---Is the totalRatio + ratio of this segment < the ratio we are looking for?
//    ---If yes, add the ratio of this distance to the total ratio
//    ---If no, trim the ratio so that it applies only to this segment and return calculated point.
    float thisLen, thisRatio, useStartLon=frlong, useStartLat=frlat, useEndLon=tolong, useEndLat=tolat, useRatio=ratio;

    for (int i=0; i<latArray.length; i++){
        if(latArray[i] != 0 && !found){
              thisLen = getLineDistance(longArray[i], latArray[i], lastLon, lastLat);
              thisRatio = thisLen / totalLength;
              if(thisLen + totalTravel >= travelTarget){
                useStartLat = lastLat; useStartLon = lastLon;
                useEndLat = latArray[i]; useEndLon = longArray[i];
                useRatio = thisRatio; found = true;
              }else{
                totalRatio = totalRatio + thisRatio;
                lastLon = longArray[i]; lastLat = latArray[i];
                totalTravel = totalTravel + thisLen;
              }
         }
    }
    float rel = (ratio - totalRatio)/ useRatio;
    float lonDist = useEndLon - useStartLon;
    float latDist = useEndLat - useStartLat;
    Geo ret = new Geo();
    ret.lat = useEndLat - (rel * latDist);
    ret.lon = useEndLon - (rel * lonDist);
    return ret;
  }

  private static float getLineDistance(float x1, float y1, float x2, float y2){
    double dx = x2 - x1, dy = y2-y1;
    return (float)Math.sqrt(dx*dx + dy*dy);
  }

  private static boolean isLeft(int streetnum, int fraddr, int fraddl, int toaddr, int toaddl){
    return fraddr == -1 || (!between(streetnum, fraddr, toaddr) && !between(streetnum, toaddr, fraddr));
  }

  private static boolean between(int num, int start, int end){
    return num >= start && num <= end;
  }

  private static Distance getDistance(float tolat, float tolong, float frlong, float frlat,
     float[] latArray, float[] longArray){
    Distance ret = new Distance();
    float lastLat = frlat, lastLon = frlong;
    for(int i=0; i<latArray.length; i++){
        if (latArray[i] != 0f){
            ret.totalLat+=Math.abs(latArray[i] - lastLat);
            ret.totalLon+=Math.abs(longArray[i] - lastLon);
            lastLat=latArray[i];
            lastLon = longArray[i];
        }
    }

    ret.totalLat += Math.abs(tolat - lastLat);
    ret.totalLon += Math.abs(tolong - lastLon);

    return ret;
  }

}
