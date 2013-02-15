package net.sourceforge.jgeocoder.us;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Utilities, Not meant for general consumptions
 * @author jliang
 *
 */
class Utils{
  private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("\\(\\?P<(.*?)>");
  //assumes all capturing groups are named
  public static NamedGroupPattern compile(String regex){
    Matcher m = NAMED_GROUP_PATTERN.matcher(regex);
    Map<Integer, String> namedGroupMap = new HashMap<Integer, String>();
    int i =1;
    while(m.find()){
      namedGroupMap.put(i, m.group(1).toUpperCase());
      i++;
    }
    return new NamedGroupPattern(m.replaceAll("("), namedGroupMap);
  }
  
  public static class NamedGroupPattern {
    private final String _regex;
    private final Map<Integer, String> _namedGroupMap;
    public NamedGroupPattern(String regex, Map<Integer, String> namedGroupMap) {
      _regex = regex;
      _namedGroupMap = namedGroupMap;
    }
    public String getRegex() {
      return _regex;
    }
    public Map<Integer, String> getNamedGroupMap() {
      return _namedGroupMap;
    }
  }
  public static <T> T nvl(T t, T tt){
    return t == null ? tt : t;
  }
  
}