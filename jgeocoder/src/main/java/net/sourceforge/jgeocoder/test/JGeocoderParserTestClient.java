package net.sourceforge.jgeocoder.test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import net.sourceforge.jgeocoder.CommonUtils;
import net.sourceforge.jgeocoder.JGeocodeAddress;
import net.sourceforge.jgeocoder.tiger.JGeocoder;
import net.sourceforge.jgeocoder.us.AddressStandardizer;


public class JGeocoderParserTestClient {
  public static void main(String[] args) throws Exception{
    String in;
    JGeocoder jg = new JGeocoder();
    if(args.length != 0){
      in = args[0];
      JGeocodeAddress ret  = jg.geocodeAddress(in);
      long start = System.currentTimeMillis();
      System.out.println();
      System.out.println("Parsed: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getParsedAddr()), "null"));
      System.out.println("Normalized: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getNormalizedAddr()), "null"));
      System.out.println("Geocoded: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getGeocodedAddr()), "null"));
      System.out.println(ret.toStringMultiLine());
      CommonUtils.printElapsed(start, TimeUnit.SECONDS);
      jg.cleanup();
      return;
    }
    
    System.out.println("Input raw address as a single line");
    System.out.println("Enter blank line to end session");
    System.out.println();
    
    BufferedReader inbuf = new BufferedReader(new InputStreamReader(System.in));
    
    while ((in = inbuf.readLine()) != null && in.length() != 0){
      long start = System.currentTimeMillis();
      System.out.println();
      JGeocodeAddress ret  = jg.geocodeAddress(in);
      System.out.println("Parsed: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getParsedAddr()), "null"));
      System.out.println("Normalized: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getNormalizedAddr()), "null"));
      System.out.println("Geocoded: "+ CommonUtils.nvl(AddressStandardizer.toSingleLine(ret.getGeocodedAddr()), "null"));
      System.out.println(ret.toStringMultiLine());
      CommonUtils.printElapsed(start, TimeUnit.SECONDS);
      System.out.println();
    }
    jg.cleanup();
  }

}
