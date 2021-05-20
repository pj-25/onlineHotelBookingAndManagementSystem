package ohbams.entity;

import ohbams.constData.BookingStatus;
import ohbams.constData.RoomType;
import ohbams.messageHandler.MessageFormatHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BookingInfo extends EntityParser implements Entity{
    private int bookingId;
    private String customerId;
    private LocalDateTime bookingOn;
    private BookingStatus bookingStatus;
    private LocalDateTime checkInOn;
    private LocalDateTime checkOutOn;
    private int totalGuests;
    private RoomType roomType;
    private String roomId;

    public BookingInfo(int bookingId, String customerId, LocalDateTime bookingOn, BookingStatus bookingStatus, LocalDateTime checkInOn, LocalDateTime checkOutOn, int totalGuests, RoomType roomType, String roomId) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.bookingOn = bookingOn;
        this.bookingStatus = bookingStatus;
        this.checkInOn = checkInOn;
        this.checkOutOn = checkOutOn;
        this.totalGuests = totalGuests;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getBookingOn() {
        return bookingOn;
    }

    public void setBookingOn(LocalDateTime bookingOn) {
        this.bookingOn = bookingOn;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCheckInOn() {
        return checkInOn;
    }

    public void setCheckInOn(LocalDateTime checkInOn) {
        this.checkInOn = checkInOn;
    }

    public LocalDateTime getCheckOutOn() {
        return checkOutOn;
    }

    public void setCheckOutOn(LocalDateTime checkOutOn) {
        this.checkOutOn = checkOutOn;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(bookingId+"", customerId, bookingOn.toString(), bookingStatus.getCode()+"", checkInOn.toString(), checkOutOn.toString(), totalGuests+"", roomType.getCode()+"", roomId+"");
    }

    public static BookingInfo parse(String ...entityData) throws EntityFormatException{
        try {
            return new BookingInfo(Integer.parseInt(entityData[0]), entityData[1], LocalDateTime.parse(entityData[2]), BookingStatus.get(Integer.parseInt(entityData[3])), LocalDateTime.parse(entityData[4]), LocalDateTime.parse(entityData[5]), Integer.parseInt(entityData[6]), RoomType.get(Integer.parseInt(entityData[7])), entityData[8]);
        }
        catch (Exception e){
            System.err.println(e);
            throw new EntityFormatException();
        }
    }

    public static ArrayList<BookingInfo> parseList(String ...entitiesData) throws EntityFormatException{
        try{
            ArrayList<BookingInfo> bookingList = new ArrayList<>();
            for(String bookingData:entitiesData){
                bookingList.add(parse(MessageFormatHandler.decode(bookingData)));
            }
            return bookingList;
        }
        catch (Exception e){
            System.out.println(e);
            throw new EntityFormatException();
        }
    }

}
