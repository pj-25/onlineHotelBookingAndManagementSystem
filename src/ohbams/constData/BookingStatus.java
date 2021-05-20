package ohbams.constData;

import java.util.HashMap;

public enum BookingStatus {

    CANCELED(0),
    UNALLOCATED(1),
    ALLOCATED(2),
    CHECKOUT(3),
    WAITING(4),
    ;

    private final int BOOKING_STATUS_CODE;

    BookingStatus(int roomCode){
        this.BOOKING_STATUS_CODE = roomCode;
    }

    public int getCode(){
        return this.BOOKING_STATUS_CODE;
    }

    private static final HashMap<Integer, BookingStatus> bookingStatusMap = new HashMap<>();

    static{
        for(BookingStatus bookingStatus: BookingStatus.values()){
            bookingStatusMap.put(bookingStatus.getCode(), bookingStatus);
        }
    }

    public static BookingStatus get(int roomCode){
        return bookingStatusMap.get(roomCode);
    }
}
