package net.sourceforge.jgeocoder.us
import net.sourceforge.jgeocoder.us.AddressParser.*
import net.sourceforge.jgeocoder.us.AddressStandardizer
import org.apache.commons.lang.StringUtils
import static net.sourceforge.jgeocoder.AddressComponent.*
class QuickTest extends GroovyTestCase {

    void testGroovy() {
      def input = '2417 MUZZY DRIVE, New Castle, PA, 16101- 000'
      def good = '2417 MUZZY DRIVE, New Castle, PA, 16101-0000'
      println AddressParser.parseAddress(input) 
      println AddressParser.parseAddress(good) 
    }
    
    void testParser2(){
      if(!new File('src/test/resources/test.txt').exists())
        return
      Writer w = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(new File('src/test/resources/out.txt'))))
      new File('src/test/resources/test.txt').eachLine{
        if(StringUtils.isNotBlank(it)){
          def map = AddressParser.parseAddress(it)
          def lst = []
            w.writeLine(it)
            if(map){
              lst<<map.get(NAME)
              lst<<map.get(NUMBER)
              lst<<map.get(PREDIR)
              lst<<map.get(STREET)
              lst<<map.get(TYPE)
              lst<<map.get(POSTDIR)
              lst<<map.get(LINE2)
              lst<<map.get(CITY)
              lst<<map.get(STATE)
              lst<<map.get(ZIP)
              w.writeLine(lst.toString())
              lst.clear()
              map = AddressStandardizer.normalizeParsedAddress(map)
              lst<<map.get(NAME)
              lst<<map.get(NUMBER)
              lst<<map.get(PREDIR)
              lst<<map.get(STREET)
              lst<<map.get(TYPE)
              lst<<map.get(POSTDIR)
              lst<<map.get(LINE2)
              lst<<map.get(CITY)
              lst<<map.get(STATE)
              lst<<map.get(ZIP)
              w.writeLine(lst.toString())
            }
            
            
        }
      }
      w.close()
    }
}
