JGeocoder
=========

JGeocoder is a free, open source geocoder implemented in Java.


Installation
==============

There are 2 projects:

* jgeocoder performs all the geocoding functionality.
* Jgeocoder-import is only used to import data from a csv (called `tiger_main.csv`, stored in the same folder all the jgeocoder-import code is) into an H2 database


Install Groovy
-------------------

http://groovy.codehaus.org/Download

Install a database.
----------------------

We used H2 version 1.3.168; you'll need the same version. Download it and add it to your classpath:

> http://hsql.sourceforge.net/m2-repo/com/h2database/h2/1.3.168/h2-1.3.168.jar

You can use something else by just changing a bit of code in `import_tiger_main.groovy` in jgeocoder-import, and `H2DbDataSourceFactory.java` in jgeocoder.

Download the Data
--------------------

Go to ftp://ftp2.census.gov/geo/tiger/TIGER2011/ADDRFEAT/ and download all the files address feature files you are interested in. Files for the state of NY start all start with 36 and are in the range 36001-36123. Other states will follow a similar pattern. Extract all the files into their own folders, stored in one parent folder.


Convert the Shape Files
--------------------------

Install Python and the `pyshp` library: http://pypi.python.org/pypi/pyshp

Edit convertShapefile.py:93, setting the outputFile to the directory that contains the `jgeocoder-import/src/main/groovy/import_tiger_main.groovy` file.

Edit `import_tiger_main.groovy`, specifying where you want to save your database.

Run `groovy import_tiger_main.groovy` from the directory that contains the `jgeocoder-import/src/main/groovy/import_tiger_main.groovy` file

Edit the `H2DbDataSourceFactory.java` to reflect where the H2 database is stored, on the line `config.setTigerDataSource(H2DbDataSourceFactory.getH2DbDataSource(`

Save the `Data` folder containing the three JDB files somewhere on your harddrive, and specify its location in `BobbysTest.java`

Run `BobbysTest.java`

