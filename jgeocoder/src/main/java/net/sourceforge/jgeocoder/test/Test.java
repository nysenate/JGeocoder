package net.sourceforge.jgeocoder.test;

import net.sourceforge.jgeocoder.us.AddressParser;

public class Test
{
    public static void main(String[] args){
        String s = "8450 169th Street 400 jamaica, New York, 11432";
        System.out.println(AddressParser.parseAddress(s));
    }

}
