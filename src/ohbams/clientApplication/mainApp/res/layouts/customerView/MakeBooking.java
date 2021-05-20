package ohbams.clientApplication.mainApp.res.layouts.customerView;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.clientApplication.mainApp.res.layouts.components.BookingInput;
import ohbams.constData.MessageDelimiter;
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


public class MakeBooking implements MessageConsumer {

    @FXML
    private StackPane bookingInputPane;
    private BookingInput bookingInputController;

    @FXML
    Button bookBtn;
    @FXML
    TextArea bookingStatus;

    public void initialize() {
        Main.getEventDispatcher().add(this);
        try{

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ohbams/clientApplication/mainApp/res/layouts/components/bookingInput.fxml"));
            bookingInputPane.getChildren().add(fxmlLoader.load());
            bookingInputController = fxmlLoader.getController();

        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void book(ActionEvent actionEvent){
        try {
            String[] bookingInputValues = bookingInputController.getBookingInput();
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.MAKE_BOOKING, Main.getUserId(), bookingInputValues[0], bookingInputValues[1], bookingInputValues[2], bookingInputValues[3]));
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
        switch (eventType){
            case FAILED:
                bookingStatus.appendText(">> Booking Failed :(\n");
                break;
            case SUCCESS:
                String bookingId = eventData[1];
                bookingStatus.appendText(">> Booking applied successfully with booking Id: "+ bookingId +"\n");
                break;
        }
    }
}
