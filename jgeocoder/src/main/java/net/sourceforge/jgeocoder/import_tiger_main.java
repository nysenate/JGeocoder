package net.sourceforge.jgeocoder;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.ResultSet;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import net.sourceforge.jgeocoder.us.AddressParser;
import net.sourceforge.jgeocoder.us.AddressStandardizer;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbutils.*;
import org.apache.log4j.Logger;
import org.apache.log4j.ConsoleAppender;
import org.h2.jdbcx.JdbcDataSource;

import java.io.*;
import java.util.Scanner;

public class import_tiger_main {
	
	public static void main(String[] args) throws Exception {

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setServerName("localhost");
        mysqlDataSource.setDatabaseName("jgeocoder");
        mysqlDataSource.setUser("ash");
        mysqlDataSource.setPassword("ashislam");

        Connection conn = mysqlDataSource.getConnection();
		final Logger logger = Logger.getLogger(import_tiger_main.class);

		String csvFile;
		Scanner inFile = null;
		String[] holdFileLine = null;
        logger.info("Initialized import_tiger_main");
        csvFile = ("/home/ash/Web/nysenate/JGeocoder/2011_data.csv");
        inFile = new Scanner(new File(csvFile));
		int count = 0;
		String fedirp =null;
		String fedirs=null;
		String fetype =null;
		String msg =null;

        int unparsed = 0;
        int parsed = 0;


		QueryRunner run = new QueryRunner();

		try {

            logger.debug("Got connection " + conn.toString());
			int updates = run.update(conn,"DROP TABLE IF EXISTS TIGER_NY;");
			updates = run.update(conn,"create table TIGER_NY ( TLID numeric not null, TFIDL varchar(255)," +
					"TFIDR varchar(255), ARIDL varchar(255)," +
					"ARIDR varchar(255),LINEARID varchar(255),FEDIRP varchar(255),FENAME varchar(255)," +
					"FETYPE  varchar(255), FEDIRS varchar(255), FULLNAME varchar(255), " +
					"FRADDL varchar(255), TOADDL varchar(255),FRADDR  varchar(255), " +
					"TOADDR  varchar(255), ZIPL varchar(255),ZIPR varchar (255)," +
					" EDGEMTFCC  varchar(255), ROADMTFCC varchar(255), PARITYL varchar(255), " +
					"PARITYR  varchar(255), PLUS4L  varchar(255)," +
					"PLUS4R  varchar(255), LFROMTYP  varchar(255), LTOTYP " +
					"varchar(255), RFROMTYP  varchar(255)," +
					"RTOTYP  varchar(255), OFFSETL  varchar(255), OFFSETR  " +
					"varchar(255), BBOX text,NUMPARTS text," +
					"SHAPETYPE numeric, LATLONGPAIRS text);");
			//numeric

			while (inFile.hasNextLine())
			{
				String line = inFile.nextLine();
				
				count++;
				holdFileLine = line.split(",");
				if(count%1000 == 0)
				{
					System.out.println(String.format("%d records processed. %d unparsed. %d parsed.", count - 1, unparsed, parsed));
                    //logger.info(count + "records inserted.");
				}
				
				Map<AddressComponent, String> m = null;
				
				try{

					 if (holdFileLine[9]!=null)
					 {   
						 if(holdFileLine[6].equals("Federal Hill Rd II"))
						 {
							 msg = "Parser does not handle: " + holdFileLine[6] + " . Will return null pointer."; 
							 logger.error(msg);
							 continue;
						 }
						 if(holdFileLine[6].equals("Black Bridge Rd II"))
						 {
							 msg = "Parser does not handle: " + holdFileLine[6] + " . Will return null pointer.";
							 logger.error(msg);
							 continue;
						 }
					
						 m  = AddressParser.parseAddress("103 "+holdFileLine[6]+" "+holdFileLine[11]);
					 }
				     else 
				     {
				    	 m  = AddressParser.parseAddress("103 "+holdFileLine[6]+" "+holdFileLine[12]);
				     }
				      if( m == null) {
				          	msg = "Error parsing: "+ line + ".";
							unparsed++;
							//logger.error(msg);
							continue;
				      } 
                        parsed++;
				        // uncomment - ash
				        m = AddressStandardizer.normalizeParsedAddress(m);
				        if (m.get(AddressComponent.PREDIR)==null)
				        {
				        	fedirp=null;
				        }
				        else
				        {
				        	fedirp=m.get(AddressComponent.PREDIR);
				        }
				        
				        if (m.get(AddressComponent.POSTDIR)=="null")
				        {
				        	fedirs=null;
				        }
				        else
				        {
				        	fedirs=m.get(AddressComponent.PREDIR);
				        }
				       
				        if (m.get(AddressComponent.TYPE)=="null")
				        {
				        	fetype= null;
				        	msg = "FETYPE is null for: "+ line + ".";
							logger.warn(msg);
				        }
				        else
				        {
				        	fetype=m.get(AddressComponent.TYPE);
				        }
				        
				        
				       String[] temp = new String[holdFileLine.length + 4];
				      for(int i = 0; i < holdFileLine.length-1; i++)
				      {
				    	 temp[i] = holdFileLine[i];
				      }
				      String street = m.get(AddressComponent.STREET);
				      temp[temp.length-4] = fedirp;
				      temp[temp.length-3] = street;
				      temp[temp.length-2] = fetype;
				      temp[temp.length-1] = fedirs;
				      holdFileLine = temp;
				      
				      if (!conn.isValid(1)) {
				        	conn.close();
				        	conn = mysqlDataSource.getConnection();
				        }
				/*updates = run.update(conn, "insert into TIGER_NY( TLID,TFIDL,TFIDR,ARIDL,ARIDR,LINEARID," +
						"FULLNAME,FRADDL,TOADDL,FRADDR,TOADDR,ZIPL,ZIPR,EDGEMTFCC, ROADMTFCC," +
						"PARITYL,PARITYR," +
						"PLUS4L,PLUS4R," +
						"LFROMTYP,LTOTYP,RFROMTYP,RTOTYP,OFFSETL,OFFSETR,BBOX,NUMPARTS,SHAPETYPE," +
						"LATLONGPAIRS,FEDIRP,FENAME,FETYPE,FEDIRS) " +
						" values (?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
						"?)" , holdFileLine); */
				}
				catch(SQLException sqle)
				{
					logger.error(sqle);
					throw sqle;
				}
				
			
		}
			if (!conn.isValid(1)) {
	        	conn.close();
	        	conn = mysqlDataSource.getConnection();
	        }
			System.out.println("creating indicies on TIGER_NY");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(tlid)");
			run.update(conn,"create index IDX1_TIGER_NY on TIGER_NY(fename)");
			run.update(conn,"create index IDX2_TIGER_NY on TIGER_NY(fraddL)");
			run.update(conn,"create index IDX3_TIGER_NY on TIGER_NY(toaddL)");
			run.update(conn,"create index IDX4_TIGER_NY on TIGER_NY(fraddR)");
			run.update(conn,"create index IDX5_TIGER_NY on TIGER_NY(toaddR)");
			run.update(conn,"create index IDX6_TIGER_NY on TIGER_NY(zipL)");
			run.update(conn,"create index IDX7_TIGER_NY on TIGER_NY(zipR)");
			
			conn.close();
		}
		
		catch (SQLException sqle) {
	      logger.error(sqle);
	      throw sqle;
		}
	}

}

