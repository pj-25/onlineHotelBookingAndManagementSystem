package ohbams.serverApplication.databaseHandler;

import ohbams.constData.BookingStatus;
import ohbams.constData.RoomType;
import ohbams.constData.UserType;
import ohbams.entity.BillingInfo;
import ohbams.entity.BookingInfo;
import ohbams.entity.ServiceCharge;

import java.awt.print.Book;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class LutonDB extends DatabaseHandler {
    public LutonDB() throws SQLException, ClassNotFoundException {
        super();
    }

    public boolean registerUser(String userID, String password, String firstName, String lastName, int age, char gender, UserType userType, String emailID, String phoneNumber, String ...userInfo) throws SQLException{
        String query = "Insert into user values(?,?,?,?,?,?,?,?,?);";

        connection.setAutoCommit(false);

        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userID);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, lastName);
        preparedStatement.setInt(5, age);
        preparedStatement.setString(6, gender+"");
        preparedStatement.setString(7, userType.getCode());
        preparedStatement.setString(8, emailID);
        preparedStatement.setString(9, phoneNumber);

        boolean registerStatus = false;
        if(preparedStatement.executeUpdate()!=0){
            if(userType == UserType.REGULAR_CUSTOMER && userInfo.length == 3){
                registerStatus = registerRegularCustomer(userID, userInfo[0], userInfo[1], userInfo[2]);
            }
            else if(userType == UserType.CORPORATE_CLIENT && userInfo.length == 2){
                registerStatus = registerCorporateClient(userID, userInfo[0], userInfo[1]);
            }
            else if(userType == UserType.RECEPTIONIST && userInfo.length == 1){
                registerStatus = registerReceptionist(userID, userInfo[0]);
            }
            else if(userType == UserType.BAR_STAFF || userType == UserType.RESTAURANT_STAFF){
                registerStatus = true;
            }
        }

        if(registerStatus){
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }
        connection.rollback();
        connection.setAutoCommit(true);
        return false;
    }

    public boolean registerRegularCustomer(String userID, String creditCardNumber, String expiryDate, String cardType) throws SQLException{
        String query = "Insert into regular_customer values(?,?,?,?);";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userID);
        preparedStatement.setString(2, creditCardNumber);
        preparedStatement.setDate(3, Date.valueOf(expiryDate));
        preparedStatement.setString(4, cardType);
        return preparedStatement.executeUpdate() != 0;
    }

    public boolean registerCorporateClient(String userID, String corporateName, String role) throws SQLException{
        String query = "Insert into corporate_client(user_id, corporate_name, role) values(?,?,?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userID);
        preparedStatement.setString(2, corporateName);
        preparedStatement.setString(3, role);
        return preparedStatement.executeUpdate() != 0;
    }

    public boolean registerReceptionist(String userID, String receptionCode) throws SQLException{
        String query = "Insert into receptionist values(?,?)";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userID);
        preparedStatement.setString(2, receptionCode);
        return preparedStatement.executeUpdate()!=0;
    }


    private String getPassword(String userID) throws SQLException{
        String query = "Select password from user where user_id=? LIMIT 1";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, userID);
        ResultSet passwordInfo = preparedStatement.executeQuery();
        if(passwordInfo!=null && passwordInfo.next()){
            return passwordInfo.getString(1);
        }
        return null;
    }

    public boolean isValidUser(String userID, String password) throws SQLException{
        String originalPassword = getPassword(userID);
        return originalPassword!=null && originalPassword.equals(password);
    }

    public int addBooking(String customerID, LocalDateTime checkinOn, LocalDateTime checkoutOn, int totalGuests, RoomType roomType) throws SQLException{
        String query = "Insert into booking_info(customer_id, checkin_on, checkout_on, total_guests, room_type_id) VALUES(?,?,?,?,?)";
        preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, customerID);
        preparedStatement.setTimestamp(2, Timestamp.valueOf(checkinOn));
        preparedStatement.setTimestamp(3, Timestamp.valueOf(checkoutOn));
        preparedStatement.setInt(4, totalGuests);
        preparedStatement.setInt(5, roomType.getCode());
        if(preparedStatement.executeUpdate()==1){
            ResultSet bookingIdInfo = preparedStatement.getGeneratedKeys();
            if(bookingIdInfo!= null && bookingIdInfo.next()){
                return bookingIdInfo.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateBooking(int bookingId, LocalDateTime checkinOn, LocalDateTime checkoutOn, int totalGuests, RoomType roomType) throws SQLException{
        String query = "Update booking_info set checkin_on=?, checkout_on=?, total_guests=?, room_type_id=? where booking_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(checkinOn));
        preparedStatement.setTimestamp(2, Timestamp.valueOf(checkoutOn));
        preparedStatement.setInt(3, totalGuests);
        preparedStatement.setInt(4, roomType.getCode());
        preparedStatement.setInt(5, bookingId);
        return preparedStatement.executeUpdate()!=0;
    }


    public BookingInfo getBookingInfoById(int bookingId) throws SQLException{
        String query = "select B.booking_id, customer_id, booking_on, status, checkin_on, checkout_on, total_guests, B.room_type_id, R.room_id from booking_info as B LEFT JOIN room as R ON B.booking_id=R.booking_id WHERE B.booking_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, bookingId);
        ResultSet bookingData = preparedStatement.executeQuery();
        if(bookingData!=null && bookingData.next()){
            return readBookingInfo(bookingData);
        }
        return null;
    }


    public ArrayList<BookingInfo> getAllCurrentBookingFor(String customer_id) throws SQLException{
        String query = "select B.booking_id, customer_id, booking_on, status, checkin_on, checkout_on, total_guests, B.room_type_id, R.room_id from booking_info as B LEFT JOIN room as R ON B.booking_id=R.booking_id WHERE customer_id=? and status NOT IN (0,3); ";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, customer_id);
        ResultSet bookings = preparedStatement.executeQuery();
        ArrayList<BookingInfo> currentBookings = new ArrayList<>();
        while(bookings.next()){
            currentBookings.add(readBookingInfo(bookings));
        }
        if(currentBookings.size()!=0){
            return currentBookings;
        }
        return null;
    }

    public ArrayList<BillingInfo> getAllPendingMonthlyBillsFor(String corporateClientId) throws SQLException{
        String query="Select billing_info.* from billing_info, booking_info where billing_info.booking_id=booking_info.booking_id and customer_id=? and billing_info.status='N' and booking_info.status=3";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, corporateClientId);
        ResultSet billingInfoData = preparedStatement.executeQuery();
        ArrayList<BillingInfo> billingInfoList = new ArrayList<>();
        while(billingInfoData.next()){
            billingInfoList.add(readBillingInfo(billingInfoData));
        }
        if(billingInfoList.size()>0) {
            return billingInfoList;
        }
        return null;
    }

    public boolean payBill(int bookingId) throws SQLException{
        String query = "UPDATE billing_info set status = 'Y' WHERE booking_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, bookingId);
        return preparedStatement.executeUpdate()!=0;
    }

    public boolean payMonthlyBill(String corporateClientId) throws SQLException{
        String query = "UPDATE billing_info set status = 'Y' WHERE booking_id IN (SELECT booking_id from booking_info where customer_id=? and status=3) and status='N'";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, corporateClientId);
        if(preparedStatement.executeUpdate()!=0){
            return updateCorporateClientAmount(corporateClientId, 0);
        }
        return false;
    }

    public boolean updateCorporateClientAmount(String corporateClientId, float amount) throws SQLException{
        String query = "UPDATE corporate_client set amount=? where user_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setFloat(1, amount);
        preparedStatement.setString(2, corporateClientId);
        return preparedStatement.executeUpdate()!=0;
    }

    public BillingInfo readBillingInfo(ResultSet billingInfoResultSet) throws SQLException{
        return new BillingInfo(billingInfoResultSet.getInt(1), billingInfoResultSet.getInt(1), billingInfoResultSet.getFloat(3), billingInfoResultSet.getTimestamp(4).toLocalDateTime(), billingInfoResultSet.getString(5).charAt(0));
    }

    public BookingInfo readBookingInfo(ResultSet bookingInfoResultSet) throws SQLException {
        if(bookingInfoResultSet!=null){
            int bookingId = bookingInfoResultSet.getInt(1);
            String customerId = bookingInfoResultSet.getString(2);
            LocalDateTime bookingOn = bookingInfoResultSet.getTimestamp(3).toLocalDateTime();
            BookingStatus bookingStatus = BookingStatus.get(bookingInfoResultSet.getInt(4));
            LocalDateTime checkInOn = bookingInfoResultSet.getTimestamp(5).toLocalDateTime();
            LocalDateTime checkOutOn = bookingInfoResultSet.getTimestamp(6).toLocalDateTime();
            int totalGuests = bookingInfoResultSet.getInt(7);
            RoomType roomType = RoomType.get(bookingInfoResultSet.getInt(8));
            String roomId = bookingInfoResultSet.getString(9);
            if(bookingInfoResultSet.wasNull()){
                roomId = "null";
            }
            return new BookingInfo(bookingId, customerId, bookingOn, bookingStatus, checkInOn, checkOutOn, totalGuests, roomType, roomId);
        }
        return null;
    }

    public boolean updateBookingStatus(int bookingID, BookingStatus bookingStatus) throws SQLException{
        String query = "Update booking_info set status=? where booking_id=?";
        if(bookingStatus == BookingStatus.CANCELED || bookingStatus == BookingStatus.ALLOCATED){
            query += " and status IN ("+BookingStatus.UNALLOCATED.getCode()+","+BookingStatus.WAITING.getCode()+")";
        }
        else if(bookingStatus == BookingStatus.CHECKOUT){
            query += " and status = "+BookingStatus.ALLOCATED.getCode();
        }
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, bookingStatus.getCode());
        preparedStatement.setInt(2, bookingID);
        return preparedStatement.executeUpdate()!=0;
    }

    public String getRoomIdFor(int bookingId) throws SQLException{
        String query = "Select room_id from room where booking_id=? LIMIT 1";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, bookingId);
        ResultSet roomIdData = preparedStatement.executeQuery();
        if(roomIdData.next()){
            return roomIdData.getString(1);
        }
        return null;
    }

    public String getUserType(String userId) throws SQLException{
        String query = "Select user_type from user where user_id='"+userId+"'";
        ResultSet userTypeInfo = statement.executeQuery(query);
        if(userTypeInfo.next()){
            return userTypeInfo.getString(1);
        }
        return "-1";
    }

    public BookingStatus getBookingStatus(int bookingId) throws SQLException{
        String query = "Select status from booking_info where booking_id='"+bookingId+"'";
        ResultSet bookingStatusData = statement.executeQuery(query);
        if(bookingStatusData.next()){
            return BookingStatus.get(bookingStatusData.getInt(1));
        }
        return null;
    }

    public int addServiceCharge(String chargedById, String serviceInfo, int bookingId, float amount) throws SQLException{
        BookingStatus bookingStatus = getBookingStatus(bookingId);
        if(bookingStatus == BookingStatus.ALLOCATED){
            try{
                String query = "Insert into service_charge(charged_by_id, service_info, booking_id, amount) VALUES(?,?,?,?);";
                preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, chargedById);
                preparedStatement.setString(2, serviceInfo);
                preparedStatement.setInt(3, bookingId);
                preparedStatement.setFloat(4, amount);
                if(preparedStatement.executeUpdate()!=0){
                    ResultSet serviceChargeIdSet = preparedStatement.getGeneratedKeys();
                    if(serviceChargeIdSet.next()){
                        return serviceChargeIdSet.getInt(1);
                    }
                }
            }catch (SQLIntegrityConstraintViolationException ignore){
            }
        }
        return -1;
    }

    public int checkAvailability(RoomType roomType) throws SQLException{
        String query = "Select (total_rooms - booked_count) from room_allocation_status where room_type_id='"+roomType.getCode()+"'";
        ResultSet availableRooms = statement.executeQuery(query);
        if(availableRooms.next()){
            return availableRooms.getInt(1);
        }
        return -1;
    }

    public ServiceCharge getServiceChargeById(int serviceChargeId) throws SQLException{
        String query = "Select * from service_charge where service_charge_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, serviceChargeId);
        ResultSet serviceChargeData = preparedStatement.executeQuery();
        if(serviceChargeData.next()){
            return readServiceCharge(serviceChargeData);
        }
        return null;
    }

    public ServiceCharge readServiceCharge(ResultSet serviceChargeData) throws SQLException {
        return new ServiceCharge(serviceChargeData.getInt(1), serviceChargeData.getString(2), serviceChargeData.getString(3), serviceChargeData.getInt(4), serviceChargeData.getFloat(5), serviceChargeData.getTimestamp(6).toLocalDateTime());
    }

    public ArrayList<ServiceCharge> getAllServiceChargesFor(int bookingId) throws SQLException{
        String query = "Select * from service_charge where booking_id=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, bookingId);
        ResultSet serviceChargesData = preparedStatement.executeQuery();
        ArrayList<ServiceCharge> serviceChargeArrayList = new ArrayList<>();
        while(serviceChargesData.next()){
            serviceChargeArrayList.add(readServiceCharge(serviceChargesData));
        }
        if(serviceChargeArrayList.size()>0){
            return serviceChargeArrayList;
        }
        return null;
    }

}
