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


def nvl(def val, def replacement){ val==null?replacement:val}

return; //comment out to run

/**
 * the raw output has total=2627282 = 441M
 * with sort | uniq will cut it down to 2588852 = 433M
 */

Config config = new Config()
//return;  //comment out to run

DriverManager.registerDriver((Driver)getClass().getClassLoader().loadClass(config.driverClass).newInstance())
//this is calling a private method of DriverManager :D 
//because the maven classloader does not have the driver class
Connection conn = DriverManager.getConnection(config.connectionString, 
    new Properties(), getClass().getClassLoader())
    
Sql sql = new Sql(conn)

sql.execute("""
    create table TIGER_PA ( TLID  numeric  not null,
        FEDIRP  varchar(2)  ,
        FENAME  varchar(30)  ,
        FETYPE  varchar(4)  ,
        FEDIRS  varchar(2)  ,
        FRADDL  numeric,
        TOADDL  numeric,
        FRADDR  numeric,
        TOADDR  numeric,
        ZIPL  varchar(5)  ,
        ZIPR  varchar(5)  ,
        FRLONG  numeric  not null,
        FRLAT  numeric  not null,
        TOLONG  numeric  not null,
        TOLAT  numeric  not null,
        LONG1  numeric ,
        LAT1  numeric ,
        LONG2  numeric  ,
        LAT2  numeric  ,
        LONG3  numeric  ,
        LAT3  numeric  ,
        LONG4  numeric  ,
        LAT4  numeric  ,
        LONG5  numeric  ,
        LAT5  numeric  ,
        LONG6  numeric  ,
        LAT6  numeric  ,
        LONG7  numeric  ,
        LAT7  numeric  ,
        LONG8  numeric  ,
        LAT8  numeric  ,
        LONG9  numeric  ,
        LAT9  numeric  ,
        LONG10  numeric  ,
        LAT10  numeric  );
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
  insert into TIGER_PA( 
      TLID,
      FEDIRP,
      FENAME,
      FETYPE,
      FEDIRS,
      FRADDL,
      TOADDL,
      FRADDR,
      TOADDR,
      ZIPL,
      ZIPR,
      FRLONG,
      FRLAT,
      TOLONG,
      TOLAT,


      LONG1,
      LAT1,
      LONG2,
      LAT2,
      LONG3,
      LAT3,
      LONG4,
      LAT4,
      LONG5,
      LAT5,
      LONG6,
      LAT6,
      LONG7,
      LAT7,
      LONG8,
      LAT8,
      LONG9,
      LAT9,
      LONG10,
      LAT10 ) 
  values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)
        """, values)
  } catch (Exception e) {
    error++
    ps.println it
      println e.message
  }
}

println "total="+total
println "error="+error

println 'creating indicies on TIGER_PA'
sql.execute('create index IDX0_TIGER_PA on TIGER_PA(tlid)')
sql.execute('create index IDX1_TIGER_PA on TIGER_PA(fename)')
sql.execute('create index IDX2_TIGER_PA on TIGER_PA(fraddL)')
sql.execute('create index IDX3_TIGER_PA on TIGER_PA(toaddL)')
sql.execute('create index IDX4_TIGER_PA on TIGER_PA(fraddR)')
sql.execute('create index IDX5_TIGER_PA on TIGER_PA(toaddR)')
sql.execute('create index IDX6_TIGER_PA on TIGER_PA(zipL)')
sql.execute('create index IDX7_TIGER_PA on TIGER_PA(zipR)')

sql.close()
ps.close()
