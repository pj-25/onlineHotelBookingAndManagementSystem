package ohbams.clientApplication.mainApp.res.layouts.customerView;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.clientApplication.mainApp.res.layouts.components.BookingInput;
import ohbams.constData.BookingStatus;
import ohbams.constData.MessageDelimiter;
import ohbams.entity.BookingInfo;
import ohbams.entity.EntityFormatException;
import ohbams.eventHandler.EventType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ViewAndEditBooking implements MessageConsumer {

    public StackPane bookingInputPane;
    public Button cancelBtn;
    public Button changeBtn;
    public VBox editBookingPane;

    @FXML
    private TableView<BookingInfo> bookingInfoTable;
    @FXML
    private ScrollPane viewPane;
    @FXML
    private VBox viewPaneChild;
    @FXML
    private Label pageTitle;
    @FXML
    private Button refreshBtn;

    @FXML
    private TableColumn<BookingInfo, Integer> column1;
    @FXML
    private TableColumn<BookingInfo, String> column2;
    @FXML
    private TableColumn<BookingInfo, LocalDateTime> column3;
    @FXML
    private TableColumn<BookingInfo, BookingStatus> column4;
    @FXML
    private TableColumn<BookingInfo, LocalDateTime> column5;
    @FXML
    private TableColumn<BookingInfo, LocalDateTime> column6;
    @FXML
    private TableColumn<BookingInfo, Integer> column7;
    @FXML
    private TableColumn<BookingInfo, String> column8;


    private BookingInput bookingInputController;

    private BookingInfo selectedBookingInfo;
    private int selectedRowIndex;

    public void initialize(){
        Main.getEventDispatcher().add(this);

        column1.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        column2.setCellValueFactory(new PropertyValueFactory<>("bookingOn"));
        column3.setCellValueFactory(new PropertyValueFactory<>("bookingStatus"));
        column4.setCellValueFactory(new PropertyValueFactory<>("checkInOn"));
        column5.setCellValueFactory(new PropertyValueFactory<>("checkOutOn"));
        column6.setCellValueFactory(new PropertyValueFactory<>("totalGuests"));
        column7.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        column8.setCellValueFactory(new PropertyValueFactory<>("roomId"));

        try{

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ohbams/clientApplication/mainApp/res/layouts/components/bookingInput.fxml"));
            bookingInputPane.getChildren().add(fxmlLoader.load());
            bookingInputController = fxmlLoader.getController();

        }catch (IOException e){
            System.err.println(e.getMessage());
        }

        editBookingPane.setDisable(true);
        sendRequest();
    }

    public void sendRequest(){
        try {
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.VIEW_BOOKING, Main.getUserId()));
        }
        catch (IOException e){
            AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
        }
    }


    @Override
    public void consume(String... data) throws IOException {
        String []eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0], 2);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){
            case FAILED:
                System.out.println("No Booking data found");
                break;
            case SUCCESS:
                try{
                    ArrayList<BookingInfo> bookingList = BookingInfo.parseList(MessageFormatHandler.decode(MessageDelimiter.ENTITY_LIST_DELIMITER, eventData[1]));
                    loadData(bookingList);
                }catch (EntityFormatException e){
                    System.err.println("Booking list parsing failed");
                }
                break;
            case UPDATED:
                bookingInfoTable.getItems().remove(selectedRowIndex);
                bookingInfoTable.getItems().add(selectedBookingInfo);
                AlertPopup.alert("Booking updated successfully for booking Id: "+eventData[1], Alert.AlertType.INFORMATION);
                break;
            case NOT_UPDATED:
                AlertPopup.alert("Update failed :(, Invalid data", Alert.AlertType.ERROR);
            case CANCELED:
                bookingInfoTable.getItems().remove(selectedRowIndex);
                AlertPopup.alert("Booking canceled successfully for booking Id: "+ eventData[1], Alert.AlertType.INFORMATION);
                bookingInputPane.setDisable(true);
                break;
            case NOT_CANCELED:
                AlertPopup.alert("Booking Cancellation failed :(\nUnallocated booking can only be canceled", Alert.AlertType.ERROR);
                break;
            case ENTITY_TRANSFER:
                try{
                    BookingInfo newBookingInfo = BookingInfo.parse(MessageFormatHandler.decode(eventData[1]));
                    addBookingInfo(newBookingInfo);
                }catch (EntityFormatException e){
                    System.err.println(e);
                }
                break;
        }
    }

    public void addBookingInfo(BookingInfo bookingInfo){
        bookingInfoTable.getItems().add(bookingInfo);
    }

    public void loadData(ArrayList<BookingInfo> bookingInfoList){
        bookingInfoTable.getItems().clear();
        bookingInfoTable.getItems().addAll(bookingInfoList);
    }

    public void refresh(ActionEvent actionEvent){
        sendRequest();
    }

    public void bookingInfoSelection(javafx.scene.input.MouseEvent mouseEvent) throws BookingInput.BookingInputException {
        selectedBookingInfo =  bookingInfoTable.getSelectionModel().getSelectedItem();
        if(selectedBookingInfo!=null){
            selectedRowIndex = bookingInfoTable.getSelectionModel().getSelectedIndex();
            bookingInputController.setBookingInput(selectedBookingInfo.getCheckInOn(), selectedBookingInfo.getCheckOutOn(), selectedBookingInfo.getTotalGuests(), selectedBookingInfo.getRoomType());
            editBookingPane.setDisable(false);
        }
        else{
            selectedRowIndex = -1;
            editBookingPane.setDisable(true);
        }
    }


    public void cancelBooking(ActionEvent actionEvent) {
        try {
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.CANCEL_BOOKING, selectedBookingInfo.getBookingId()+""));
        }catch (IOException e){
            AlertPopup.alert("Enable to cancel booking, Server connection down:(", Alert.AlertType.ERROR);
        }
    }

    public void changeBooking(ActionEvent actionEvent) {
        try {
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.CHANGE_BOOKING, selectedBookingInfo.getBookingId()+"", MessageFormatHandler.encode(bookingInputController.getBookingInput())));
        }catch (IOException e){
            AlertPopup.alert("Enable to change booking, Server connection down:(", Alert.AlertType.ERROR);
        }catch (BookingInput.BookingInputException bookingInputException){
            AlertPopup.alert(bookingInputException.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
