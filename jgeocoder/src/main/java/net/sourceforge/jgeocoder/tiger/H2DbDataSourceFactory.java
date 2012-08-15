package net.sourceforge.jgeocoder.tiger;

import javax.sql.DataSource;

import net.sourceforge.jgeocoder.CommonUtils;

import org.h2.jdbcx.JdbcDataSource;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
public class H2DbDataSourceFactory{
  private H2DbDataSourceFactory(){}
  private static String _tigerUrl =
    CommonUtils.nvl(System.getProperty("jgeocoder.tiger.url"), "jdbc:h2:C:/Users/Bobby/tiger_ny;LOG=0;UNDO_LOG=0");

  public static DataSource getH2DbDataSource(){
    return getH2DbDataSource(_tigerUrl);
  }
  
  public static DataSource getH2DbDataSource(String tigerUrl){
    JdbcDataSource ds =  new JdbcDataSource();
    ds.setURL(tigerUrl);
    return ds;
  }
}