package edu.northeastern.cs_5200;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.List;

/**
 * iRate  the database for managing movie ratings
 * @author Sichen Wang and LinyiGao
 */

/**
 * various queries of the database
 */
public class iRate_query {


    /**
     *
     * get the average rating of the movie
     * @param stmt statement to conduct the query
     * @param movieId id of the movie
     * @throws SQLException SQLException
     */
    private static void averageRating(Statement stmt, int movieId) throws SQLException{
        ResultSet rs =  stmt.executeQuery("select Movie.Title, avg(Review.Rating) " +
                "from Review join Movie on Review.MovieID = Movie.MovieID " +
                "and Movie.MovieID ="+ movieId+" group by Movie.Title");
        if(rs.next()){
            String title = rs.getString(1);
            float rating =  rs.getFloat(2);
            System.out.println("The average rating of movie " + movieId + " " + title + " is " + rating);
        } else {
            System.out.println("No rating for movie " + movieId);
        }
        rs.close();
    }



    /**
     * get all movies' average rating
     * @param stmt statement to conduct the query
     * @throws SQLException SQLException
     */
    private static void averageRatingsAll(Statement stmt) throws SQLException {
        ResultSet rs1 = stmt.executeQuery("select MovieID from Movie");
        List<Integer> mIds = new ArrayList<>();

        while (rs1.next()) {
            mIds.add(rs1.getInt(1));
        }
        for(int i: mIds){
            averageRating(stmt, i);
        }
        rs1.close();
    }


    /**
     * get the number of endorsements of a review
     * @param stmt stmt statement to conduct the query
     * @param reviewId id of the review
     * @throws SQLException SQLException
     */
    private static void endorsement(Statement stmt, int reviewId) throws SQLException{
        ResultSet rs = stmt.executeQuery(
                "select count(ReviewId) from Endorsement where ReviewId =" + reviewId);
        if(rs.next()){
            int total = rs.getInt(1);
            System.out.println("Total votes for review " + reviewId + " is " + total);
        }
        rs.close();
    }



    /**
     * get all reviews' endorsements number
     * @param stmt stmt statement to conduct the query
     * @throws SQLException SQLException
     */
    private static void endorsementsAll(Statement stmt) throws SQLException{
        List<Integer> reviews = new ArrayList<>();
        ResultSet rs = stmt.executeQuery(
                "select ReviewId from Review ");
        while(rs.next()){
            reviews.add(rs.getInt(1));
        }
        for(int i : reviews){
            endorsement(stmt, i);
        }
        rs.close();
    }



    /**
     * get all reviews and their votes of a movie
     * @param stmt stmt statement to conduct the query
     * @param movieId id of the movie
     * @throws SQLException SQLException
     */
    private static void reviewsOfMovie(Statement stmt, int movieId) throws SQLException{
        String title = null;
        ResultSet rs1 = stmt.executeQuery("select Title from Movie where MovieID = "+movieId);
        if(rs1.next()){
            title = rs1.getString(1);
        }
        rs1.close();
        System.out.println("all reviews of "+title);
        System.out.printf("%-12s%-12s\n", "reviewId", "votes");
        ResultSet rs2 = stmt.executeQuery(
                "select a.ReviewID, a.votes from Review join " +
                        "(select Endorsement.ReviewID, count(Endorsement.ReviewID) as votes " +
                        "from Endorsement group by Endorsement.ReviewID) as a " +
                        "on Review.ReviewID = a.ReviewID where Review.MovieID = "+movieId);
        while(rs2.next()){
            System.out.printf("%-12d%-12d\n", rs2.getInt(1), rs2.getInt(2));
        }
        rs2.close();
    }



    /**
     * get the movies watched by a customer
     * @param stmt statement to conduct the query
     * @param CustomerId id of the customer
     * @throws  SQLException SQLException
     */
    private static void movieWatched(Statement stmt, int CustomerId) throws SQLException{
        ResultSet rs = stmt.executeQuery(
                "select Movie.Title, Attendance.AttendanceDate from Attendance join Movie on Attendance.MovieID = Movie.MovieID and CustomerID ="+CustomerId);
        System.out.println("customer " + CustomerId + " has watched:");
        while(rs.next()){
            String title = rs.getString(1);
            Date mDate = rs.getDate(2);
            System.out.printf("%-30s%-30s\n",title, mDate, "email");
        }
        rs.close();
    }


    /**
     * get the reviews wrote by a customer
     * @param stmt statement to conduct the query
     * @param CustomerId id of the customer
     * @throws SQLException SQLException
     */
    private static void ReviewWrote(Statement stmt, int CustomerId) throws SQLException{
        ResultSet rs = stmt.executeQuery("select ReviewID from Review where CustomerID =" + CustomerId);
        System.out.println("The review of customer " + CustomerId);
        while (rs.next()){
            int id = rs.getInt(1);
            System.out.println("review " + id);
        }
        rs.close();
    }



    /**
     * get the reviews wrote by a customer
     * @param stmt statement to conduct the query
     * @param CustomerId id of the customer
     * @throws SQLException SQLException
     */
    private static void ReviewEndorsed(Statement stmt, int CustomerId) throws SQLException{
        ResultSet rs = stmt.executeQuery("select ReviewID from Endorsement where CustomerID = " + CustomerId);
        System.out.println("customer " + CustomerId + " has voted for:");
        while(rs.next()){
            int id = rs.getInt(1);
            System.out.println("review " + id);
        }
        rs.close();
    }


    /**
     * get customers who endorse reviews on a given day in order to choose some customers to give them free concession items
     * @param stmt statement to conduct the query
     * @param date date the target date
     * @throws SQLException SQLException
     */
    private static void CustomerEndorsingOnAGivenDay(Statement stmt, Date date) throws SQLException {
        ResultSet rs = stmt.executeQuery("select CustomerID from Endorsement where EndorseDate = '" + date + "'");
        System.out.println("These customers endorsed a review on " + date + ":");
        while(rs.next()) {
            int id = rs.getInt(1);
            System.out.println("Customer " + id);
        }
        rs.close();
    }



    /**
     * get top voted author of review of a movie 3 days before given day
     * @param stmt statement to conduct the query
     * @throws SQLException SQLException
     * @throws ParseException ParseException
     */
    public static void topVotedAuthor(Statement stmt) throws SQLException, ParseException{
        long time, prevTime;
        List<Integer> rIds = new ArrayList<>();
        List<Integer> cIds = new ArrayList<>();
        List<Integer> mIds = new ArrayList<>();
        List<Integer> votes = new ArrayList<>();
        List<String> mTitles = new ArrayList<>();
        Calendar c = Calendar.getInstance();

//		Date today = iRate_editTable.getCurrentTime();
//		String Date = today.toString();

//		set today as "2019-01-15" for testing purpose
        String Date = "2019-01-15";
        System.out.println("Following customers wrote the top voted review for certain movies and won a free ticket on "+Date);
        c.setTime(iRate_editTable.StringToDate(Date));
        time = c.getTimeInMillis();
        Date currentDate = new Date(time);
        c.add(Calendar.DAY_OF_YEAR, -3);
        prevTime = c.getTimeInMillis();
        Date prevDate = new Date(prevTime);
        ResultSet rs = stmt.executeQuery(
                "select MovieID, ReviewID, numVote from " +
                        "(select a.ReviewID, a.MovieID, a.numVote from " +
                        "(select Review.MovieID, o.ReviewID, o.numVote from " +
                        "Review " +
                        "join " +
                        "(select Endorsement.ReviewID, count(Endorsement.ReviewID) as numVote from " +
                        "Endorsement where EndorseDate < '" + currentDate +"' group by Endorsement.ReviewID) as o " +
                        "on Review.ReviewID = o.ReviewId " +
                        "where Review.ReviewDate ='"+prevDate+"') as a) as t1 " +
                        "where t1.numVote = (select max(t2.numVote) from " +
                        "(select b.ReviewID, b.MovieID, b.numVote from " +
                        "(select Review.MovieID, i.ReviewID, i.numVote from " +
                        "Review " +
                        "join" +
                        "(select Endorsement.ReviewID, count(Endorsement.ReviewID) as numVote from " +
                        "Endorsement where EndorseDate < '" + currentDate + "' group by Endorsement.ReviewID) as i " +
                        "on Review.ReviewID = i.ReviewID " +
                        "where Review.ReviewDate ='"+ prevDate + "') as b) as t2 " +
                        "where t1.MovieID = t2.MovieID)"
        );

        while(rs.next()){
            int movieId = rs.getInt(1);
            int reviewId = rs.getInt(2);
            int maxVote = rs.getInt(3);
            rIds.add(reviewId);
            mIds.add(movieId);
            votes.add(maxVote);
        }
        rs.close();
        for(int rId : rIds){
            rs = stmt.executeQuery(
                    "select CustomerID from Review where ReviewID="+ rId);
            if(rs.next()){
                int custId = rs.getInt(1);
                cIds.add(custId);
            }
        }
        rs.close();
        for(int mId: mIds){
            rs = stmt.executeQuery(
                    "select Title from Movie where MovieID="+ mId);
            if(rs.next()){
                String title = rs.getString(1);
                mTitles.add(title);
            }
        }
        for(int cId : cIds){
            rs = stmt.executeQuery(
                    "select Name from Customer where CustomerID = " + cId);
            if(rs.next()){
                String name = rs.getString(1);
                System.out.println(name+"! You have won a free ticket! \n" +
                        "You wrote the most voted review on "+mTitles.remove(0)+".\n" +
                        "You had "+votes.remove(0)+" votes.");
            }
        }
        rs.close();

    }


    /**
     * print the table customer
     * @param stmt statement to conduct the query
     */
    public static void printTable_Customer(Statement stmt) {
        ResultSet rs;
        try {
            System.out.println("Print table Customer:");
            System.out.printf("%-12s%-12s%-12s\n","id", "name", "email");
            rs = stmt.executeQuery("select * from Customer");
            while (rs.next()) {
                String name = rs.getString(1);
                String email = rs.getString(2);
                int id = rs.getInt(3);
                System.out.printf("%-12d%-12s%-12s\n",id, name, email);
            }
            rs.close();
        }
        catch (SQLException ex) {

        }
    }

    // print the table movie
    public static void printTable_Movie(Statement stmt) {
        ResultSet rs;
        System.out.println("Print table Movie:");
        System.out.printf("%-12s%-12s\n","id", "title");

        try {
            rs = stmt.executeQuery("select * from Movie");
            while (rs.next()) {
                String title = rs.getString(1);
                int id = rs.getInt(2);
                System.out.printf("%-12d%-12s\n",id, title);

            }
            rs.close();
        } catch (SQLException ex) {

        }
    }

    // print the table attendance
    public static void printTable_Attendance(Statement stmt) {
        ResultSet rs;
        System.out.println("Print table Attendance:");
        System.out.printf("%-12s%-12s%-12s\n","customerId","movieId","attendancedate");
        try {
            rs = stmt.executeQuery("select * from Attendance");
            while (rs.next()) {
                int cid = rs.getInt(1);
                int mid = rs.getInt(2);
                Date RDate = rs.getDate(3);
                System.out.printf("%-12d%-12d%-12s\n",cid,mid,RDate);
            }
            rs.close();
        } catch (SQLException ex) {

        }
    }


    /**
     * print the table review
     * @param stmt statement to conduct the query
     */
    public static void printTable_Review(Statement stmt) {
        ResultSet rs;
        System.out.println("Print table Review");
        System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s\n","reviewid", "constomerId", "movieid","reviewDate", "rating", "review");
        try {
            rs=stmt.executeQuery("select * from Review");
            while(rs.next()){
                int cId = rs.getInt(1);
                int mId = rs.getInt(2);
                Date reviewDate = rs.getDate(3);
                float Rating = rs.getFloat(4);
                String Review = rs.getString(5);
                int rId = rs.getInt(6);
                System.out.printf("%-12d%-12d%-12d%-12s%-12f%-12s\n",rId, cId, mId,reviewDate, Rating, Review);
            }
            rs.close();
        } catch (SQLException ex) {

        }
    }


    /**
     * print the table endorsement
     * @param stmt statement to conduct the query
     */
    public static void printTable_Endorsement(Statement stmt) {
        ResultSet rs;
        System.out.println("Print table Endorsement");
        System.out.printf("%-12s%-12s%-12s\n","customerid","reviewid","endorsementdate");
        try {
            rs=stmt.executeQuery("select * from Endorsement");
            while(rs.next()){
                int cId = rs.getInt(1);
                int rId = rs.getInt(2);
                Date eDate = rs.getDate(3);
                System.out.printf("%-12d%-12d%-12s\n",cId,rId,eDate);
                //System.out.println(cId +"\t" + rId + "\t" + eDate);
            }
            rs.close();
        } catch (SQLException ex) {

        }
    }


    /**
     * print all the tables
     * @param stmt statement to conduct the query
     * @param dbTables all tables
     */
    public static void printAllTableCount(Statement stmt, String[] dbTables) {
        ResultSet rs = null;
        try {
            for (String tbl : dbTables) {
                rs = stmt.executeQuery("select count(*) from " + tbl);
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.printf("Table %s : count: %d\n", tbl, count);
                }
            }
            rs.close();
        } catch (SQLException ex) {

        }
    }


    /**
     * print all tables
     * @param stmt statement to conduct the query
     */
    public static void printAllTable(Statement stmt) {
        printTable_Customer(stmt);
        System.out.println("---------------------------------------------------------");
        printTable_Movie(stmt);
        System.out.println("---------------------------------------------------------");
        printTable_Attendance(stmt);
        System.out.println("---------------------------------------------------------");
        printTable_Review(stmt);
        System.out.println("---------------------------------------------------------");
        printTable_Endorsement(stmt);
    }


    public static void main(String[] args) {
        String protocol = "jdbc:derby:";
        String dbName = "iRate";
        String connStr = protocol + dbName+ ";create=true";

        String[] dbTables = {
                "Attendance", "Endorsement", "Review", "Customer", "Movie"
        };

        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        try (
                Connection conn = DriverManager.getConnection(connStr, props);
                Statement stmt = conn.createStatement()

        ) {
            System.out.println("Connected to and created database " + dbName);

            printAllTable(stmt);

            System.out.println();

            try {
                Date date = iRate_editTable.StringToDate("2019-01-14");
                CustomerEndorsingOnAGivenDay(stmt, date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println();

            reviewsOfMovie(stmt, 2);

            System.out.println();

            topVotedAuthor(stmt);

            System.out.println();

            averageRating(stmt, 2);

            System.out.println();

            averageRatingsAll(stmt);

            System.out.println();

            endorsement(stmt, 2);

            System.out.println();

            endorsementsAll(stmt);

            System.out.println();

            movieWatched(stmt, 1);

            System.out.println();

            ReviewWrote(stmt, 1);

            System.out.println();

            ReviewEndorsed(stmt, 3);

            System.out.println();

            printAllTableCount(stmt, dbTables);

        } catch (SQLException e){

        }catch (ParseException e){

        }
    }
}
