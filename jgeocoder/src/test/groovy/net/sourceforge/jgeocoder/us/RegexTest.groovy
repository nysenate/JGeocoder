package net.sourceforge.jgeocoder.us

import net.sourceforge.jgeocoder.us.RegexLibrary

class RegexTest extends GroovyTestCase {
    void testNumber() {
        ['12th','21st','23rd','5235th', '3456345th','4th','35634th','56th','345th',
         '634th','56th','3456th','345th','63456th','3456th', '2nd'].each({i -> 
          assert (i ==~ RegexLibrary.ORDINAL_ALL)
        })
    }
}