package net.sourceforge.jgeocoder;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
public class JGeocodeAddress implements Serializable{

  private static final long serialVersionUID = 20080613L;
  private Map<AddressComponent, String> _parsedAddr = null;
  private Map<AddressComponent, String> _normalizedAddr = null;
  private Map<AddressComponent, String> _geocodedAddr = null;
  private GeocodeAcuracy _acuracy = GeocodeAcuracy.UNKNOWN;
  public Map<AddressComponent, String> getParsedAddr() {
    return _parsedAddr;
  }
  public void setParsedAddr(Map<AddressComponent, String> parsedAddr) {
    _parsedAddr = parsedAddr;
  }
  public Map<AddressComponent, String> getNormalizedAddr() {
    return _normalizedAddr;
  }
  public void setNormalizedAddr(Map<AddressComponent, String> normalizedAddr) {
    _normalizedAddr = normalizedAddr;
  }
  public Map<AddressComponent, String> getGeocodedAddr() {
    return _geocodedAddr;
  }
  public void setGeocodedAddr(Map<AddressComponent, String> geocodedAddr) {
    _geocodedAddr = geocodedAddr;
  }
  public GeocodeAcuracy getAcuracy() {
    return _acuracy;
  }
  public void setAcuracy(GeocodeAcuracy acuracy) {
    _acuracy = acuracy;
  }
  
  public String toStringMultiLine(){
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }
  
  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}