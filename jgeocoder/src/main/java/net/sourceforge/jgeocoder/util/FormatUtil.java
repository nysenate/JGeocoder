package net.sourceforge.jgeocoder.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.PrintStream;

public class FormatUtil
{
    private static Logger logger = Logger.getLogger(FormatUtil.class);

    /**
     * Returns JSON representation of object.
     * Failure to map object results in empty string.
     *
     * @return String   JSON string
     * */
    public static String toJsonString(Object o){
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(o);
        }
        catch(JsonGenerationException g){
            logger.error("Object to JSON Error: ".concat(g.getMessage()));
            return "";
        }
        catch(JsonMappingException m){
            logger.error("Object to JSON Error: ".concat(m.getMessage()));
            return "";
        }
        catch(Exception ex){
            logger.error("Object to JSON Error: ".concat(ex.getMessage()));
            return "";
        }
    }

    /** Prints out JSON representation of object to standard out */
    public static String printObject(Object o){
        return printObject(o, System.out);
    }

    /** Prints out JSON representation of object to the given print stream */
    public static String printObject(Object o, PrintStream ps){
        String s = toJsonString(o);
        if (ps != null){
            ps.println(s);
        }
        return s;
    }
}
