# 🏨 onlineHotelBookingAndManagementSystem(OHBAMS)
The project is to implement an Online hotel booking and management system using Java which provides different functionalities for different users (like Regular customer, Corporate Client, Receptionist, Bar or Restaurant Staff) to efficiently book and manage rooms for hotel.

* Project Name: Online Hotel Booking and Management System(ohbams)
* Compatibility: JDK8 with JavaFX

[Jump to JavaDocs--->](https://pj-25.github.io/onlineHotelBookingAndManagementSystem/docs/)

## Project Structure:

```javascript
ohbams
├── clientApplication
│   ├── mainApp
│   │   ├── AlertPopup.java
│   │   ├── EventDispatcher.java
│   │   ├── Main.java
│   │   └── res
│   │       ├── images
│   │       │   └── png
│   │       └── layouts
│   │           ├── base.fxml
│   │           ├── Base.java
│   │           ├── components
│   │           │   ├── bookingInput.fxml
│   │           │   └── BookingInput.java
│   │           ├── customerView
│   │           │   ├── corporateClientView
│   │           │   │   ├── payMonthlyBill.fxml
│   │           │   │   └── PayMonthlyBill.java
│   │           │   ├── makeBooking.fxml
│   │           │   ├── MakeBooking.java
│   │           │   ├── regularCustomerView
│   │           │   ├── viewAndEditBooking.fxml
│   │           │   └── ViewAndEditBooking.java
│   │           └── staffView
│   │               ├── addServiceCharge.fxml
│   │               ├── AddServiceCharge.java
│   │               └── receptionistView
│   │                   ├── addBooking.fxml
│   │                   ├── AddBooking.java
│   │                   ├── billPayment.fxml
│   │                   ├── BillPayment.java
│   │                   ├── checkInGuest.fxml
│   │                   ├── CheckInGuest.java
│   │                   ├── checkOutGuest.fxml
│   │                   └── CheckOutGuest.java
│   ├── networkConnection
│   │   └── ServerChannel.java
│   ├── res
│   │   └── stylesheets
│   │       └── style.css
│   └── userAuthentication
│       ├── controller
│       │   ├── AuthenticationController.java
│       │   ├── LoginController.java
│       │   └── RegisterController.java
│       └── res
│           ├── images
│           └── layouts
│               ├── authentication.fxml
│               ├── login.fxml
│               └── register.fxml
├── constData
│   ├── BookingStatus.java
│   ├── MessageDelimiter.java
│   ├── RoomType.java
│   └── UserType.java
├── entity
│   ├── BillingInfo.java
│   ├── BookingInfo.java
│   ├── CorporateClient.java
│   ├── EntityFormatException.java
│   ├── Entity.java
│   ├── EntityParser.java
│   ├── Receptionist.java
│   ├── RegularCustomer.java
│   ├── RoomAllocationStatus.java
│   ├── Room.java
│   ├── ServiceCharge.java
│   └── User.java
├── eventHandler
│   ├── CustomEvent.java
│   ├── EventHandler.java
│   └── EventType.java
├── messageHandler
│   ├── MessageCode.java
│   ├── MessageConsumer.java
│   ├── MessageFormatHandler.java
│   ├── MessageParser.java
│   ├── MessageType.java
│   ├── RequestType.java
│   ├── ResponseType.java
│   └── UpdateEventType.java
└── serverApplication
    ├── databaseHandler
    │   ├── DatabaseHandler.class
    │   ├── DatabaseHandler.java
    │   ├── InvalidDataException.java
    │   ├── LutonDB.class
    │   └── LutonDB.java
    ├── ServeClient.java
    └── Server.java
    

26 directories, 141 files
```

### Database config:
    DBMS : MySQL 
    Database Server: XAMPP MariaDB server
	
* Configure Database:

  * Create database
  
        Database name: luton_hotel
		
  * Create user
     
        User Name: luton@admin
        Password: luton@admin

  * Import database schema:
      
        Import using sql script (luton_hotel.sql)

  * JDBC driver: ```mysql-connector-java-8.0.27``` (https://dev.mysql.com/downloads/connector/j/)
  
### IDE config:

* IDE used: ```Intellij Idea```

      Java version: 1.8.0_281 (JDK8)

* External library: 
      
      JDBC connector driver:  mysql-connector-java-8.0.27.jar (Will require to download and add)

* Add libraries: 
      
      File > Project Structure > library > + > Java > {select above connector jar file} 


### Server Config: 
```
    Server package: ohbams.serverApplication
    Default server port number: 9292
    Server IP: localhost
```

* Start Server:

		Run Server (IDE)
			
  _default port:9292 but can also pass server port as command line argument_


### Client/User config:
	 Client package: ohbams.clientApplication
	
* Start application:

      Run Main (IDE)
	
	

