package ohbams.clientApplication.mainApp.res.layouts.staffView.receptionistView;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.eventHandler.EventType;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;

public class CheckInGuest implements MessageConsumer {
    public TextField bookingId;
    public Button checkInBtn;
    public TextArea status;

    public void initialize(){
        Main.getEventDispatcher().add(this);
    }

    @Override
    public void consume(String... data) throws IOException {
        String[] eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0]);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){
            case SUCCESS:
                String[] msgData = MessageFormatHandler.decode(eventData[1]);
                int bookingId = Integer.parseInt(msgData[0]);
                String roomId = msgData[1];
                AlertPopup.alert("CheckIn successfully!\n Booking Id: "+bookingId
                        +"\n RoomId: "+roomId, Alert.AlertType.INFORMATION);
                break;
            case FAILED:
                AlertPopup.alert("Invalid booking Id :(", Alert.AlertType.ERROR);
                this.bookingId.requestFocus();
                break;
        }
    }

    public void checkIn(ActionEvent actionEvent) {
        if(bookingId.getText().isEmpty()){
            AlertPopup.alert("Booking Id is required*", Alert.AlertType.ERROR);
            bookingId.requestFocus();
            return;
        }
        try{
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.CHECKIN_GUEST, Integer.parseInt(bookingId.getText())+""));
        }catch (IOException ioException){
            AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
        }catch (NumberFormatException e){
            AlertPopup.alert("Booking Id must be numeric value", Alert.AlertType.ERROR);
            bookingId.requestFocus();
        }
    }
}
