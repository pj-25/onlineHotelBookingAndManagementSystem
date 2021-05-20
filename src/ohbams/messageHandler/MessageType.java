package ohbams.messageHandler;

import java.util.HashMap;

public enum MessageType implements MessageCode {

    UNAUTHORIZED_USER(-2),
    INVALID_MSG(-1),
    FAILED(0),
    SUCCESS(1),
    REGISTER(2),
    LOGIN(3),
    MAKE_BOOKING(4),
    VIEW_BOOKING(5),
    CHANGE_BOOKING(6),
    CANCEL_BOOKING(7),
    PAY_MONTHLY_BILL(8),
    BILL_PAYMENT(9),
    CHECKIN_GUEST(10),
    CHECKOUT_GUEST(11),
    ADD_BOOKING(12),
    ADD_SERVICE_CHARGE(13),
    ENTITY_DATA(14),
    GET_PENDING_MONTHLY_BILLS(15),
    CHECK_ROOM_AVAILABILITY(16)
    ;

    private final int MESSAGE_CODE;

    MessageType(int responseCode){
        this.MESSAGE_CODE = responseCode;
    }

    public int getCode(){
        return MESSAGE_CODE;
    }

    private static final HashMap<Integer, MessageType> responseTypeMap = new HashMap<>();

    static{
        for(MessageType messageType : MessageType.values()){
            responseTypeMap.put(messageType.getCode(), messageType);
        }
    }

    public static MessageType get(int msgCode) {
        return responseTypeMap.get(msgCode);
    }
}
