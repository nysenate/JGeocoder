package net.sourceforge.jgeocoder.tiger;

import java.io.Serializable;

import javax.sql.DataSource;

import net.sourceforge.jgeocoder.CommonUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
public class JGeocoderConfig implements Serializable{
  private static final long serialVersionUID = 20080604L;
  public static final JGeocoderConfig DEFAULT = new JGeocoderConfig();
  private String _jgeocoderDataHome = 
    CommonUtils.nvl(System.getProperty("jgeocoder.data.home"), "/jgeocoder/data");

  private DataSource _tigerDataSource = null; 
   
  private long _berkeleyDbCacheSize = -1;
  private int _berkeleyDbCachePercent = -1;
  
  /**
   * get the {@link DataSource} to the tiger/line address database
   * @return
   */
  public synchronized DataSource getTigerDataSource() {
    if(_tigerDataSource == null){
      _tigerDataSource = H2DbDataSourceFactory.getH2DbDataSource();
    }
    return _tigerDataSource;
  }
  
  public synchronized void setTigerDataSource(DataSource tigerDataSource) {
    _tigerDataSource = tigerDataSource;
  }
  
  /**
   *  <p>By default, JE sets its cache size proportionally to the JVM
     memory. This formula is used:</p>
    
     <blockquote><pre>je.maxMemoryPercent *  JVM maximum memory
     </pre></blockquote>
    
     <p>where JVM maximum memory is specified by the JVM -Xmx flag.
     setCachePercent() specifies the percentage used and is equivalent to
     setting the je.maxMemoryPercent property in the je.properties file.</p>
    
     <p>Calling setCacheSize() with a non-zero value overrides the percentage
     based calculation and sets the cache size explicitly.</p>
    
     <p>Note that the log buffer cache may be cleared if the cache size is
     changed after the environment has been opened.</p>
    
     <p>If setSharedCache(true) is called, setCacheSize and setCachePercent
     specify the total size of the shared cache, and changing these
     parameters will change the size of the shared cache.</p>
   * @param berkeleyDbCachePercent berkeleyDb default will be used if set to negative
   */
  public int getBerkeleyDbCachePercent() {
    return _berkeleyDbCachePercent;
  }
  
  public void setBerkeleyDbCachePercent(int berkeleyDbCachePercent) {
    _berkeleyDbCachePercent = berkeleyDbCachePercent;
  }
  /**
   * see http://www.oracle.com/technology/documentation/berkeley-db/je/java/com/sleepycat/je/EnvironmentMutableConfig.html#setCachePercent(int)
   * @return
   */
  public long getBerkeleyDbCacheSize() {
    return _berkeleyDbCacheSize;
  }
  /**
   * Configures the memory available to the database system, in bytes.
 <p>Equivalent to setting the je.maxMemory property in the je.properties
 file. The system will evict database objects when it comes within a
 prescribed margin of the limit.</p>

 <p>By default, JE sets the cache size to:</p>

 <pre><blockquote>je.maxMemoryPercent *  JVM maximum memory
 </blockquote></pre>

 <p>where JVM maximum memory is specified by the JVM -Xmx flag. However,
 calling setCacheSize() with a non-zero value overrides the percentage
 based calculation and sets the cache size explicitly.</p>

 <p>Note that the cache does not include transient objects created by the
 JE library, such as cursors, locks and transactions.</p>

 <p>Note that the log buffer cache may be cleared if the cache size is
 changed after the environment has been opened.</p>

 <p>If setSharedCache(true) is called, setCacheSize and setCachePercent
 specify the total size of the shared cache, and changing these
 parameters will change the size of the shared cache.</p>
 @param berkeleyDbCacheSize berkeleyDb default will be used if set to negative
   */
  public void setBerkeleyDbCacheSize(long berkeleyDbCacheSize) {
    _berkeleyDbCacheSize = berkeleyDbCacheSize;
  }
  
  public String getJgeocoderDataHome() {
    return _jgeocoderDataHome;
  }
  public void setJgeocoderDataHome(String jgeocoderDataHome) {
    _jgeocoderDataHome = jgeocoderDataHome;
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