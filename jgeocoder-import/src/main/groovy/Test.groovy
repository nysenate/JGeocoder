import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.IllegalArgumentExceptionimport java.sql.DriverManager
import java.sql.Connection
import groovy.sql.Sql
import org.apache.commons.lang.StringUtils
import java.sql.Driver
/* no longer valid 
DriverManager.registerDriver((Driver)getClass().getClassLoader().loadClass('org.h2.Driver').newInstance())
Connection conn = DriverManager.getConnection("jdbc:h2:/home/jliang/Desktop/h2db/testdb", 
    new Properties(), getClass().getClassLoader())
Sql db = new Sql(conn)
testQuery(db, '8', 'SNYDER', null, null, null, '19148')

db.close()

def testQuery(Sql db,def num, def street, def type, def predir, def postdir, def zip){
//  street, name, predir, postdir, type, number, city, state, zip, line2,
//  where fename = @StreetName and fetype = @StreetType and fedirp = @DirPrefix and fedirs = @DirSuffix and (zipl = @ZipCode or zipr =@ZipCode)
//       and ((fraddl <= @Number and toaddl >= @Number) or (fraddl >= @Number and toaddl <= @Number )
//        OR (fraddr <= @Number and toaddr >= @Number) OR (fraddr >= @Number and toaddr <= @Number) )
  if(zip == null || num == null || street == null )
    throw new IllegalArgumentException("zip, num, street are required")//  t1.tlid, t1.fraddr, t1.fraddl, t1.toaddr, t1.toaddl, //  t1.zipL, t1.zipR, t1.tolat, t1.tolong, t1.frlong, t1.frlat,  //  t2.long1, t2.lat1, t2.long2, t2.lat2, t2.long3, t2.lat3, t2.long4, t2.lat4,//  t2.long5, t2.lat5, t2.long6, t2.lat6, t2.long7, t2.lat7, t2.long8, t2.lat8,//  t2.long9, t2.lat9, t2.long10, t2.lat10  def sql = """  select *   from tiger_1 t1 left outer join tiger_2 t2 on t1.tlid = t2.tlid where  t1.fename = '$street' and  (        (fraddL <= '$num' and toaddL >= '$num') or (fraddL >= '$num' and toaddL <= '$num')    or (fraddR <= '$num' and toaddR >= '$num') or (fraddR >= '$num' and toaddR <= '$num')  )  and (t1.zipL = '$zip' or t1.zipR = '$zip') """
  def criterias = []
  if(type != null){    sql += " and  ( t1.fetype = ? or t1.fetype = null)"    criterias << type   }
  println criterias  println sql
  db.eachRow(sql, criterias){    println it
  }
}*/