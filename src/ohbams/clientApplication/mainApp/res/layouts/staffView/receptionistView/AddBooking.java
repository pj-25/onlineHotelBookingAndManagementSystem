package ohbams.clientApplication.mainApp.res.layouts.staffView.receptionistView;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.clientApplication.mainApp.res.layouts.components.BookingInput;
import ohbams.constData.MessageDelimiter;
import ohbams.constData.RoomType;
import ohbams.eventHandler.EventType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;

public class AddBooking implements MessageConsumer {


    public TextField userId;
    public ComboBox<String> roomTypeChoice;
    public Button chkRoomAvailabilityBtn;
    public TextArea status;

    @FXML
    private StackPane bookingInputPane;
    private BookingInput bookingInputController;


    public void initialize() {
        Main.getEventDispatcher().add(this);
        try{

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ohbams/clientApplication/mainApp/res/layouts/components/bookingInput.fxml"));
            bookingInputPane.getChildren().add(fxmlLoader.load());
            bookingInputController = fxmlLoader.getController();

        }catch (IOException e){
            System.err.println(e.getMessage());
        }
        for(RoomType roomType: RoomType.values()){
            roomTypeChoice.getItems().add(roomType.toString());
        }
    }

    public void book(ActionEvent actionEvent){
        if(userId.getText().isEmpty()){
            AlertPopup.alert("User Id is required!", Alert.AlertType.ERROR);
            return;
        }
        try {
            String[] bookingInputValues = bookingInputController.getBookingInput();
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.ADD_BOOKING, userId.getText(), bookingInputValues[0], bookingInputValues[1], bookingInputValues[2], bookingInputValues[3]));
        }catch (BookingInput.BookingInputException bookingInputException){
            AlertPopup.alert(bookingInputException.getMessage(), Alert.AlertType.ERROR);
        }catch (IOException e){
            AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void consume(String... data) throws IOException {
        String[] eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0]);

        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        String[] msgData = MessageFormatHandler.decode(eventData[1]);
        switch (eventType){
            case FAILED:
                status.appendText(">> Enable to book\n");
                break;
            case SUCCESS:
                String bookingId = eventData[1];
                status.appendText(">> Booking applied successfully with booking Id: "+ bookingId +"\n");
                break;
            case ROOM_STATUS:
                RoomType roomType = RoomType.get(Integer.parseInt(msgData[0]));
                int unallocatedRooms = Integer.parseInt(msgData[1]);
                if(unallocatedRooms<=0){
                    status.appendText(">> No "+ roomType.toString() +" rooms available!\n");
                }
                else{
                    status.appendText(">> Unallocated "+ roomType.toString()+" rooms: "+ unallocatedRooms +"\n");
                }
                break;
        }
    }

    public void checkRoomAvailability(ActionEvent actionEvent) {
        try {
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.CHECK_ROOM_AVAILABILITY, RoomType.valueOf(roomTypeChoice.getValue()).getCode()+""));
        }catch (IOException ioException){
            AlertPopup.alert("Server Connection Down :(", Alert.AlertType.ERROR);
        }catch (NullPointerException e){
            AlertPopup.alert("Select Room Type!", Alert.AlertType.ERROR);
        }
    }
}
