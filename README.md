# Project4-iRate
This is the team project repository of project 4 - the database for managing movie ratings, which includes the original java files, java docs and testing data samples.<br><br>

Created by Sichen Wang and Linyi Gao

# Backgroud and requirements
This project is a portion of a an application that enables registered movie theater customers to rate a movie that they saw at the theater, and for other registered customers to vote for reviews.<br><br>

iRate is a social media application that encourages theater customers to rate a movie that they saw at the theater in the past week and write a short review. Other costumers can vote one review of a particular movie as "helpful" each day. The writer of the top rated review of a movie written three days earlier receives a free movie ticket, and voting is closed for all reviews of the movie written three days ago. Someone who voted one or more movie reviews as "helpful" on a given day will be chosen to receive a free concession item.<br><br>

This project provides a back-end that could be used for a future project in a web development class to create web-based front-end to the database that allows a movie theater to operate this promotional application. It can also be used as part of a back end to a mobile application for the theater.

# Project details
The project is to develop and document a data model for representing entities and relationships in this promotial social media application, provide DDL for creating the tables, DML for editing entries in the tables, and DQL for making commonly used queries to retrieve information about the status of reviews and votes from the database.<br><br>

The data model for the project is based on the concept of registered customers, movies seen by a costumer, reviews written by a customer, and votes cast by other costumers for a given movie.<br><br>

Customer is a registered customer of the theater. The Customer information includes a customer name, email address, the date the customer joined, and a gensym customer ID. The information is entered by the theater when the customer registers. If a customer is deleted, all of his or her reviews and endorsements are deleted.<br><br>

Movie is a record of a movie playing at the theater. It includes a title, and a gensym movie ID. This information is entered by t he theater for each movie it plays.<br><br>

Attendance is a record of a movie seen by a customer on a given date. It includes a movie ID, the attendance date, and the customer ID. This information is entered when the customer purchases a ticket for a show. If a movie is deleted, all of its attendances are deleted. Attendance info is used to verify attendance when creating a review.<br><br>

Review is a review of a particular movie attended by a custumer within the last week. The review includes the customer ID, the movie ID, the review date, a rating (0-5 stars), a short (1000 characters or less) review, and a gensym review ID. There can only be one movie review per customer, and the date of the review must be within 7 days of the most recent attendance of the movie. If a movie is deleted, all of its reviews are also deleted.<br><br>

Endorsement is an endoresement of a movie review by a customer. A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie. The endorsement includes the review ID, the customerID of the endorser, and the endoresemnt date. A customer cannot endorse his or her own review. If a review is deleted, all endorsements are also deleted.<br><br>

# Table design
## Create tables
We create 5 tables as mentioned in project details:<br>
Customer, Movie, Attendance, Review and Endorsement. The ER model is shown in the presentation slide.

### Table details
#### Customer
    Name  varchar(64) not null
    Email varchar(64) not null check (isEmail(Email))
    CustomerID int not null
    
    primary key - CustomerID
  
#### Movie
    Title varchar(64) not null
    MovieID int not null
        
    primary key - MovieID
    
#### Attendance
    CustomerID int
    MovieID int
    AttendanceDate date not null
        
    primary key - CustomerID, MovieID, AttendanceDate
        
    foreign key (CustomerID) references Customer (CustomerID) on delete cascade
    foreign key (MovieID) references Movie (MovieID) on delete cascade
    
#### Review
    CustomerID int
    MovieID int
    ReviewDate date not null
    Rating float not null check (Rating between 0 and 5)
    ShortReview varchar(1000)
    ReviewID int not null
        
    primary key - ReviewID
        
    foreign key (CustomerID) references Customer (CustomerID) on delete cascade
    foreign key (MovieID) references Movie (MovieID) on delete cascade
        
    unique - CustomerID, MovieID
    
#### Endorsement
    CustomerID int
    ReviewID int
    EndorseDate date not null
        
    primary key - CustomerID, ReviewID
        
    foreign key (CustomerID) references Customer (CustomerID) on delete cascade
    foreign key (ReviewID) references Review (ReviewID) on delete cascade
    
## Create stored functions
We create a stored function isEmail(String email) that takes a string as input, check if it is the right format of email.
  
# Table Edit
## Insert data into table
### insertCustomer function
We just use a prepared statement created before to insert a customer.

### insertMovie function
We just use a prepared statement created before to insert a movie.

### insertAttendance function
We just use a prepared statement created before to insert an attendance.

### insertReview function
Insert a review has 2 requirements:
* The date of the review must be within 7 days of the most recent attendance of the movie
* Cannot write a review for a movie the customer has not watched

Our solutions before the insert operation:
* Use a query to search for the most recent attendance date from table Attendance using CustomerID and MovieID (and we use 'order by AttendanceDate desc' to get the most recent one)
* If we cannot find a record, the customer has not watched the movie he is writing review for
* Otherwise we compare the date we find with the review date to check the first requirement

#### samples of review insertion
Our samples of review is in main function of iRate_editTable.java. In the 11 samples of reviews, 
* `5 7 10 11` the customer write a review that he has not watched
* `4` the review date is 2019-01-19 and the most recent endorsement date is 2019-01-10

### insertEndorsement function
Insert an endorsement has 3 requirements:
* A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie.
* A customer cannot endorse his or her own review
* The endorsement date cannot be three or more days after the review date 

Our solutions before the insert operation:
* Search this review from the Review table using reviewID to check if the review is written by this customer
* Compare the review date and endorsement date to check the third requirement
* Search for the most recent endorsement date from this customer for the same movie from Endorsement and Review table (using join for these tables)

#### samples of endorsement insertion
Our samples of review is in main function of iRate_editTable.java. In the 14 samples of endorsements:
* `1 7` the customer endorses his or her own review
* `3 9` the endorsement is too late that the review for votes has been closed
* `6 12 13` the customer endorses a review that does not exist
* `14` the customer endorse 2 reviews of the same movie on one day

## Update data of table
We think only the personal information of customer can be updated. So we have the updateCustomer function using a query to update the name or email according to the customerID.<br><br>
The main function has optional testing function that changes the email of customer 1 from gmail to 163.

## Delete data from table
We can delete customer, movie or review data from the database. "ON DELETE CASCADE" makes surThe main function has optional testing function thate that all related data will also be deleted.
    
### deleteCustomer function
using a query to delete a customer using input customerID<br><br>
The main function has optional testing function that delete customer 1. And table printed before and after shows that the review he wrote and his endorsements are also deleted.

### deleteMovie function
using a query to delete a movie using input movieID<br><br>
The main function has optional testing function that delete movie 2. And table printed before and after shows that the review written for this movie are also deleted.

### deleteReview function
using a query to delete a review using input reviewID<br><br>
The main function has optional testing function that delete review 3. And table printed before and after shows that the endorsed review are also deleted.

# Table Query
## print table
We have functions that can print one specific table or all tables. Using a query to select from a table can easily print all infomation of the data.

    Print table Customer:
    name  email id
    Austin  Austin@gmail.com  1
    Biff  Biff@gmail.com  2
    Bo  Bo@gmail.com  3
    Bode  Bode@gmail.com  4
    Brooklyn  Brooklyn@gmail.com  5
    Bradley Bradley@gmail.com 6
    Brilie  Brilie@gmail.com  7
    Finley  Finley@gmail.com  8
    Harley  Harley@gmail.com  9
    Huxley  Huxley@gmail.com  10
    ---------------------------------------------------------
    Print table Movie:
    title id
    The Shawshank Redemption  1
    The Godfather 2
    The Godfather: Part II  3
    12 Angry Men  5
    The Dark Knight 4
    Pulp Fiction  7
    The Lord of the Rings: The Return of the King 6
    The Good, the Bad and the Ugly  8
    Fight Club  9
    Schindler's List  10
    ---------------------------------------------------------
    Print table Attendance:
    customerid  movieid attendancedate
    1 1 2019-01-10
    1 2 2019-01-11
    2 2 2019-01-11
    3 3 2019-01-13
    3 5 2019-01-15
    4 4 2019-01-14
    5 5 2019-01-13
    5 7 2019-01-17
    6 6 2019-01-10
    7 7 2019-01-10
    8 8 2019-01-13
    9 8 2019-01-12
    9 9 2019-01-10
    10  2 2019-01-11
    10  10  2019-01-19
    ---------------------------------------------------------
    Print table Review
    customerid  movieid reviewDate  Rating  Review  reviewId
    1 2 2019-01-12  2.0 so good 1
    2 2 2019-01-12  3.0 so good 2
    3 5 2019-01-16  4.0 so good 3
    10  10  2019-01-21  5.0 so good 6
    10  2 2019-01-18  5.0 so good 8
    6 6 2019-01-13  4.0 so good 9
    ---------------------------------------------------------
    Print table Endorsement
    customerid  reviewid  endorsementdate
    1 2 2019-01-13
    4 3 2019-01-16
    5 3 2019-01-16
    3 1 2019-01-14
    7 9 2019-01-13
    8 9 2019-01-14

## CustomerEndorsingOnAGivenDay function
This function is used to give customers who endorse one or more reviews on a given day a free concession item. Taking the given date as input, we can use a query to get the customers from endorsement table.

    These customers endorsed a review on 2019-01-14:
    Customer 3
    Customer 8

## insertTicket function
This function is used to get customers who wrote the most voted review on corresponding movies three days ago. We use queries to get all reviews written three days ago grouped by movies, and get the reviews that are voted most for each movie, and get the authors of these reviews.

    Following customer wrote the top voted review for certain movies and won a free ticket
    Austin! You have won a free ticket!
    Biff! You have won a free ticket!
    Bo! You have won a free ticket!
    Bradley! You have won a free ticket!

## average rating functions
These functions are used to get the average ratings for one or all movies. We just use 'avg' and 'group by' to calculate movies, and get the result.

    The average rating of movie 2 The Godfather is 3.3333333

    No rating for movie 1
    The average rating of movie 2 The Godfather is 3.3333333
    No rating for movie 3
    No rating for movie 4
    The average rating of movie 5 12 Angry Men is 4.0
    The average rating of movie 6 The Lord of the Rings: The Return of the King is 4.0
    No rating for movie 7
    No rating for movie 8
    No rating for movie 9
    The average rating of movie 10 Schindler's List is 5.0

## endorsement functions
This function is used to get the number of endorsements of one or all reviews. We can use count(reviewID) from endorsement to get the result.

    Total votes for review 2 is 1

    Total votes for review 1 is 1
    Total votes for review 2 is 1
    Total votes for review 3 is 2
    Total votes for review 6 is 0
    Total votes for review 8 is 0
    Total votes for review 9 is 2

## movieWatched function
This function is used to get the movie lists watched by a customer. We join Attendance and Movie and get all movies the customer watched.

    customer 1 has watched:
    The Shawshank Redemption  2019-01-10
    The Godfather 2019-01-11

## reviewWrote function
This function is used to get the reviews wrote by a customer. We select reviews from Review table using customerID input to get the results.

    The review of customer 1
    review 1

## reviewEndorsed function
This function is used to get the endorsements by a customer. We select reviewID from endorsement using customerID to get the results.

    customer 3 has voted for:
    review 1

# Conclusions and Future improvements
There are still a lot of aspects that might be improved, for example:
* We can set all unique IDs as auto increment to make them created automatically. In our java files we just take them as an input for test purposes.
* There is no trigger in our projects, and most of our checkings are outside the database. We may consider putting checkings into triggers or put multiqueries into one single query when we get more knowledge of SQL languages and functions. 
