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
import org.apache.commons.lang.StringUtils

@Grab(group='commons-lang', module='commons-lang', version='2.4')

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
Connection conn = DriverManager.getConnection("jdbc:h2:C:\\Users\\Bobby\\tiger_ny;LOG=0;UNDO_LOG=0;IGNORECASE=TRUE")//, "Sa","")
  //  new Properties(), getClass().getClassLoader())
println(conn)
    
Sql sql = new Sql(conn)
sql.execute("""
    create table TIGER_NY ( TLID numeric not null, 
        ARIDL  varchar(22),
        ARIDR  varchar(22),
        LINEARID  varchar(22),
        FULLNAME  varchar(100),
        FRADDL varchar(12), 
        TOADDL  varchar(12),
        FRADDR  varchar(12),
        TOADDR  varchar(12),
        ZIPL varchar(5),
        ZIPR varchar (5),
        EDGEMTFCC  varchar(5),
        PARITYL varchar(1), 
        PARITYR  varchar(1), 
        PLUS4L  varchar(4),
        PLUS4R  varchar(4),
        LFROMTYP  varchar(1),
        LTOTYP  varchar(1),
        RFROMTYP  varchar(1),
        RTOTYP  varchar(1),
        OFFSETL  varchar(1),
        OFFSETR  varchar(1),
        BBOX text,
        NUMPARTS text,
        SHAPETYPE numeric, 
        LATLONGPAIRS text);
        """)
        
        

PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(/error.csv/)))
int total = 0, error =0;
new File(/tiger_main.csv/).eachLine{
  if((total++)%10000==0){
    println total
    sql.commit()
  }
  def values = it.split(',')
  values.eachWithIndex(){v, idx->
    if(StringUtils.isBlank(v) || v=='null'){ values[idx] = null}
  }
  values = Arrays.asList(values)
  try {

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
      LATLONGPAIRS) 
  values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?)
        """, values)
  } catch (Exception e) {
    error++
    //ps.println it
      println e.message
  }
}

println "total="+total
println "error="+error

println 'creating indicies on TIGER_NY'
sql.execute('create index IDX0_TIGER_NY on TIGER_NY(tlid)')
sql.execute('create index IDX1_TIGER_NY on TIGER_NY(fullname)')
sql.execute('create index IDX2_TIGER_NY on TIGER_NY(fraddL)')
sql.execute('create index IDX3_TIGER_NY on TIGER_NY(toaddL)')
sql.execute('create index IDX4_TIGER_NY on TIGER_NY(fraddR)')
sql.execute('create index IDX5_TIGER_NY on TIGER_NY(toaddR)')
sql.execute('create index IDX6_TIGER_NY on TIGER_NY(zipL)')
sql.execute('create index IDX7_TIGER_NY on TIGER_NY(zipR)')

sql.close()
ps.close()
