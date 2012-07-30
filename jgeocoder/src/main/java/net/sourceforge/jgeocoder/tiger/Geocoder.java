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
        hit.lon1, hit.lat1, hit.lon2, hit.lat2, hit.lon3, hit.lat3, hit.lon4, hit.lat4, hit.lon5, hit.lat5,
        hit.lon6, hit.lat6, hit.lon7, hit.lat7, hit.lon8, hit.lat8, hit.lon9, hit.lat9, hit.lon10, hit.lat10);
  }
  
  static Geo geocode(int streetnum, long tlid, int fraddr, int fraddl, int toaddr, int toaddl, 
    int zipL, int zipR, float tolat, float tolong, float frlong, float frlat,  
    float long1, float lat1, float long2, float lat2, float long3, float lat3, float long4, float lat4,
    float long5, float lat5, float long6, float lat6, float long7, float lat7, float long8, float lat8,
    float long9, float lat9, float long10, float lat10 ){
    
    Distance distance = getDistance(tolat, tolong, frlong, frlat, long1, lat1, long2, lat2, long3, lat3, long4, lat4, long5, lat5, long6, lat6, long7, lat7, long8, lat8, long9, lat9, long10, lat10);
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
    Geo ret = pointFromChainRatio(totalDist, rel, tolat, tolong, frlong, frlat, long1, lat1, long2, lat2, long3, lat3, long4, lat4, long5, lat5, long6, lat6, long7, lat7, long8, lat8, long9, lat9, long10, lat10);
    ret.zip = zip; ret.tlid = tlid;
    return ret;
  }
  
  private static Geo pointFromChainRatio(float totalLength, float ratio,  
          float tolat, float tolong, float frlong, float frlat,  
          float long1, float lat1, float long2, float lat2, float long3, float lat3, float long4, float lat4,
          float long5, float lat5, float long6, float lat6, float long7, float lat7, float long8, float lat8,
          float long9, float lat9, float long10, float lat10
          ){
    float lastLat = frlat, lastLon = frlong;
    boolean found = false;
    float totalRatio=0f, totalTravel=0f, travelTarget=ratio*totalLength;
//    ---For each lng/lat pair that isn't empty, calculate distance from last non empty pair.
//    ---Is the totalRatio + ratio of this segment < the ratio we are looking for?
//    ---If yes, add the ratio of this distance to the total ratio
//    ---If no, trim the ratio so that it applies only to this segment and return calculated point.
    float thisLen, thisRatio, useStartLon=frlong, useStartLat=frlat, useEndLon=tolong, useEndLat=tolat, useRatio=ratio;

    // 10 freaking times of this
    if(lat1 != 0 && !found){
      thisLen = getLineDistance(long1, lat1, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat1; useEndLon = long1;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long1; lastLat = lat1;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat2 != 0 && !found){
      thisLen = getLineDistance(long2, lat2, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat2; useEndLon = long2;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long2; lastLat = lat2;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat3 != 0 && !found){
      thisLen = getLineDistance(long3, lat3, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat3; useEndLon = long3;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long3; lastLat = lat3;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat4 != 0 && !found){
      thisLen = getLineDistance(long4, lat4, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat4; useEndLon = long4;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long4; lastLat = lat4;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat5 != 0 && !found){
      thisLen = getLineDistance(long5, lat5, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat5; useEndLon = long5;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long5; lastLat = lat5;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat6 != 0 && !found){
      thisLen = getLineDistance(long6, lat6, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat6; useEndLon = long6;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long6; lastLat = lat6;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat7 != 0 && !found){
      thisLen = getLineDistance(long7, lat7, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat7; useEndLon = long7;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long7; lastLat = lat7;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat8 != 0 && !found){
      thisLen = getLineDistance(long8, lat8, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat8; useEndLon = long8;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long8; lastLat = lat8;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat9 != 0 && !found){
      thisLen = getLineDistance(long9, lat9, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat9; useEndLon = long9;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long9; lastLat = lat9;
        totalTravel = totalTravel + thisLen;
      }
    }
    
    if(lat10 != 0 && !found){
      thisLen = getLineDistance(long10, lat10, lastLon, lastLat);
      thisRatio = thisLen / totalLength;
      if(thisLen + totalTravel >= travelTarget){
        useStartLat = lastLat; useStartLon = lastLon;
        useEndLat = lat10; useEndLon = long10;
        useRatio = thisRatio; found = true;
      }else{
        totalRatio = totalRatio + thisRatio;
        lastLon = long10; lastLat = lat10;
        totalTravel = totalTravel + thisLen;
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
      float long1, float lat1, float long2, float lat2, float long3, float lat3, float long4, float lat4,
      float long5, float lat5, float long6, float lat6, float long7, float lat7, float long8, float lat8,
      float long9, float lat9, float long10, float lat10){
    Distance ret = new Distance();
    float lastLat = frlat, lastLon = frlong;
    if(lat1 != 0f){
      ret.totalLat += Math.abs(lat1 - lastLat);
      ret.totalLon += Math.abs(long1 - lastLon);
      lastLat = lat1;
      lastLon = long1;
    }
    if(lat2 != 0f){
      ret.totalLat += Math.abs(lat2 - lastLat);
      ret.totalLon += Math.abs(long2 - lastLon);
      lastLat = lat2;
      lastLon = long2;
    }
    
    if(lat3 != 0f){
      ret.totalLat += Math.abs(lat3 - lastLat);
      ret.totalLon += Math.abs(long3 - lastLon);
      lastLat = lat3;
      lastLon = long3;
    }
    
    if(lat4 != 0f){
      ret.totalLat += Math.abs(lat4 - lastLat);
      ret.totalLon += Math.abs(long4 - lastLon);
      lastLat = lat4;
      lastLon = long4;
    }
    
    if(lat5 != 0f){
      ret.totalLat += Math.abs(lat5 - lastLat);
      ret.totalLon += Math.abs(long5 - lastLon);
      lastLat = lat5;
      lastLon = long5;
    }
    
    if(lat6 != 0f){
      ret.totalLat += Math.abs(lat6 - lastLat);
      ret.totalLon += Math.abs(long6 - lastLon);
      lastLat = lat6;
      lastLon = long6;
    }
    
    if(lat7 != 0f){
      ret.totalLat += Math.abs(lat7 - lastLat);
      ret.totalLon += Math.abs(long7 - lastLon);
      lastLat = lat7;
      lastLon = long7;
    }
    
    if(lat8 != 0f){
      ret.totalLat += Math.abs(lat8 - lastLat);
      ret.totalLon += Math.abs(long8 - lastLon);
      lastLat = lat8;
      lastLon = long8;
    }
    
    if(lat9 != 0f){
      ret.totalLat += Math.abs(lat9 - lastLat);
      ret.totalLon += Math.abs(long9 - lastLon);
      lastLat = lat9;
      lastLon = long9;
    }
    
    if(lat10 != 0f){
      ret.totalLat += Math.abs(lat10 - lastLat);
      ret.totalLon += Math.abs(long10 - lastLon);
      lastLat = lat10;
      lastLon = long10;
    }
    
    ret.totalLat += Math.abs(tolat - lastLat);
    ret.totalLon += Math.abs(tolong - lastLon);
    
    return ret;
  }
    
}
