package net.sourceforge.jgeocoder;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;

public class CommonUtils{

  /**
   * Replace an object with a replacement object if it's <code>null</code>
   *
   * @param <T>
   * @param value the object to test for a null value.
   * @param replacement the value returned if input <code>value</code> is null.
   * @return <code>replacement</code> if input <code>value</code> is <code>null<code>, 
   * <code>value</code> otherwise.
   */
  public static <T> T nvl(T value, T replacement){
    return value==null? replacement : value;
  }
  
  /**
   * Calculate the elapsed time in specified {@link TimeUnit}
   * @param startInMilli
   * @param unit 
   * @return
   */
  public static double getElapsed(long startInMilli, TimeUnit unit){
    double elapsed = System.currentTimeMillis() - startInMilli;
    if(unit == MILLISECONDS){
      return elapsed;
    }else if(unit == DAYS){
      return elapsed/DateUtils.MILLIS_PER_DAY;
    }else if(unit == HOURS){
      return elapsed/DateUtils.MILLIS_PER_HOUR;
    }else if(unit == MINUTES){
      return elapsed/DateUtils.MILLIS_PER_MINUTE;
    }else if(unit == SECONDS){
      return elapsed/DateUtils.MILLIS_PER_SECOND;
    }else {
      throw new UnsupportedOperationException(unit+" conversion is not supported");
    }
  }
  /**
   * Convenient method for printing elapsed time
   * @param startInMilli
   * @param unit
   */
  public static void printElapsed(long startInMilli, TimeUnit unit){
    System.out.println("Elapsed time = "+getElapsed(startInMilli, unit)+" "+unit.name());
  }
}