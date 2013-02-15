package net.sourceforge.jgeocoder.tiger;

import static net.sourceforge.jgeocoder.AddressComponent.POSTDIR;
import static net.sourceforge.jgeocoder.AddressComponent.PREDIR;
import static net.sourceforge.jgeocoder.AddressComponent.TYPE;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import net.sourceforge.jgeocoder.AddressComponent;
import net.sourceforge.jgeocoder.us.AddressParser;
import net.sourceforge.jgeocoder.us.AddressStandardizer;


import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.h2.jdbcx.JdbcDataSource;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
class TigerLineHit{
    long tlid;
    String streetNum;
    String aridL;
    String aridR;
    String linearid;
    String fullname;
    
    // need address parser to split fullname into different parts
    String fedirp; //feature direction prefix (NORTH adams ave.)
    String fetype; //feature type (crestview BOULEVARD)
    String fedirs; //feature direction suffix  (providence street NW)
    
    String frAddR;
    String frAddL;
    String toAddR;
    String toAddL;
    String zipL;
    String zipR;
    String edgemtfcc;
    String parityL;
    String parityR;
    String plus4L;
    String plus4R;
    String lfromtyp;    
    String ltotyp;
    String rfromtyp;
    String rtotyp;
    String offsetl;
    String offsetr;
    float toLat;
    float toLon;
    float frLat;
    float frLon;
    float[] latArray;
    float[] longArray;
    
    
  
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}

class TigerQueryFailedException extends Exception{
  private static final long serialVersionUID = 1L;
  public TigerQueryFailedException(String message) {
    super(message);
  }
  
  public TigerQueryFailedException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public TigerQueryFailedException(Throwable cause) {
    super(cause);
  }
}

class TigerLineDao{
  
  
  private static final String TIGER_QUERY = "SELECT *"+
		  /*"select t.tlid, t.fraddr, t.fraddl, t.toaddr, t.toaddl,"+
" t.zipL, t.zipR, " +
//" t.tolat, t.tolong, t.frlong, t.frlat,"+
"t.bbox, t.latlongpairs, t.fullname"+
		  " t.fedirp, t.fetype, t.fedirs " +*/
		  " from TIGER_{0} t where t.fename = ? and (t.zipL = ? or t.zipR = ?) and"+
          "(" + 
		  "(t.fraddL <= ? and t.toaddL >= ?) or (t.fraddL >= ? and t.toaddL <= ?) "+
          "    or (t.fraddR <= ? and t.toaddR >= ?) or (t.fraddR >= ? and t.toaddR <= ?))";
  private DataSource _tigerDs;
  public TigerLineDao(DataSource tigerDs){
    _tigerDs = tigerDs;
  }
  
  private static final Pattern DIGIT = Pattern.compile("^.*?(\\d+).*$");
  private static String getStreetNum(String streetNum){
    Matcher m = DIGIT.matcher(streetNum);
    if(!m.matches()){
      throw new RuntimeException("Cannot find valid street number");
    }
    return m.group(1);
  }
  
  public static TigerLineHit findBest(Map<AddressComponent, String> normalizedAddr, List<TigerLineHit> hits){
    if(hits.size() == 0){
      return null;
    }
    if(hits.size() == 1){ //unique hit 
      return hits.get(0);
    }
    TigerLineHit best = null;
    int bestScore = Integer.MIN_VALUE;
    for(TigerLineHit hit : hits){
      int score = 0;
      if(ObjectUtils.equals(hit.fedirp, normalizedAddr.get(AddressComponent.PREDIR))){
        score++;
      }
      if(ObjectUtils.equals(hit.fedirs, normalizedAddr.get(AddressComponent.POSTDIR))){
        score++;
      }
      if(ObjectUtils.equals(hit.fetype, normalizedAddr.get(AddressComponent.TYPE))){
        score+=3; //boost type match score
      }
      if(score > bestScore){
        best = hit;
        bestScore = score;
      }
    }
    return best;
  }
  /**
   * Searches the tiger/line database using ZIP, NUMBER, and STREET
   * @param normalizedAddr
   * @return search hit, rank and return the best match if encounter multiple hits
   * @throws TigerQueryFailedException 
   */
  public TigerLineHit getTigerLineHit(Map<AddressComponent, String> normalizedAddr) throws TigerQueryFailedException{
    return findBest(normalizedAddr, getTigerLineHits(normalizedAddr));
  }
  
  /**
   * Searches the tiger/line database using ZIP, NUMBER, and STREET
   * @param normalizedAddr
   * @return 0 or more search hits 
   * @throws TigerQueryFailedException 
   */
  public List<TigerLineHit> getTigerLineHits(Map<AddressComponent, String> normalizedAddr) throws TigerQueryFailedException{
    List<TigerLineHit> ret = new ArrayList<TigerLineHit>();
    if(normalizedAddr.get(AddressComponent.ZIP) == null 
        || normalizedAddr.get(AddressComponent.NUMBER) == null
        || normalizedAddr.get(AddressComponent.STREET) == null){
      return ret; //nothing to do if anything of these things are missing
    }
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    
    String streetNum = getStreetNum(normalizedAddr.get(AddressComponent.NUMBER));
    String zip =  normalizedAddr.get(AddressComponent.ZIP);
    
    try {
        
      if (_tigerDs instanceof JdbcDataSource) {
        JdbcDataSource ds = (JdbcDataSource) _tigerDs;
        //ds.setUser("Sa");
        //ds.setPassword("");

        conn = ds.getConnection();  
        //DriverManager.registerDriver((Driver)getClass().getClassLoader().loadClass("org.h2.Driver").newInstance());

        //conn = DriverManager.getConnection("jdbc:h2:C:\\Users\\Bobby\\tiger_ny;LOG=0;UNDO_LOG=0");//;IFEXISTS=TRUE");//, "Sa","");
      }else{
        conn = _tigerDs.getConnection();
        
      }
     
      ps = conn.prepareStatement(generateSelectQuery(normalizedAddr.get(AddressComponent.STATE)));
      int i=1;
      ps.setString(i++, normalizedAddr.get(AddressComponent.STREET));
      ps.setString(i++, zip);
      ps.setString(i++, zip);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      rs = ps.executeQuery();
      while(rs.next()){
        TigerLineHit hit = new TigerLineHit();
        hit.streetNum = streetNum;
        hit.tlid = rs.getLong("tlid");
        hit.frAddL = rs.getString("fraddl");
        hit.frAddR = rs.getString("fraddr");
        hit.toAddL = rs.getString("toaddl");
        hit.toAddR = rs.getString("toaddr");
        hit.zipL = rs.getString("zipL");
        hit.zipR = rs.getString("zipR");
        String latlongwhole = rs.getString("latlongpairs");
        String[] latlongpoints = latlongwhole.split(":");
        hit.latArray = new float[latlongpoints.length];
        hit.longArray = new float[latlongpoints.length];
        for (int j=0; j<latlongpoints.length; j++){
            String pair = latlongpoints[j];
            pair = pair.replace("(", "");
            pair = pair.replace(")", "");
            String[] splitpair = pair.split(";");
            hit.latArray[j]=new Float(splitpair[0]);
            hit.longArray[j]=new Float(splitpair[1]);
            
        }
        String firstpair = latlongpoints[0].replace("(","").replace(")", "");
        String lastpair = latlongpoints[latlongpoints.length-1].replace("(","").replace(")", "");
        String[] firstpairPieces = firstpair.split(";");
        String[] lastpairPieces = lastpair.split(";");
        hit.frLon = new Float(firstpairPieces[0]);
        hit.frLat = new Float(firstpairPieces[1]);
        hit.toLon = new Float(lastpairPieces[0]);
        hit.toLat = new Float(lastpairPieces[1]);
        hit.fedirp = rs.getString("fedirp");
        hit.fedirs = rs.getString("fedirs");
        hit.fetype = rs.getString("fetype");
        ret.add(hit);
      }
    } catch (Exception e) {
      throw new TigerQueryFailedException(e.getMessage(), e);
    }finally{
      DbUtils.closeQuietly(conn);
      DbUtils.closeQuietly(rs);
      DbUtils.closeQuietly(ps);
    }
    return ret;
  }
  
  private static String generateSelectQuery(String state){
    if(state==null || state.length() != 2){
      throw new IllegalArgumentException(state+" is not a valid 2 letter state code");
    }
    return MessageFormat.format(TIGER_QUERY, state);
  }
}
