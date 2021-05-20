package ohbams.clientApplication.mainApp.res.layouts.staffView.receptionistView;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.eventHandler.EventType;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;

public class CheckOutGuest implements MessageConsumer {

    public TextField bookingId;
    public Button checkOutBtn;

    public void initialize(){
        Main.getEventDispatcher().add(this);
    }

    public void checkout(ActionEvent actionEvent) {
        if(bookingId.getText().isEmpty()){
            AlertPopup.alert("Booking Id is required*", Alert.AlertType.ERROR);
            bookingId.requestFocus();
            return;
        }
        try{
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.CHECKOUT_GUEST, Integer.parseInt(bookingId.getText())+""));
        }catch (IOException ioException){
            AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
        }catch (NumberFormatException e){
            AlertPopup.alert("Booking Id must be numeric value", Alert.AlertType.ERROR);
            bookingId.requestFocus();
        }
    }


    @Override
    public void consume(String... data) throws IOException {
        String[] eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0]);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){
            case SUCCESS:
                int bookingId = Integer.parseInt(eventData[1]);
                AlertPopup.alert("Checkout successfully for booking Id: "+bookingId+"\n**(Jump to Bill Payment tab for payment info)", Alert.AlertType.INFORMATION);
                break;
            case FAILED:
                AlertPopup.alert("Invalid booking Id :(", Alert.AlertType.ERROR);
                this.bookingId.requestFocus();
                break;
        }
    }
}
