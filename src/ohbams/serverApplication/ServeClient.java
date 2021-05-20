package ohbams.serverApplication;

import ohbams.constData.BookingStatus;
import ohbams.constData.MessageDelimiter;
import ohbams.constData.RoomType;
import ohbams.constData.UserType;
import ohbams.entity.BillingInfo;
import ohbams.entity.BookingInfo;
import ohbams.entity.ServiceCharge;
import ohbams.eventHandler.EventType;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;
import ohbams.serverApplication.databaseHandler.LutonDB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServeClient extends Thread{

    private Socket clientSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private LutonDB lutonDB;
    private boolean isConnected = false;

    private String userID = null;

    public ServeClient(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        inStream = new DataInputStream(clientSocket.getInputStream());
        outStream = new DataOutputStream(clientSocket.getOutputStream());
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public DataInputStream getInStream() {
        return inStream;
    }

    public void setInStream(DataInputStream inStream) {
        this.inStream = inStream;
    }

    public DataOutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(DataOutputStream outStream) {
        this.outStream = outStream;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public void run() {
        isConnected = true;
        while(isConnected()){
            serve();
        }
        close();
    }

    public void serve(){
        try{
            String msg = readMsg();

            //System.out.println("Received Msg >> "+msg);

            String[] reqData = MessageFormatHandler.decode(MessageDelimiter.REQUEST_TYPE_DELIMITER, msg);
            MessageType reqType = MessageType.get(Integer.parseInt(reqData[0]));

            if(userID!=null || reqType == MessageType.REGISTER || reqType == MessageType.LOGIN){
                lutonDB = new LutonDB();

                boolean responseStatus;
                String responseMsg = "";

                String[] msgData = MessageFormatHandler.decode(reqData[1]);

                switch (reqType){
                    case LOGIN:
                        responseStatus = lutonDB.isValidUser(msgData[0], msgData[1]);
                        if(responseStatus){
                            userID = msgData[0];

                            Server.addClient(userID, this);

                            String userTypeCode = lutonDB.getUserType(userID);
                            responseMsg = MessageFormatHandler.encode(MessageType.LOGIN, EventType.SUCCESS, userTypeCode);
                        }
                        else {
                            responseMsg = MessageFormatHandler.encode(MessageType.LOGIN, EventType.FAILED, "");
                        }

                        break;

                    case REGISTER:
                        String []userData = new String[msgData.length-9];
                        System.arraycopy(msgData, 9, userData, 0, msgData.length - 9);
                        responseStatus = lutonDB.registerUser(msgData[0], msgData[1], msgData[2], msgData[3], Integer.parseInt(msgData[4]), msgData[5].charAt(0), UserType.get(msgData[6]), msgData[7], msgData[8], userData);
                        if(responseStatus){
                            responseMsg = MessageFormatHandler.encode(MessageType.REGISTER, EventType.SUCCESS, "ok");
                        }
                        else {
                            responseMsg = MessageFormatHandler.encode(MessageType.REGISTER, EventType.FAILED, "");
                        }
                        break;

                    case MAKE_BOOKING:
                    case ADD_BOOKING:
                        String customerId = msgData[0];
                        int bookingId = lutonDB.addBooking(customerId, LocalDateTime.parse(msgData[1]), LocalDateTime.parse(msgData[2]), Integer.parseInt(msgData[3]), RoomType.get(Integer.parseInt(msgData[4])));
                        if(bookingId!=-1){
                            responseMsg = MessageFormatHandler.encode(reqType, EventType.SUCCESS, bookingId+"");
                            if(Server.isClientOnline(customerId))
                                Server.getClient(customerId).writeMsg(MessageFormatHandler.encode(MessageType.VIEW_BOOKING, EventType.ENTITY_TRANSFER, lutonDB.getBookingInfoById(bookingId).toString()));
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(reqType, EventType.FAILED, "");
                        }
                        break;

                    case VIEW_BOOKING:
                        ArrayList<BookingInfo> currentBookings = lutonDB.getAllCurrentBookingFor(msgData[0]);
                        if(currentBookings!=null){
                            responseMsg = MessageFormatHandler.encode(MessageType.VIEW_BOOKING, EventType.SUCCESS, MessageFormatHandler.encode(currentBookings));
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(MessageType.VIEW_BOOKING, EventType.FAILED, "");
                        }
                        break;
                    case CHANGE_BOOKING:
                        if(lutonDB.updateBooking(Integer.parseInt(msgData[0]), LocalDateTime.parse(msgData[1]), LocalDateTime.parse(msgData[2]), Integer.parseInt(msgData[3]), RoomType.get(Integer.parseInt(msgData[4])))){
                            responseMsg = MessageFormatHandler.encode(MessageType.CHANGE_BOOKING, EventType.UPDATED, msgData[0]);
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(MessageType.CHANGE_BOOKING, EventType.NOT_UPDATED, "");
                        }
                        break;
                    case CANCEL_BOOKING:
                        bookingId = Integer.parseInt(msgData[0]);
                        if(lutonDB.updateBookingStatus(bookingId, BookingStatus.CANCELED)){
                            responseMsg = MessageFormatHandler.encode(MessageType.CANCEL_BOOKING, EventType.CANCELED, msgData[0]);
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(MessageType.CANCEL_BOOKING, EventType.NOT_CANCELED, "");
                        }
                        break;
                    case PAY_MONTHLY_BILL:
                        String corporateClientId = msgData[0];
                        if(lutonDB.payMonthlyBill(corporateClientId)){
                            responseMsg = MessageFormatHandler.encode(MessageType.PAY_MONTHLY_BILL, EventType.SUCCESS, "ok");
                        }else{
                            responseMsg = MessageFormatHandler.encode(MessageType.PAY_MONTHLY_BILL, EventType.FAILED, "");
                        }
                        break;
                    case BILL_PAYMENT:
                        bookingId = Integer.parseInt(msgData[0]);
                        if(lutonDB.payBill(bookingId)){
                            responseMsg = MessageFormatHandler.encode(MessageType.BILL_PAYMENT, EventType.SUCCESS, bookingId+"");
                        }else{
                            responseMsg = MessageFormatHandler.encode(MessageType.BILL_PAYMENT, EventType.FAILED, "");
                        }
                        break;
                    case CHECK_ROOM_AVAILABILITY:
                        RoomType roomType = RoomType.get(Integer.parseInt(msgData[0]));
                        int unallocatedRooms = lutonDB.checkAvailability(roomType);
                        responseMsg = MessageFormatHandler.encode(MessageType.CHECK_ROOM_AVAILABILITY, EventType.ROOM_STATUS, roomType.getCode()+"", unallocatedRooms+"");
                        break;
                    case CHECKIN_GUEST:
                        bookingId = Integer.parseInt(msgData[0]);
                        if(lutonDB.updateBookingStatus(bookingId, BookingStatus.ALLOCATED)){
                            responseMsg = MessageFormatHandler.encode(MessageType.CHECKIN_GUEST, EventType.SUCCESS, bookingId+"", lutonDB.getRoomIdFor(bookingId));
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(MessageType.CHECKIN_GUEST, EventType.FAILED, "");
                        }
                        break;
                    case CHECKOUT_GUEST:
                        bookingId = Integer.parseInt(msgData[0]);
                        if(lutonDB.updateBookingStatus(bookingId, BookingStatus.CHECKOUT)){
                            responseMsg = MessageFormatHandler.encode(MessageType.CHECKOUT_GUEST, EventType.SUCCESS, bookingId+"");
                            writeMsg(MessageFormatHandler.encode(MessageType.BILL_PAYMENT, EventType.ENTITY_LIST_TRANSFER, bookingId+"", MessageFormatHandler.encode(lutonDB.getAllServiceChargesFor(bookingId))));
                        }
                        else{
                            responseMsg = MessageFormatHandler.encode(MessageType.CHECKOUT_GUEST, EventType.FAILED, "");
                        }
                        break;
                    case ADD_SERVICE_CHARGE:
                        int scId = lutonDB.addServiceCharge(msgData[0], msgData[1], Integer.parseInt(msgData[2]), Float.parseFloat(msgData[3]));
                        if(scId!=-1){
                            ServiceCharge serviceCharge = lutonDB.getServiceChargeById(scId);
                            responseMsg = MessageFormatHandler.encode(MessageType.ADD_SERVICE_CHARGE, EventType.SUCCESS, serviceCharge.toString());
                        }else{
                            responseMsg = MessageFormatHandler.encode(MessageType.ADD_SERVICE_CHARGE, EventType.FAILED, "");
                        }
                        break;
                    case ENTITY_DATA:
                        break;
                    case GET_PENDING_MONTHLY_BILLS:
                        corporateClientId = msgData[0];
                        ArrayList<BillingInfo> pendingMonthlyBills = lutonDB.getAllPendingMonthlyBillsFor(corporateClientId);
                        if(pendingMonthlyBills!=null){
                            responseMsg = MessageFormatHandler.encode(MessageType.GET_PENDING_MONTHLY_BILLS, EventType.ENTITY_LIST_TRANSFER, MessageFormatHandler.encode(pendingMonthlyBills));
                        }else{
                            responseMsg = MessageFormatHandler.encode(MessageType.GET_PENDING_MONTHLY_BILLS, EventType.FAILED, "");
                        }
                        break;
                }

                writeMsg(responseMsg);
                lutonDB.close();
            }
            else{
                writeMsg(MessageType.UNAUTHORIZED_USER);
            }

        }catch (IOException e){
            System.err.println( "User("+userID+") disconnected...");
            close();
        }catch(SQLException|ClassNotFoundException exception){
            System.err.println(exception);
        }catch (IndexOutOfBoundsException indexError){
            writeMsg(MessageType.INVALID_MSG, "Invalid arguments");
            System.err.println("Invalid msg");
        }
    }

    public String readMsg() throws IOException{
        return inStream.readUTF();
    }

    public void writeMsg(String msg){
        try{
            outStream.writeUTF(msg);
            //System.out.println("Message Sent >> "+ msg);
        }catch (IOException ioException){
            System.err.println("Enable to send msg!");
        }
    }

    public void writeMsg(MessageType msgType, EventType eventType, String ... msgData){
        writeMsg(MessageFormatHandler.encode(msgType, eventType,msgData));
    }

    public void writeMsg(MessageType resType, String ...msgData){
        writeMsg(MessageFormatHandler.encode(MessageDelimiter.RESPONSE_TYPE_DELIMITER, resType, msgData));
    }

    public void close(){
        isConnected = false;
        try{
            Server.removeClient(userID);
            clientSocket.close();
        }catch (IOException e){
            System.err.println("Unable to close client connection");
        }
    }

}
