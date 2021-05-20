package ohbams.clientApplication.mainApp;

import ohbams.constData.MessageDelimiter;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;
import java.util.HashMap;

public class EventDispatcher extends HashMap<String, MessageConsumer> implements MessageConsumer {

    public void add(MessageConsumer msgConsumer){
        put(msgConsumer.getClass().getSimpleName(), msgConsumer);
    }

    @Override
    public void consume(String ...msgData) throws IOException {
        String[] responseData = MessageFormatHandler.decode(MessageDelimiter.RESPONSE_TYPE_DELIMITER, msgData[0], 2);
        MessageType messageType = MessageType.get(Integer.parseInt(responseData[0]));
        switch (messageType){
            case UNAUTHORIZED_USER:
                System.err.println("Please Login!!");
                break;
            case INVALID_MSG:
                System.err.println("Invalid msg request");
                break;
            case LOGIN:
                get("LoginController").consume(responseData[1]);
                break;
            case REGISTER:
                get("RegisterController").consume(responseData[1]);
                break;
            case MAKE_BOOKING:
                get("MakeBooking").consume(responseData[1]);
                break;
            case VIEW_BOOKING:
                case CHANGE_BOOKING:
            case CANCEL_BOOKING:
                get("ViewAndEditBooking").consume(responseData[1]);
                break;
            case PAY_MONTHLY_BILL:
                get("PayMonthlyBill").consume(responseData[1]);
                break;
            case BILL_PAYMENT:
                get("BillPayment").consume(responseData[1]);
                break;
            case CHECKIN_GUEST:
                get("CheckInGuest").consume(responseData[1]);
                break;
            case CHECKOUT_GUEST:
                get("CheckOutGuest").consume(responseData[1]);
                break;
            case ADD_BOOKING:
            case CHECK_ROOM_AVAILABILITY:
                get("AddBooking").consume(responseData[1]);
                break;
            case ADD_SERVICE_CHARGE:
                get("AddServiceCharge").consume(responseData[1]);
                break;
            case ENTITY_DATA:
                System.out.println(responseData[1]);
                break;
        }

    }
}
