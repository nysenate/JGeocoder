import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.IllegalArgumentException
import java.sql.DriverManager
import java.sql.Connection
import groovy.sql.Sql
import org.apache.commons.lang.StringUtils
import java.sql.Driver
/*no longer valid

DriverManager.registerDriver((Driver)getClass().getClassLoader().loadClass('org.h2.Driver').newInstance())
Connection conn = DriverManager.getConnection("jdbc:h2:C:\Users\Bobby\tiger_ny", 
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
    throw new IllegalArgumentException("zip, num, street are required")
  def criterias = []
  if(type != null){
  println criterias
  db.eachRow(sql, criterias){
  }
}