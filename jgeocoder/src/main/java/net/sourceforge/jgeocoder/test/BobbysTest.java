package net.sourceforge.jgeocoder.test;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import net.sourceforge.jgeocoder.CommonUtils;
import net.sourceforge.jgeocoder.JGeocodeAddress;
import net.sourceforge.jgeocoder.tiger.H2DbDataSourceFactory;
import net.sourceforge.jgeocoder.tiger.JGeocoder;
import net.sourceforge.jgeocoder.tiger.JGeocoderConfig;
/**
 * @author Bobby Zheng
 *
 */
class BobbysTest{
  public static void main(String[] args) {
    JGeocoderConfig config = new JGeocoderConfig();
    config.setJgeocoderDataHome("C:/Users/Bobby/Documents/jgeocoder/jgeocoder/src/data");
    config.setTigerDataSource(H2DbDataSourceFactory.getH2DbDataSource("jdbc:h2:C:\\Users\\Bobby\\tiger_ny;LOG=0;UNDO_LOG=0"));
    JGeocoder jg = new JGeocoder(config);
    long start = System.currentTimeMillis();
//    for(int i =0; i<100; i++){
      JGeocodeAddress addr = jg.geocodeAddress("30 Tech Valley Dr,East Greenbush, New York 12061 ");
      System.out.println(ToStringBuilder.reflectionToString(addr, ToStringStyle.MULTI_LINE_STYLE));
      JGeocodeAddress addr2 = jg.geocodeAddress("203 Hoadley Hill Rd, windsor ny 13865");
      //JGeocodeAddress addr2 = jg.geocodeAddress("203 Hoadley Hill Rd 13865");

      System.out.println(ToStringBuilder.reflectionToString(addr2, ToStringStyle.MULTI_LINE_STYLE));
//    }
    CommonUtils.printElapsed(start, TimeUnit.SECONDS);
    
    jg.cleanup();
  }
}