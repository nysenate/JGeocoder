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

import net.sourceforge.jgeocoder.us.AddressParser;
import net.sourceforge.jgeocoder.us.AddressStandardizer;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbutils.*;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcDataSource;

import java.io.*;
import java.util.Scanner;

public class import_tiger_main {
	
	public static void main(String[] args) throws Exception {
		
		JdbcDataSource WorkingtigerDs = new JdbcDataSource();
		WorkingtigerDs.setURL("jdbc:h2:C:\\Users\\Gateway\\Documents\\GitHub\\JGeocoder\\local\\street_data.h2");
		
		//final Logger logger = Logger.getLogger(this.getClass());
		
		String csvFile;
		Scanner inFile = null;
		String[] holdFileLine = null;
        //logger.info("Initialized import_tiger_main");
        csvFile = ("C:\\Users\\Gateway\\Documents\\GitHub\\JGeocoder\\jgeocoder\\src\\main\\java\\net\\sourceforge\\jgeocoder\\ConvertedDate.csv");
		inFile = new Scanner(new File(csvFile));
		int count = 0;
		String fedirp =null;
		String fedirs=null;
		String fetype =null;
		
		QueryRunner run = new QueryRunner();
		
		try {
			Connection conn = WorkingtigerDs.getConnection();
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
				System.out.println(line);
				count++;
				holdFileLine = line.split(",");
				if(count%25 == 0)
				{
					System.out.println(count);
				}
				
				Map<AddressComponent, String> m = null;
				
				try{

					 if (holdFileLine[9]!=null)
					 {      
						 m  = AddressParser.parseAddress("103 "+holdFileLine[6]+" "+holdFileLine[11]);
					 }
				     else 
				     {
				    	 m  = AddressParser.parseAddress("103 "+holdFileLine[6]+" "+holdFileLine[12]);
				     }
				      if( m == null) {
				          System.out.println("error parsing "+ holdFileLine);
				          
				      } 
				      else
				      {
				        m = AddressStandardizer.normalizeParsedAddress(m);
				        if (m.get(AddressComponent.PREDIR)==null)
				        {
				        	fedirp=null;
				        }
				        else
				        {
				        	fedirp="'"+m.get(AddressComponent.PREDIR)+"'";
				        }
				        
				        if (m.get(AddressComponent.POSTDIR)=="null")
				        {
				        	fedirs=null;
				        }
				        else
				        {
				        	fedirs="'"+m.get(AddressComponent.PREDIR)+"'";
				        }
				       
				        if (m.get(AddressComponent.TYPE)=="null")
				        {
				        	fetype= null;
				        }
				        else
				        {
				        	fetype="'"+ m.get(AddressComponent.TYPE)+"'";
				        }
				        
				        
				      }
				      
				      if (!conn.isValid(1)) {
				        	conn.close();
				        	conn = WorkingtigerDs.getConnection();
				        }
				updates = run.update(conn, "insert into TIGER_NY( TLID,TFIDL,TFIDR,ARIDL,ARIDR,LINEARID," +
						"FULLNAME,FRADDL,TOADDL,FRADDR,TOADDR,ZIPL,ZIPR,EDGEMTFCC, ROADMTFCC," +
						"PARITYL,PARITYR," +
						"PLUS4L,PLUS4R," +
						"LFROMTYP,LTOTYP,RFROMTYP,RTOTYP,OFFSETL,OFFSETR,BBOX,NUMPARTS,SHAPETYPE," +
						"LATLONGPAIRS,FEDIRP,FENAME,FETYPE,FEDIRS) " +
						" values (?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?," +
						fedirp + ",'" + m.get(AddressComponent.STREET) + "'," + fetype + "," +
						fedirs+ ")" , holdFileLine);
				}
				//26 "?" (Note to Self) -Vincent Hueber
				catch(SQLException sqle)
				{
					throw sqle;
				}
				
			
		}
			if (!conn.isValid(1)) {
	        	conn.close();
	        	conn = WorkingtigerDs.getConnection();
	        }
			System.out.println("creating indicies on TIGER_NY");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(tlid)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(fename)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(fraddL)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(toaddL)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(fraddR)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(toaddR)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(zipL)");
			run.update(conn,"create index IDX0_TIGER_NY on TIGER_NY(zipR)");
			
			conn.close();
		}
		
		catch (SQLException sqle) {
	      //logger.error("SQL Statement not executed", sqle);
	      throw sqle;
		}
	}

}

