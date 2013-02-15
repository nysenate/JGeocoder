package net.sourceforge.jgeocoder.test;

import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.sourceforge.jgeocoder.CommonUtils;
import net.sourceforge.jgeocoder.JGeocodeAddress;
import net.sourceforge.jgeocoder.tiger.JGeocoder;
import net.sourceforge.jgeocoder.tiger.JGeocoderConfig;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
/**
 * @author Bobby Zheng
 *
 */
class BobbysTest{
  public static void main(String[] args) throws ClassNotFoundException {
    JGeocoderConfig config = new JGeocoderConfig();
    config.setJgeocoderDataHome("/home/graylin/projects/jgeocoder/jgeocoder/src/data");

    MysqlDataSource db = new MysqlDataSource();
    for(String arg : args) {
        System.out.println(arg);
    }
    // Section dbconfig = config.get("database");
    db.setServerName(args[0]);
    db.setUser(args[1]);
    db.setPassword(args[2]);
    db.setDatabaseName(args[3]);

    config.setTigerDataSource(db);
    JGeocoder jg = new JGeocoder(config);
    long start = System.currentTimeMillis();
    //for(int i =0; i<100; i++){
      JGeocodeAddress addr = jg.geocodeAddress("30 Tech Valley Dr,East Greenbush, New York 12061 ");
      System.out.println(ToStringBuilder.reflectionToString(addr, ToStringStyle.MULTI_LINE_STYLE));
      JGeocodeAddress addr2 = jg.geocodeAddress("203 Hoadley Hill Rd, windsor ny 13865");

      System.out.println(ToStringBuilder.reflectionToString(addr2, ToStringStyle.MULTI_LINE_STYLE));
    //}
    CommonUtils.printElapsed(start, TimeUnit.SECONDS);

    jg.cleanup();

  }
}