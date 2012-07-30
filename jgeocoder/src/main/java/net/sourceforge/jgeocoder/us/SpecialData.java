package net.sourceforge.jgeocoder.us;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SpecialData{
    public static final Map<String, List<String>> C_MAP = new HashMap<String, List<String>>();
    static{
      BufferedReader r = null;
        try {
            r = new BufferedReader( new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("exception_city.txt")));
            String line = null;
            Map<String, Set<String>> tmp = new HashMap<String, Set<String>>();
            while((line = r.readLine())!=null){
                String[] items = line.split("\\s*->\\s*");
                String[] cities = items[1].split("[|]");
                String state = items[0];
                Set<String> set = tmp.get(state);
                if(set == null){
                    set = new HashSet<String>();
                    tmp.put(state, set);
                }
                for(String city : cities){                    
                    set.add(city);
                }
            }
            for(Map.Entry<String, Set<String>> e : tmp.entrySet()){
                String[] array = e.getValue().toArray(new String[]{});
                Arrays.sort(array, new Comparator<String>(){
                    @Override
                    public int compare(String o1, String o2) {
                        return Integer.valueOf(o2.length()).compareTo(o1.length());
                    }
                });
                C_MAP.put(e.getKey(), Arrays.asList(array));
            }
        } catch (Exception e) {
            throw new Error("Unable to initalize exception_city", e);
        }finally{
          if(r != null){ try {
        r.close();
      } catch (IOException e) {} }
        }
    }
}