import java.sql.*
import java.sql.Statement
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.sql.ResultSet
import java.sql.Driver
import java.sql.DriverManager
import java.sql.Connection
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import TigerDefinition
import TigerTable
import groovy.sql.Sql
//import net.sourceforge.jgeocoder
//import net.sourceforge.jgeocoder.us.AddressParser;

//import net.sourceforge.jgeocoder.us.AddressParser;
import org.apache.commons.lang.StringUtils

//def loader = this.class.classLoader.rootLoader
//loader.addURL(new File("C:/Users/Gateway/Documents/GitHub/JGeocoder/jgeocoder-import/jars/jgeocoder.jar").toURI().toURL())
// can't use traditional package import
//AliasResolver = Class.forName("net.sourceforge.jgeocoder.us.AliasResolver")


//@Grab(group='net.sourceforge.jgeocoder', module='jgeocoder', version='1.0-SNAPSHOT')

@Grab(group='commons-lang', module='commons-lang', version='2.4')
import net.sourceforge.jgeocoder.us.AddressParser;
import net.sourceforge.jgeocoder.AddressComponent;
import net.sourceforge.jgeocoder.us.AddressStandardizer;
//import net.sourceforge.jgeocoder.us.AliasResolver; //(Marking for possible delete)
import static net.sourceforge.jgeocoder.AddressComponent.POSTDIR;
import static net.sourceforge.jgeocoder.AddressComponent.PREDIR;
import static net.sourceforge.jgeocoder.AddressComponent.TYPE;
import static net.sourceforge.jgeocoder.AddressComponent.STREET;
@GrabConfig(systemClassLoader=true)
@Grab(group='com.h2database', module='h2', version='1.3.168')



def nvl(def val, def replacement){ val==null?replacement:val}

//return; //comment out to run

/**
 * the raw output has total=2627282 = 441M
 * with sort | uniq will cut it down to 2588852 = 433M
 */

Config config = new Config()
//return;  //comment out to run

DriverManager.registerDriver((Driver)getClass().getClassLoader().loadClass(config.driverClass).newInstance())
println(config.driverClass)
//this is calling a private method of DriverManager :D 
//because the maven classloader does not have the driver class
// connectiontype:drivername:dbpath;option1;option2;option3
Connection conn = DriverManager.getConnection("jdbc:h2:C:\\Users\\Gateway\\Documents\\GitHub\\JGeocoder\\local\\street_data.h2;LOG=0;UNDO_LOG=0;IGNORECASE=TRUE")//, "Sa","")
  //  new Properties(), getClass().getClassLoader())
println(conn)
    
Sql sql = new Sql(conn)
sql.execute("""DROP TABLE IF EXISTS TIGER_NY""");
sql.execute("""
    create table TIGER_NY ( TLID numeric not null, 
        ARIDL  varchar(255),
        ARIDR  varchar(255),
        LINEARID  varchar(255),
        FEDIRP  varchar(255)  ,
        FENAME  varchar(255)  ,
        FETYPE  varchar(255)  ,
        FEDIRS  varchar(255)  ,
        FULLNAME  varchar(255),
        FRADDL varchar(255), 
        TOADDL  varchar(255),
        FRADDR  varchar(255),
        TOADDR  varchar(255),
        ZIPL varchar(255),
        ZIPR varchar (255),
        EDGEMTFCC  varchar(255),
        PARITYL varchar(255), 
        PARITYR  varchar(255), 
        PLUS4L  varchar(255),
        PLUS4R  varchar(255),
        LFROMTYP  varchar(255),
        LTOTYP  varchar(255),
        RFROMTYP  varchar(255),
        RTOTYP  varchar(255),
        OFFSETL  varchar(255),
        OFFSETR  varchar(255),
        BBOX text,
        NUMPARTS text,
        SHAPETYPE numeric, 
        LATLONGPAIRS text);
        """)
        
        

PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(/error.csv/)))
int total = 0, error =0;
new File(/C:\Users\Gateway\Documents\GitHub\JGeocoder\jgeocoder-import\src\main\groovy\ConvertedData.csv/).eachLine{
  if((total++)%10000==0){
    println total
    sql.commit()
  }
  def values = it.split(',')
  values.eachWithIndex(){v, idx->
    if(StringUtils.isBlank(v) || v=='null'){ values[idx] = null}
  }
  values = new ArrayList<Object>(Arrays.asList(values))

  Map<AddressComponent, String> m
  try {
      if (values[9]!=null){      m  = AddressParser.parseAddress("103 "+values[4]+" "+values[9])}
      else { m  = AddressParser.parseAddress("103 "+values[4]+" "+values[10])}
      if( m == null) {
          println("error parsing "+values)
          
      } else {
        m = AddressStandardizer.normalizeParsedAddress(m)
        String fedirp
        if (m.get(PREDIR)==null){fedirp="NULL"}
        else{fedirp="'"+m.get(PREDIR)+"'"}
        String fedirs
        if (m.get(POSTDIR)=="null"){fedirs="NULL"}
        else{FEDIRS="'"+m.get(PREDIR)+"'"}
        String fetype
        if (m.get(TYPE)=="null"){fetype="NULL"}
        else{FETYPE="'"+m.get(TYPE)+"'"}
    
        values.addAll(Arrays.asList(m.get(STREET),fetype,fedirs));
        
        sql.execute("""
      insert into TIGER_NY( 
          TLID,
          ARIDL,
          ARIDR,
          LINEARID,
          FULLNAME,
          FRADDL, 
          TOADDL,
          FRADDR,
          TOADDR,
          ZIPL,
          ZIPR,
          EDGEMTFCC,
          PARITYL,
          PARITYR,
          PLUS4L,
          PLUS4R,
          LFROMTYP,
          LTOTYP,
          RFROMTYP,
          RTOTYP,
          OFFSETL,
          OFFSETR,
          BBOX,
          NUMPARTS,
          SHAPETYPE,
          LATLONGPAIRS,
          FEDIRP,
          FENAME,
          FETYPE,
          FEDIRS) 
      values (?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?, """+fedirp+""",?,?,?)
            """, values)
            }
    } catch (Exception e) {
        println("error parsing "+values)
        e.printStackTrace()
    }
}

println "total="+total
println "error="+error

println 'creating indicies on TIGER_NY'
sql.execute('create index IDX0_TIGER_NY on TIGER_NY(tlid)')
sql.execute('create index IDX1_TIGER_NY on TIGER_NY(fename)')
sql.execute('create index IDX2_TIGER_NY on TIGER_NY(fraddL)')
sql.execute('create index IDX3_TIGER_NY on TIGER_NY(toaddL)')
sql.execute('create index IDX4_TIGER_NY on TIGER_NY(fraddR)')
sql.execute('create index IDX5_TIGER_NY on TIGER_NY(toaddR)')
sql.execute('create index IDX6_TIGER_NY on TIGER_NY(zipL)')
sql.execute('create index IDX7_TIGER_NY on TIGER_NY(zipR)')

sql.close()
ps.close()