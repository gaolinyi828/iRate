package edu.northeastern.cs_5200;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * iRate  the database for managing movie ratings
 * @author Sichen Wang and LinyiGao
 */

/**
 * Create Tables includs Customer, Movie, Attendance, Review and Endorsement 5 tables and IsEmail 1 stored function.
 */
public class iRate_createTable {
	public static void main(String[] args) {
		String protocol = "jdbc:derby:";
	    String dbName = "iRate";
		String connStr = protocol + dbName+ ";create=true";
		
		String[] dbTables = {
				"Attendance", "Endorsement", "Review", "Customer", "Movie"
		};

		String[] dbFunctions = {
				"isEmail"
		};




		Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");

        try (

				Connection conn = DriverManager.getConnection(connStr, props);
				Statement stmt = conn.createStatement()
		){
	        System.out.println("Connected to and created database " + dbName);        
	        
	        for (String tbl : dbTables) {
	            try {
	            		stmt.executeUpdate("drop table " + tbl);
	            		System.out.println("Dropped table " + tbl);
	            } catch (SQLException ex) {
	            		System.out.println("Did not drop table " + tbl);
	            }
	        }

			for (String fuc : dbFunctions) {
				try {
					stmt.executeUpdate("drop function " + fuc);
					System.out.println("Dropped function " + fuc);
				} catch (SQLException ex) {
					System.out.println("Did not drop function " + fuc);
				}
			}


			String createFunction_IsEmail =
					"CREATE FUNCTION isEmail("
							+ " 	email VARCHAR(64)"
							+ "	)  RETURNS BOOLEAN"
							+ " PARAMETER STYLE JAVA"
							+ " LANGUAGE JAVA"
							+ " DETERMINISTIC"
							+ " NO SQL"
							+ " EXTERNAL NAME"
							+ "		'functions.isEmail'";
			stmt.executeUpdate(createFunction_IsEmail);
			System.out.println("Created stored function isEmail");



	        String createTable_Customer = 
	        		  "create table Customer ("
	        		+ "  Name varchar(32) not null,"
	        		+ "  Email varchar(64) not null,"
				  	+ "  CustomerID int not null,"
	        		+ "  primary key (CustomerID), "
				  	+ "check (isEmail(Email))"
	        		+ ")";
	        stmt.executeUpdate(createTable_Customer);
	        System.out.println("Created table Customer");
	        
	        String createTable_Movie = 
	        		  "create table Movie ("
	        		+ "  Title varchar(64) not null,"
	        		+ "  MovieID int not null ,"
	        		+ "  primary key (MovieID)"
	        		+ ")";
	        stmt.executeUpdate(createTable_Movie);
	        System.out.println("Created table Movie");
	        
	        String createTable_Attendance = 
	        		  "create table Attendance ("
	        		+ "  CustomerID int not null,"
	        		+ "  MovieID int not null,"
	        		+ "  AttendanceDate date not null,"
	        		+ "  primary key (CustomerID, MovieID, AttendanceDate),"
	        		+ "  foreign key (CustomerID) references Customer(CustomerID) on delete cascade,"
	        		+ "  foreign key (MovieID) references Movie(MovieID) on delete cascade"
	        		+ ")";
	        stmt.executeUpdate(createTable_Attendance);
	        System.out.println("Created table Attendance");
	        
	        String createTable_Review = 
	        		  "create table Review ("
	        		+ "  CustomerID int not null,"
	        		+ "  MovieID int not null,"
	        		+ "  ReviewDate date not null,"
	        		+ "  Rating float not null check (Rating between 0 and 5),"
	        		+ "  ShortReview varchar(1000) not null,"
	        		+ "  ReviewID int not null,"
	        		+ "  primary key (ReviewID),"
	        		+ "  unique (CustomerID, MovieID),"
	        		+ "  foreign key (CustomerID) references Customer (CustomerID) on delete cascade,"
	        		+ "  foreign key (MovieID) references Movie (MovieID) on delete cascade"
	        		+ ")";
	        stmt.executeUpdate(createTable_Review);
	        System.out.println("Created table Review");
	        
	        String createTable_Endorsement = 
	        		  "create table Endorsement ("
	        		+ "  CustomerID int,"
	        		+ "  ReviewID int,"
	        		+ "  EndorseDate date not null,"
	        		+ "  primary key(CustomerID, ReviewID),"
	        		+ "  foreign key (CustomerID) references Customer (CustomerID) on delete cascade,"
	        		+ "  foreign key (ReviewID) references Review (ReviewID) on delete cascade"
	        		+ ")";
	        stmt.executeUpdate(createTable_Endorsement);
	        System.out.println("Created table Endorsement");




        } catch (SQLException e) {
        	System.out.println(e);
        }

	}
}
