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
 * blah blah
 * another revision?
 * @author jliang
 *
 */
class JaysTest{
  public static void main(String[] args) {
    JGeocoderConfig config = new JGeocoderConfig();
    config.setJgeocoderDataHome("C:\\jgeocoder\\data");
    config.setTigerDataSource(H2DbDataSourceFactory.getH2DbDataSource("jdbc:h2:C:\\jgeocoder\\tiger\\tiger;LOG=0;UNDO_LOG=0"));
    JGeocoder jg = new JGeocoder(config);
    long start = System.currentTimeMillis();
//    for(int i =0; i<100; i++){
      JGeocodeAddress addr = jg.geocodeAddress("lazaros pizza house 1743 south st philadelphia pa 19146");
      System.out.println(ToStringBuilder.reflectionToString(addr, ToStringStyle.MULTI_LINE_STYLE));
//    }
    CommonUtils.printElapsed(start, TimeUnit.SECONDS);
    
    jg.cleanup();
  }
}