package net.sourceforge.jgeocoder.tiger;

import java.sql.Connection;
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
  
  
  private static final String TIGER_QUERY = "select t.tlid, t.fraddr, t.fraddl, t.toaddr, t.toaddl,"+ 
" t.zipL, t.zipR, t.tolat, t.tolong, t.frlong, t.frlat,"+ 
          "t.lat1, t.long1, t.lat2, t.long2, t.lat3, t.long3, t.lat4, t.long4, t.lat5, " +
          "t.long5, t.lat6, t.long6, t.lat7, t.long7, t.lat8, t.long8, t.lat9, t.long9, " +
          "t.lat10, t.long10, t.lat11, t.long11, t.lat12, t.long12, t.lat13, t.long13, " +
          "t.lat14, t.long14, t.lat15, t.long15, t.lat16, t.long16, t.lat17, t.long17, " +
          "t.lat18, t.long18, t.lat19, t.long19, t.lat20, t.long20, t.lat21, t.long21, " +
          "t.lat22, t.long22, t.lat23, t.long23, t.lat24, t.long24, t.lat25, t.long25, " +
          "t.lat26, t.long26, t.lat27, t.long27, t.lat28, t.long28, t.lat29, t.long29, " +
          "t.lat30, t.long30, t.lat31, t.long31, t.lat32, t.long32, t.lat33, t.long33," +
          " t.lat34, t.long34, t.lat35, t.long35, t.lat36, t.long36, t.lat37, t.long37," +
          " t.lat38, t.long38, t.lat39, t.long39, t.lat40, t.long40, t.lat41, t.long41, " +
          "t.lat42, t.long42, t.lat43, t.long43, t.lat44, t.long44, t.lat45, t.long45," +
          " t.lat46, t.long46, t.lat47, t.long47, t.lat48, t.long48, t.lat49, t.long49," +
          " t.lat50, t.long50, t.lat51, t.long51, t.lat52, t.long52, t.lat53, t.long53, " +
          "t.lat54, t.long54, t.lat55, t.long55, t.lat56, t.long56, t.lat57, t.long57, " +
          "t.lat58, t.long58, t.lat59, t.long59, t.lat60, t.long60, t.lat61, t.long61," +
          " t.lat62, t.long62, t.lat63, t.long63, t.lat64, t.long64, t.lat65, t.long65, " +
          "t.lat66, t.long66, t.lat67, t.long67, t.lat68, t.long68, t.lat69, t.long69, " +
          "t.lat70, t.long70, t.lat71, t.long71, t.lat72, t.long72, t.lat73, t.long73," +
          "t.lat74, t.long74, t.lat75, t.long75, t.lat76, t.long76, t.lat77, t.long77, " +
          "t.lat78, t.long78, t.lat79, t.long79, t.lat80, t.long80, t.lat81, t.long81," +
          " t.lat82, t.long82, t.lat83, t.long83, t.lat84, t.long84, t.lat85, t.long85," +
          " t.lat86, t.long86, t.lat87, t.long87, t.lat88, t.long88, t.lat89, t.long89, " +
          "t.lat90, t.long90, t.lat91, t.long91, t.lat92, t.long92, t.lat93, t.long93, " +
          "t.lat94, t.long94, t.lat95, t.long95, t.lat96, t.long96, t.lat97, t.long97, " +
          "t.lat98, t.long98, t.lat99, t.long99, t.lat100, t.long100, t.lat101, t.long101, " +
          "t.lat102, t.long102, t.lat103, t.long103, t.lat104, t.long104, t.lat105, t.long105," +
          " t.lat106, t.long106, t.lat107, t.long107, t.lat108, t.long108, t.lat109, t.long109," +
          " t.lat110, t.long110, t.lat111, t.long111, t.lat112, t.long112, t.lat113, t.long113," +
          " t.lat114, t.long114, t.lat115, t.long115, t.lat116, t.long116, t.lat117, t.long117," +
          " t.lat118, t.long118, t.lat119, t.long119, t.lat120, t.long120, t.lat121, t.long121," +
          " t.lat122, t.long122, t.lat123, t.long123, t.lat124, t.long124, t.lat125, t.long125," +
          " t.lat126, t.long126, t.lat127, t.long127, t.lat128, t.long128, t.lat129, t.long129," +
          " t.lat130, t.long130, t.lat131, t.long131, t.lat132, t.long132, t.lat133, t.long133," +
          " t.lat134, t.long134, t.lat135, t.long135, t.lat136, t.long136, t.lat137, t.long137," +
          " t.lat138, t.long138, t.lat139, t.long139, t.lat140, t.long140, t.lat141, t.long141, t.lat142," +
          " t.long142, t.lat143, t.long143, t.lat144, t.long144, t.lat145, t.long145, t.lat146, t.long146," +
          " t.lat147, t.long147, t.lat148, t.long148, t.lat149, t.long149, t.lat150, t.long150," +
          " t.lat151, t.long151, t.lat152, t.long152, t.lat153, t.long153, t.lat154, t.long154," +
          "t.lat155, t.long155, t.lat156, t.long156, t.lat157, t.long157, t.lat158, t.long158, " +
          "t.lat159, t.long159, t.lat160, t.long160, t.lat161, t.long161, t.lat162, t.long162," +
          " t.lat163, t.long163, t.lat164, t.long164, t.lat165, t.long165, t.lat166, t.long166," +
          " t.lat167, t.long167, t.lat168, t.long168, t.lat169, t.long169, t.lat170, t.long170," +
          " t.lat171, t.long171, t.lat172, t.long172, t.lat173, t.long173, t.lat174, t.long174, " +
          "t.lat175, t.long175, t.lat176, t.long176, t.lat177, t.long177, t.lat178, t.long178," +
          " t.lat179, t.long179, t.lat180, t.long180, t.lat181, t.long181, t.lat182, t.long182, " +
          "t.lat183, t.long183, t.lat184, t.long184, t.lat185, t.long185, t.lat186, t.long186, " +
          "t.lat187, t.long187, t.lat188, t.long188, t.lat189, t.long189, t.lat190, t.long190, " +
          "t.lat191, t.long191, t.lat192, t.long192, t.lat193, t.long193, t.lat194, t.long194," +
          " t.lat195, t.long195, t.lat196, t.long196, t.lat197, t.long197, t.lat198, t.long198," +
          " t.lat199, t.long199, t.lat200, t.long200, t.lat201, t.long201, t.lat202, t.long202," +
          " t.lat203, t.long203, t.lat204, t.long204, t.lat205, t.long205, t.lat206, t.long206," +
          " t.lat207, t.long207, t.lat208, t.long208, t.lat209, t.long209, t.lat210, t.long210, " +
          "t.lat211, t.long211, t.lat212, t.long212, t.lat213, t.long213, t.lat214, t.long214," +
          " t.lat215, t.long215, t.lat216, t.long216, t.lat217, t.long217, t.lat218, t.long218," +
          " t.lat219, t.long219, t.lat220, t.long220, t.lat221, t.long221, t.lat222, t.long222, " +
          "t.lat223, t.long223, t.lat224, t.long224, t.lat225, t.long225, t.lat226, t.long226, " +
          "t.lat227, t.long227, t.lat228, t.long228, t.lat229, t.long229, t.lat230, t.long230, " +
          "t.lat231, t.long231, t.lat232, t.long232, t.lat233, t.long233, t.lat234, t.long234, " +
          "t.lat235, t.long235, t.lat236, t.long236, t.lat237, t.long237, t.lat238, t.long238, t.lat239, t.long239"+
          " t.fedirp, t.fetype, t.fedirs from TIGER_{0} t where t.fename = ? and "+
          "(" + 
          "       (t.fraddL <= ? and t.toaddL >= ?) or (t.fraddL >= ? and t.toaddL <= ?) "+
          "    or (t.fraddR <= ? and t.toaddR >= ?) or (t.fraddR >= ? and t.toaddR <= ?) "+
          ")" +  
          "  and (t.zipL = ? or t.zipR = ?)";
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
        conn = ds.getPooledConnection().getConnection();
      }else{
        conn = _tigerDs.getConnection();
      }
      ps = conn.prepareStatement(generateSelectQuery(normalizedAddr.get(AddressComponent.STATE)));
      int i=1;
      ps.setString(i++, normalizedAddr.get(AddressComponent.STREET));
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, streetNum);
      ps.setString(i++, zip);
      ps.setString(i++, zip);
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
        hit.toLat = rs.getFloat("tolat");
        hit.toLon = rs.getFloat("tolong");
        hit.frLat = rs.getFloat("frlat");
        hit.frLon = rs.getFloat("tolong");
        for(int j=0; j<240; j++){
            hit.latArray[i]=rs.getFloat("lat"+i);
        }
        hit.fedirp = rs.getString("fedirp");
        hit.fetype = rs.getString("fetype");
        hit.fedirs = rs.getString("fedirs");
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