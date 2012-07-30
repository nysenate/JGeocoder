package net.sourceforge.jgeocoder.us;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import net.sourceforge.jgeocoder.AddressComponent;

public class AddressParserTest
{

   @org.junit.Test
   public void testParseAddress()
   {
      String addr1 = "123 Avenue of art, philadelphia pa 12345";
      Map<AddressComponent, String> addressComponents = AddressParser.parseAddress(addr1);
      assertEquals("12345", addressComponents.get(AddressComponent.ZIP));
      assertEquals("philadelphia", addressComponents.get(AddressComponent.CITY));
      assertEquals("pa", addressComponents.get(AddressComponent.STATE));
      assertEquals("123", addressComponents.get(AddressComponent.NUMBER));
      addressComponents = AddressParser.parseAddress("123 FISH AND GAME rd philadelphia pa 12345");
      assertEquals("12345", addressComponents.get(AddressComponent.ZIP));
      assertEquals("philadelphia", addressComponents.get(AddressComponent.CITY));
      assertEquals("pa", addressComponents.get(AddressComponent.STATE));
      assertEquals("123", addressComponents.get(AddressComponent.NUMBER));
      assertEquals("FISH AND GAME", addressComponents.get(AddressComponent.STREET));
      assertEquals("rd", addressComponents.get(AddressComponent.TYPE));
   }

   @org.junit.Test
   public void testParseAddress2()
   {
      String addr1 = " 14625 County Road 672, Wimauma, FL 33598";
      Map<AddressComponent, String> addressComponents = AddressParser.parseAddress(addr1);
      System.out.println("addressComponents: " + addressComponents);
      // {CITY=Wimauma, ZIP=33598, STREET=County, STATE=FL, LINE2=672, TYPE=Road, NUMBER=14625}
      assertEquals("14625", addressComponents.get(AddressComponent.NUMBER));

      //      assertEquals("12345", addressComponents.get(AddressComponent.ZIP));
      //      assertEquals("philadelphia", addressComponents.get(AddressComponent.CITY));
      //      assertEquals("pa", addressComponents.get(AddressComponent.STATE));
      //      assertEquals("123", addressComponents.get(AddressComponent.NUMBER));
   }

   @org.junit.Test
   public void testSaintNameExpansion(){
     String addr1 = "St. louis Missouri";
     Map<AddressComponent, String> m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("SAINT LOUIS", m.get(AddressComponent.CITY));
     assertEquals("MO", m.get(AddressComponent.STATE));
     addr1 = "123 St peters ave, St. louis Missouri";
     m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("SAINT LOUIS", m.get(AddressComponent.CITY));
     assertEquals("SAINT PETERS", m.get(AddressComponent.STREET));
     assertEquals("MO", m.get(AddressComponent.STATE));
   }
   
   @org.junit.Test
   public void testOrdinalNormalization(){
     String addr1 = "Mozilla Corporation, 1981 second street building K Mountain View CA 94043-0801";
     Map<AddressComponent, String> m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("MOUNTAIN VIEW", m.get(AddressComponent.CITY));
     assertEquals("CA", m.get(AddressComponent.STATE));
     assertEquals("2ND", m.get(AddressComponent.STREET));
     assertEquals("BLDG K", m.get(AddressComponent.LINE2));
   }
   
   @org.junit.Test
   public void testDesignatorConfusingCitiesParsing(){
     String addr1 = "123 main street St. louis Missouri";
     Map<AddressComponent, String> m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("123", m.get(AddressComponent.NUMBER));
     assertEquals("MAIN", m.get(AddressComponent.STREET));
     assertEquals("ST", m.get(AddressComponent.TYPE));
     assertEquals("SAINT LOUIS", m.get(AddressComponent.CITY));
     assertEquals("MO", m.get(AddressComponent.STATE));
     addr1 = "123 south lake park  Fort Duchesne Utah";
     m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("FORT DUCHESNE", m.get(AddressComponent.CITY));
     assertEquals("LAKE", m.get(AddressComponent.STREET));
     assertEquals("PARK", m.get(AddressComponent.TYPE));
     assertEquals("UT", m.get(AddressComponent.STATE));
     addr1 = "123 south lake park apt 200 Fort Duchesne Utah";
     m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("FORT DUCHESNE", m.get(AddressComponent.CITY));
     assertEquals("LAKE", m.get(AddressComponent.STREET));
     assertEquals("PARK", m.get(AddressComponent.TYPE));
     assertEquals("UT", m.get(AddressComponent.STATE));
     assertEquals("APT 200", m.get(AddressComponent.LINE2));
     
     addr1 = "123 main st cape may court house nj";
     m = AddressStandardizer.normalizeParsedAddress(AddressParser.parseAddress(addr1));
     assertEquals("CAPE MAY COURT HOUSE", m.get(AddressComponent.CITY));
     assertEquals("NJ", m.get(AddressComponent.STATE));

   }
   
}
