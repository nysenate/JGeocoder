package net.sourceforge.jgeocoder.us;
import static net.sourceforge.jgeocoder.us.RegexLibrary.ADDR_UNIT;
import static net.sourceforge.jgeocoder.us.RegexLibrary.DIRECTIONS;
import static net.sourceforge.jgeocoder.us.RegexLibrary.ORDINAL_ALL;
import static net.sourceforge.jgeocoder.us.RegexLibrary.STREET_DESIGNATOR;
import static net.sourceforge.jgeocoder.us.RegexLibrary.TXT_NUM_0_9;
import static net.sourceforge.jgeocoder.us.RegexLibrary.TXT_NUM_10_19;
import static net.sourceforge.jgeocoder.us.RegexLibrary.TXT_ORDINAL_0_19;
import static net.sourceforge.jgeocoder.us.RegexLibrary.US_STATES;
import static net.sourceforge.jgeocoder.us.Utils.compile;
import net.sourceforge.jgeocoder.us.Utils.NamedGroupPattern;
/**
 * TODO javadocs me
 * @author jliang
 *
 */
class AddressRegexLibrary{
  
  private static final String NUMBER =
    "(?:\\p{Alpha})?\\d+(?:[- ][\\p{Alpha}&&[^NSEW]]" +
    "(?!\\s+(?:st|street|ave|aven|avenu|avenue|blvd|boulv|boulevard|boulv|plz|plaza|plza)))?" +
    "|\\d+-?\\d*(?:-?\\p{Alpha})?|"+TXT_NUM_0_9+"|" +TXT_NUM_10_19; 
  
  private static final String FRACTION = "\\d+\\/\\d+";
  
  private static final String LINE1A = 
    "(?P<street>"+DIRECTIONS+")\\W+" + 
    "(?P<type>"+STREET_DESIGNATOR+")\\b";
  
  private static final String LINE1B = 
    "(?:(?P<predir>"+DIRECTIONS+")\\W+)?" +
    "(?:" +
      "(?P<street>[^,]+)" +
      "(?:[^\\w,]+(?P<type>"+STREET_DESIGNATOR+")\\b)" +
      "(?:[^\\w,]+(?P<postdir>"+DIRECTIONS+")\\b)?" +
     "|" +
       "(?P<street>[^,]*\\d)" +
       "(?:(?P<postdir>"+DIRECTIONS+")\\b)" +
     "|" +
       "(?P<street>[^,]+?)" +
       "(?:[^\\w,]+(?P<type>"+STREET_DESIGNATOR+")\\b)?" +
       "(?:[^\\w,]+(?P<postdir>"+DIRECTIONS+")\\b)?" +       
    ")";
  
  private static final String LINE1A2 = 
    "(?P<street2>"+DIRECTIONS+")\\W+" + 
    "(?P<type2>"+STREET_DESIGNATOR+")\\b";
  
  private static final String LINE1B2 = 
    "(?:(?P<predir2>"+DIRECTIONS+")\\W+)?" +
    "(?:" +
      "(?P<street2>[^,]+)" +
      "(?:[^\\w,]+(?P<type2>"+STREET_DESIGNATOR+")\\b)" +
      "(?:[^\\w,]+(?P<postdir2>"+DIRECTIONS+")\\b)?" +
     "|" +
       "(?P<street2>[^,]*\\d)" +
       "(?:(?P<postdir2>"+DIRECTIONS+")\\b)" +
     "|" +
       "(?P<street2>[^,]+?)" +
       "(?:[^\\w,]+(?P<type2>"+STREET_DESIGNATOR+")\\b)?" +
       "(?:[^\\w,]+(?P<postdir2>"+DIRECTIONS+")\\b)?" +       
    ")";
  
  private static final String LINE1 =
    "(?P<number>(?:" + NUMBER + ")(?:\\W+"+FRACTION+")?)\\W+" + 
    "(?:" + LINE1B + "|" + LINE1A + ")";
  
  //A, 2A, 22, A2, 2-a, 2/a, etc...
  private static final String UNIT_NUMBER = 
    "(?:\\b\\p{Alpha}{1}\\s+|\\p{Alpha}*[-/]?)?" +
    "(?:\\d+|\\b\\p{Alpha}\\b(?=\\s|$))" +
    "(?:[ ]*\\p{Alpha}\\b|-\\w+)?";
  private static final String ZIP = "\\d{5}(?:[- ]\\d{3,4})?";
  private static final String NOT_STATE_OR_ZIP = "(?![^,]*\\W+(?:\\b(?:"+US_STATES+")\\b(?:\\W*$|(?:"+ZIP+")\\W*$))|(?:\\b(?:"+ZIP+")\\b\\W*$))";
  private static final String LINE2A = "(?:"+ADDR_UNIT+")[s]?\\W*?(?:"+UNIT_NUMBER+")";
  public static final String LINE2A_GROUPED = "("+ADDR_UNIT+")[s]?\\W*?("+UNIT_NUMBER+")";
  private static final String LINE2B = "(?:(?:"+TXT_ORDINAL_0_19+"|"+ORDINAL_ALL+")\\W*(?:"+ADDR_UNIT+")[s]?)";
  private static final String LINE2 = "(?:(?P<line2>"+LINE2A+"|"+LINE2B+"|[^,]*?"+NOT_STATE_OR_ZIP+")\\W+)??";
  
  private static final String LASTLINE = 
    "(?:" +
      "(?P<city>[^\\d,]+?)\\W+" +  //city                                              
      "\\b(?P<state>(?:"+US_STATES+")\\b)?\\W*" + //state                                          
    ")?" +
    "(?P<zip>"+ZIP+")?";      //zip

  private static final String ADDR_NAME =  "(?:(?P<name>[^,]+)\\W+)??"; 
  
  private static final String STREET_ADDRESS = 
   ADDR_NAME + LINE1 + "(?P<tlid>\\W+)"+ LINE2 + LASTLINE +"\\W*"; //the group name is a hack
  
  private static final String CORNER = "(?:\\band\\b|\\bat\\b|&|\\@)";

  private static final String INTERSECTION = ADDR_NAME +
  "(?:" + LINE1A + "|" + LINE1B + ")" + "\\W*\\s+" + CORNER + "\\s+" +
  "(?:" + LINE1A2 + "|" + LINE1B2 + ")" + "\\W+" + LASTLINE +"\\W*";
  
  public static final NamedGroupPattern P_CSZ = compile("(?i:"+LASTLINE+")");
  public static final NamedGroupPattern P_STREET_ADDRESS = compile("(?i:"+STREET_ADDRESS+")");
  public static final NamedGroupPattern P_INTERSECTION = compile("(?i:"+INTERSECTION+")");
  public static final NamedGroupPattern P_CORNER = compile("(?i:"+CORNER+")");
  

}