# project4-iRate
This is the team project repository of project 4 - the database for managing movie ratings, which includes the original java files, java docs and testing data samples.<br>Created by Sichen Wang and Linyi Gao

# Backgroud and requirements
    This project is a portion of a an application that enables registered movie theater customers to rate a movie that they saw at the theater, and for other registered customers to vote for reviews.

    iRate is a social media application that encourages theater customers to rate a movie that they saw at the theater in the past week and write a short review. Other costumers can vote one review of a particular movie as "helpful" each day. The writer of the top rated review of a movie written three days earlier receives a free movie ticket, and voting is closed for all reviews of the movie written three days ago. Someone who voted one or more movie reviews as "helpful" on a given day will be chosen to receive a free concession item.

    This project provides a back-end that could be used for a future project in a web development class to create web-based front-end to the database that allows a movie theater to operate this promotional application. It can also be used as part of a back end to a mobile application for the theater.

# Project details
    The project is to develop and document a data model for representing entities and relationships in this promotial social media application, provide DDL for creating the tables, DML for editing entries in the tables, and DQL for making commonly used queries to retrieve information about the status of reviews and votes from the database.

    The data model for the project is based on the concept of registered customers, movies seen by a costumer, reviews written by a customer, and votes cast by other costumers for a given movie.

    Customer is a registered customer of the theater. The Customer information includes a customer name, email address, the date the customer joined, and a gensym customer ID. The information is entered by the theater when the customer registers. If a customer is deleted, all of his or her reviews and endorsements are deleted

    Movie is a record of a movie playing at the theater. It includes a title, and a gensym movie ID. This information is entered by t he theater for each movie it plays.

    Attendance is a record of a movie seen by a customer on a given date. It includes a movie ID, the attendance date, and the customer ID. This information is entered when the customer purchases a ticket for a show. If a movie is deleted, all of its attendances are deleted. Attendance info is used to verify attendance when creating a review.

    Review is a review of a particular movie attended by a custumer within the last week. The review includes the customer ID, the movie ID, the review date, a rating (0-5 stars), a short (1000 characters or less) review, and a gensym review ID. There can only be one movie review per customer, and the date of the review must be within 7 days of the most recent attendance of the movie. If a movie is deleted, all of its reviews are also deleted.

    Endorsement is an endoresement of a movie review by a customer. A customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie. The endorsement includes the review ID, the customerID of the endorser, and the endoresemnt date. A customer cannot endorse his or her own review. If a review is deleted, all endorsements are also deleted.

# Table design
## Create tables
    We create 5 tables as mentioned in project details:
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
    We think only the personal information of customer can be updated. So we have the updateCustomer function using a query to update the name or email according to the customerID.
    The main function has optional testing function that changes the email of customer 1 from gmail to 163.

## Delete data from table
We can delete customer, movie or review data from the database. "ON DELETE CASCADE" makes surThe main function has optional testing function thate that all related data will also be deleted.
    
### deleteCustomer function
    using a query to delete a customer using input customerID
    The main function has optional testing function that delete customer 1. And table printed before and after shows that the review he wrote and his endorsements are also deleted.

### deleteMovie function
    using a query to delete a movie using input movieID
    The main function has optional testing function that delete movie 2. And table printed before and after shows that the review written for this movie are also deleted.

### deleteReview function
    using a query to delete a review using input reviewID
    The main function has optional testing function that delete review 3. And table printed before and after shows that the endorsed review are also deleted.

# Table Query
## print table
