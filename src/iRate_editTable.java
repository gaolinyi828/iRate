import javax.swing.event.ListDataEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.Date;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * iRate  the database for managing movie ratings
 * @author Sichen Wang and LinyiGao
 */

/**
 * Edit Table: Insert data into table
 */
public class iRate_editTable {
	/**
	 * Miliseconds per day
	 */
	public static long MILLI_SECS_PER_DAY = 86400000;


	/**
	 * convert  date (String) into date (java.sql.Date)
	 * @param date date(String)
	 * @return date (java.sql.Date)
	 * @throws ParseException ParseException
	 */
	public static Date StringToDate(String date) throws ParseException{
		java.util.Date dateUtil = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		java.sql.Date dateSql = new java.sql.Date(dateUtil.getTime());

		return dateSql;
	}

	/**
	 * get current date(java.sql.Date)
	 * @return current date(java.sql.Date)
	 */
	public static Date getCurrentTime(){
		java.util.Date dUtile = new java.util.Date();
		java.sql.Date dSql= new java.sql.Date(dUtile.getTime());
		return dSql;
	}


	/**
	 * insert data into table Customer
	 * @param name name of the customer
	 * @param email email address of the customer
	 * @param CustomerId id of the customer
	 * @param insertRow_Customer the prepared statement to inserting a Customer
	 */
	public static void insertCustomer
			(String name, String email, int CustomerId, PreparedStatement insertRow_Customer) {
		try {

			insertRow_Customer.setString(1, name);
			insertRow_Customer.setString(2, email);
			insertRow_Customer.setInt(3, CustomerId);
			insertRow_Customer.executeUpdate();


			System.out.println("customer " + " added");
		} catch (SQLException e) {
			System.err.println("Input customer invalid!");
		}
	}

	/**
	 * insert data into table Movie
	 * @param title title of the movie
	 * @param movieID id of the movie
	 * @param insertRow_Movie the prepared statement to inserting a Movie
	 */
	public static void insertMovie(String title, int movieID, PreparedStatement insertRow_Movie) {
		try {
			insertRow_Movie.setString(1, title);
			insertRow_Movie.setInt(2, movieID);


			insertRow_Movie.execute();
			System.out.println("movie " + movieID + " added");
		} catch (SQLException e) {
			System.err.println("Input movie invalid!");
		}
	}

	/**
	 * insert data into table Attendance
	 * @param customerID id of the customer attending
	 * @param movieID id of the movie
	 * @param attendanceDate date of the attendance
	 * @param insertRow_Attendace the prepared statement to inserting an Attendance
	 */
	public static void insertAttendance(int customerID, int movieID, Date attendanceDate, PreparedStatement insertRow_Attendace) {
		try {
			insertRow_Attendace.setInt(1, customerID);
			insertRow_Attendace.setInt(2, movieID);
			insertRow_Attendace.setDate(3, attendanceDate);

			insertRow_Attendace.execute();
			System.out.println("attendance of customer " + customerID + " for movie " + movieID + " added");
		} catch (SQLException e) {
			System.err.println("Input attendance invalid!");
		}
	}

	/**
	 * insert data into table Attendance
	 * @param stmt statement to query
	 * @param customerID id of the author of the review
	 * @param movieID id of the movie review is about
	 * @param reviewDate date the review written
	 * @param rating rating of the review
	 * @param shortReview content of the review
	 * @param reviewID id of the review
	 * @param insertRow_Review the prepared statement to inserting an Review
	 */
	public static void insertReview(Statement stmt, int customerID, int movieID, Date reviewDate, float rating, String shortReview, int reviewID, PreparedStatement insertRow_Review) {
		try {
			Calendar c = Calendar.getInstance();
			long timeReview, timeAttend;

			ResultSet rs1 = stmt.executeQuery("select AttendanceDate " +
					"from Attendance where CustomerID=" + customerID + "and MovieID = "+movieID);
			if(rs1.next()){
				Date d = rs1.getDate(1);
				System.out.println(reviewDate+ " " + d);
				c.setTime(reviewDate);
				timeReview = c.getTimeInMillis();
				c.setTime(d);
				timeAttend = c.getTimeInMillis();
				if ((timeReview - timeAttend) <= 7 * MILLI_SECS_PER_DAY && (timeReview - timeAttend) >= 0) {
					insertRow_Review.setInt(1, customerID);
					insertRow_Review.setInt(2, movieID);
					insertRow_Review.setDate(3, reviewDate);
					insertRow_Review.setFloat(4, rating);
					insertRow_Review.setString(5, shortReview);
					insertRow_Review.setInt(6, reviewID);
					insertRow_Review.execute();
					System.out.println("review " + reviewID + " added");
				}
				else {
					System.err.println("The date " + reviewDate + " of the review " + reviewID + " must be within 7 days of the most recent attendance of the movie!");
				}
			}
			rs1.close();
		} catch (SQLException e) {
			System.err.println("Input review invalid!");
		}
	}

	/**
	 * insert data into table Endorsement
	 * @param stmt statement to query
	 * @param customerId id of the customer endorsing
	 * @param reviewId id of the review endorsed
	 * @param EndorseDate date of the endorsement
	 * @param insertRow_Endorsement the prepared statement to inserting an Review
	 */
	public static void insertEndorsement(Statement stmt, int customerId, int reviewId, Date EndorseDate, PreparedStatement insertRow_Endorsement) {
		try{
			Calendar c = Calendar.getInstance();
			long timePrevEndorse, timeEndorse, timeReview;
			ResultSet rs1 = null;
			ResultSet rs2 = null;
			ResultSet rs3 = null;
			ResultSet rs4 = null;
			Date d = null;
			Date d1 = null;

			// check if the review does not exist and get the date the review is written on
			rs3 = stmt.executeQuery("select ReviewDate from"
					+ " Review where"
					+ "  Review.ReviewID = " + reviewId);
			if (rs3.next()) {
				d1 = rs3.getDate(1);
			}
			else {
				System.err.println("Cannot endorse a review that does not exist!" + reviewId);
				return;
			}
			rs3.close();

			// check if the review is written by him or herself
			rs4 = stmt.executeQuery("select * from"
					+ " Review where "
					+ "  Review.ReviewID = " + reviewId
					+ "  and Review.CustomerID = " + customerId);
			if (rs4.next()) {
				System.err.println("Customer " + customerId + " cannot endorse his or her own review " + reviewId + "!");
				rs4.close();
				return;
			}

			// check if the most recent endorsement date is at least one day after the customer's endorsement of a review for the same movie
			// and check if the current endorsement date is within three days of the review date
			rs1 = stmt.executeQuery("select MovieID from " +
					"Review WHERE" +
					" Review.ReviewID = "+ reviewId +
					" and Review.CustomerID !="
					+ customerId);
			if(rs1.next()){
				int mId = rs1.getInt(1);
				rs2= stmt.executeQuery(
						"select MAX(EndorseDate) from" +
								" Review join Endorsement on" +
								" Endorsement.ReviewID = Review.ReviewID and" +
								" EndorseMent.CustomerID=" + customerId +
								" and Review.MovieID = "+ mId);
			} else return;
			rs1.close();
			if (rs2.next()) {
				d = rs2.getDate(1);
			}
			c.setTime(EndorseDate);
			timeEndorse = c.getTimeInMillis();
			c.setTime(d1);
			timeReview = c.getTimeInMillis();
			// not endorse for any review of this movie before
			if(d == null && timeEndorse - timeReview < 3 * MILLI_SECS_PER_DAY) {
				insertRow_Endorsement.setInt(1, customerId);
				insertRow_Endorsement.setInt(2, reviewId);
				insertRow_Endorsement.setDate(3, EndorseDate);
				insertRow_Endorsement.execute();
				System.out.println("endorsement of customer " + customerId + " for review " + reviewId + " added");
				return;
			}
			else if (timeEndorse - timeReview >= 3 * MILLI_SECS_PER_DAY) {
				System.err.println("Endorse Date is "+ EndorseDate + ". You cannot vote for review " + reviewId + " written equal or more than three days ago! ");
				return;
			}
			c.setTime(d);
			timePrevEndorse = c.getTimeInMillis();
			rs2.close();
			// check if the most recent endorsement date is at least one day after the customer's endorsement of a review for the same movie
			if (timeEndorse - timePrevEndorse >= 1 * MILLI_SECS_PER_DAY && timeEndorse - timeReview < 3 * MILLI_SECS_PER_DAY) {
				insertRow_Endorsement.setInt(1, customerId);
				insertRow_Endorsement.setInt(2, reviewId);
				insertRow_Endorsement.setDate(3, EndorseDate);
				insertRow_Endorsement.execute();
				System.out.println("endorsement of customer " + customerId + " for review " + reviewId + " added");
			}
			else {
				// endorse more than one review of the same movie one day
				if (timeEndorse - timePrevEndorse < 1 * MILLI_SECS_PER_DAY)
					System.err.println("A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie!");
					// endorse a review that was written equal or more than three days ago
				else if (timeEndorse - timeReview >= 3 * MILLI_SECS_PER_DAY)
					System.err.println("Endorse Date is "+ EndorseDate + ". You cannot vote for review " + reviewId + " written equal or more than three days ago! ");
			}
		}catch (SQLException e){
			System.err.println("Input endorsement invalid!");
		}
	}

	/**
	 * update a customer
	 * @param stmt statement to contduct query
	 * @param name name of the customer
	 * @param email email address of a customer
	 * @param customerId id of a customer
	 */
	public static void updateCustomer(Statement stmt, String name, String email, int customerId) {
		try {
			stmt.execute("update Customer"
					+ " set Name = '" + name
					+ "' ,Email = '" + email
					+ "' where CustomerID = " + customerId);

			System.out.println("customer " + customerId + " updated");
		} catch (SQLException ex) {
			System.err.println("Input customer invalid!");
		}
	}

	/**
	 * delete a customer
	 * @param stmt statement to contduct query
	 * @param customerID id of a customer
	 */
	public static void deleteCustomer(Statement stmt, int customerID) {
		try {
			stmt.execute("delete from Customer where customerID = " + customerID);
			System.out.println("customer " + customerID + " deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * delete a movie
	 * @param stmt statement to contduct query
	 * @param movieID id of a movie
	 */
	public static void deleteMovie(Statement stmt, int movieID) {
		try {
			stmt.execute("delete from Movie where movieID = " + movieID);
			System.out.println("movie " + movieID + " deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * delete a review
	 * @param stmt statement to contduct query
	 * @param reviewID id of a review
	 */
	public static void deleteReview(Statement stmt, int reviewID) {
		try {
			stmt.execute("delete from Review where reviewID = " + reviewID);
			System.out.println("review " + reviewID + " deleted");
		} catch (SQLException e) {

		}
	}

	public static void main(String[] args) {
		String protocol = "jdbc:derby:";
		String dbName = "iRate";
		String connStr = protocol + dbName+ ";create=true";

		String[] dbTables = {
				"Attendance", "Endorsement", "Review", "Customer", "Movie"
		};

		String fileName = "data.txt";

		BufferedReader br = null;
		Properties props = new Properties();
		props.put("user", "user1");
		props.put("password", "user1");
		try (
				Connection conn = DriverManager.getConnection(connStr, props);
				Statement stmt = conn.createStatement();
				FileReader fReader = new FileReader(new File(fileName));


				PreparedStatement insertRow_Customer = conn.prepareStatement(
						"insert into Customer values(?, ?, ?)");
				PreparedStatement insertRow_Movie = conn.prepareStatement(
						"insert into Movie values(?, ?)");
				PreparedStatement insertRow_Attendance = conn.prepareStatement(
						"insert into Attendance values(?, ?, ?)");
				PreparedStatement insertRow_Review = conn.prepareStatement(
						"insert into Review values(?, ?, ?, ?, ?, ?)");
				PreparedStatement insertRow_Endorsement = conn.prepareStatement(
						"insert into Endorsement values(?, ?, ?)")

		) {

			System.out.println("Connected to and created database " + dbName);
			System.out.println();
			br = new BufferedReader(fReader);

			for (String tbl : dbTables) {
				try {
					stmt.executeUpdate("delete from " + tbl);
					System.out.println("Truncated table " + tbl);
				} catch (SQLException ex) {
					System.out.println("Did not truncate table " + tbl);
				}
			}
			System.out.println();

			String line;
			String CData[];
			while((line=br.readLine())!=null) {

				try {
					CData = line.split("\t");

					String name = CData[0];
					String email = CData[1];
					Integer cId = Integer.parseInt(CData[2]);
					String title = CData[3];
					Integer Mid = Integer.parseInt(CData[4]);
					Date aDate = StringToDate(CData[5]);

					insertCustomer(name, email, cId, insertRow_Customer);

					insertMovie(title, Mid, insertRow_Movie);

					insertAttendance(cId, Mid, aDate, insertRow_Attendance);

				} catch (ParseException e) {

				}
			}
			br.close();
			System.out.println();

			int cid[] = {1, 2, 3, 6, 2, 10, 1, 10, 6, 8, 1};
			int mid[] = {2, 2, 5, 6, 4, 10, 3, 2, 6, 6, 3};
			float rating[] = {2, 3, 4, 1, 5, 5, 4, 5, 4, 4, 4};
			String rDate[] = {"2019-01-12", "2019-01-12", "2019-01-16",
					"2019-01-19", "2019-01-18", "2019-01-21", "2019-01-21",
					"2019-01-18", "2019-1-13", "2019-1-12", "2019-02-15"};
			String review = "so good";
			int rid[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

			try{
				for (int i = 0; i < cid.length; i++){
					insertReview(stmt, cid[i], mid[i], StringToDate(rDate[i]), rating[i], review, rid[i], insertRow_Review);
				}
			} catch (ParseException ex){
			}
			System.out.println();

			int custId[] = {1, 1, 3, 4, 5, 6, 10, 3, 3, 7, 8, 3, 2, 3};
			int revId[] = {1, 2, 2, 3, 3, 5, 8, 1, 2, 9, 9, 10, 11, 2};
			String endorDate[] ={"2019-01-13", "2019-01-13", "2019-01-16",
					"2019-01-16", "2019-01-16", "2019-01-22", "2019-01-22",
					"2019-01-14", "2019-01-15", "2019-01-13", "2019-01-14", "2019-01-13",
					"2019-02-15", "2019-01-14"
			};

			try{
				for(int i = 0; i < custId.length; i++){
					insertEndorsement(stmt, custId[i], revId[i], StringToDate(endorDate[i]), insertRow_Endorsement);
				}

			}catch (ParseException ex){

			}


			System.out.println();

//			// optional operations
//
//			// update customer 1
//			System.out.println();
//			iRate_query.printTable_Customer(stmt);
//			updateCustomer(stmt, "Austin", "Austin@163.com", 1);
//			iRate_query.printTable_Customer(stmt);
//
//			// delete customer 1
//			System.out.println();
//			iRate_query.printTable_Customer(stmt);
//			iRate_query.printTable_Review(stmt);
//			iRate_query.printTable_Endorsement(stmt);
//			deleteCustomer(stmt, 1);
//			iRate_query.printTable_Customer(stmt);
//			iRate_query.printTable_Review(stmt);
//			iRate_query.printTable_Endorsement(stmt);
//
//			// delete movie 2
//			System.out.println();
//			iRate_query.printTable_Movie(stmt);
//			iRate_query.printTable_Review(stmt);
//			deleteMovie(stmt, 2);
//			iRate_query.printTable_Movie(stmt);
//			iRate_query.printTable_Review(stmt);
//
//			// delete review 3
//			System.out.println();
//			iRate_query.printTable_Review(stmt);
//			iRate_query.printTable_Endorsement(stmt);
//			deleteReview(stmt, 3);
//			iRate_query.printTable_Review(stmt);
//			iRate_query.printTable_Endorsement(stmt);


		} catch (SQLException ex) {
		} catch (IOException ex){

		}
	}
}


