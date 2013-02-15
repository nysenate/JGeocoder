package net.sourceforge.jgeocoder.tiger.importer;

import com.vividsolutions.jts.geom.GeometryFactory;
import net.sourceforge.jgeocoder.factory.ApplicationFactory;
import net.sourceforge.jgeocoder.util.Config;
import net.sourceforge.jgeocoder.util.FormatUtil;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapefileReader;

import java.nio.charset.Charset;

/**
 * JGeocoder relies on TIGER Line data which is distributed by the Census bureau.
 * The data is organized on a per county basis and is formatted as shapefiles.
 * Shapefiles are essentially composed of the following files:
 *     .shp - Contains the feature geometry ( collection of coordinate pairs )
 *     .shx - Contains an index of the feature geometry
 *     .dbf - Contains attribute information ( ids, street name, etc )
 *     .prj - Coordinate system meta-data
 *     .shp.xml - Meta-data
 *
 * In order for JGeocoder to use this data it has to be stored in a database. This
 * class can be used to automate the process of creating the necessary database
 * schemas and transferring the data from the files into the database.
 *
 * The requirements for a successful import are as follows:
 *     -
 *
 *
 */
public class TigerDataImporter
{
    private Config config;

    public TigerDataImporter()
    {
        ApplicationFactory.buildInstances();
        this.config = ApplicationFactory.getConfig();
    }

    public static void main(String[] args) throws Exception
    {
        TigerDataImporter tiger = new TigerDataImporter();
        String baseShapeDir = tiger.config.getValue("tiger.dir");

        GeometryFactory geometryFactory = new GeometryFactory();
        ShpFiles shpFiles = new ShpFiles(baseShapeDir + "tl_2012_36001_addrfeat.shp");
        ShapefileReader shpReader = new ShapefileReader(shpFiles, true, true, geometryFactory);
        DbaseFileReader dbReader = new DbaseFileReader(shpFiles, true, Charset.defaultCharset());

        FormatUtil.printObject(dbReader.readEntry());
        FormatUtil.printObject(shpReader.nextRecord().shape().toString());

        FormatUtil.printObject(dbReader.readEntry());
        FormatUtil.printObject(shpReader.nextRecord().toString());






    }
}
