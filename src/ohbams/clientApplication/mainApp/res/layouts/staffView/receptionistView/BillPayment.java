package ohbams.clientApplication.mainApp.res.layouts.staffView.receptionistView;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.entity.EntityFormatException;
import ohbams.entity.ServiceCharge;
import ohbams.eventHandler.EventType;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BillPayment implements MessageConsumer {
    public TableView<ServiceCharge> serviceChargeTable;
    public TableColumn<ServiceCharge, Integer> column1;
    public TableColumn<ServiceCharge, String> column2;
    public TableColumn<ServiceCharge, String> column3;
    public TableColumn<ServiceCharge, Float> column4;
    public TableColumn<ServiceCharge, LocalDateTime> column5;

    public Label totalAmount;

    public int bookingIdValue=-1;
    public Label bookingId;
    public Button payedBtn;
    private float totalAmountValue= 0.0f;

    public void initialize(){
        Main.getEventDispatcher().add(this);

        column1.setCellValueFactory(new PropertyValueFactory<>("serviceChargeId"));
        column2.setCellValueFactory(new PropertyValueFactory<>("chargedById"));
        column3.setCellValueFactory(new PropertyValueFactory<>("serviceInfo"));
        column4.setCellValueFactory(new PropertyValueFactory<>("amount"));
        column5.setCellValueFactory(new PropertyValueFactory<>("chargedOn"));
        payedBtn.setDisable(true);
    }

    public void loadTableData(ArrayList<ServiceCharge> serviceChargesList) {
        serviceChargeTable.getItems().clear();
        serviceChargeTable.getItems().addAll(serviceChargesList);

        serviceChargesList.forEach(billingInfo -> {
            serviceChargeTable.getItems().add(billingInfo);
            totalAmountValue += billingInfo.getAmount();
        });
        Platform.runLater(()->{
            totalAmount.setText(totalAmountValue+"");
        });
    }

    public void payed(ActionEvent actionEvent) {
        try{
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.BILL_PAYMENT, bookingIdValue+""));
        }catch (IOException ioException){
            AlertPopup.alert("Server connection down!", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void consume(String... data) throws IOException {
        String []eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0], 2);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){
            case ENTITY_LIST_TRANSFER:
                String[] msgData = MessageFormatHandler.decode(eventData[1], 2);
                bookingIdValue = Integer.parseInt(msgData[0]);
                setBookingIdText(bookingIdValue+"");
                payedBtn.setDisable(false);
                try{
                    ArrayList<ServiceCharge> serviceChargeArrayList = ServiceCharge.parseList(MessageFormatHandler.decode(MessageDelimiter.ENTITY_LIST_DELIMITER, msgData[1]));
                    loadTableData(serviceChargeArrayList);
                }catch (EntityFormatException e){
                    System.err.println("Billing list parsing failed");
                }
                break;
            case SUCCESS:
                serviceChargeTable.getItems().clear();
                setBookingIdText("--");
                totalAmount.setText("00.0");
                payedBtn.setDisable(true);
                AlertPopup.alert("Bill payed successfully!", Alert.AlertType.INFORMATION);
                break;
            case FAILED:
                AlertPopup.alert("Payment Failed :(", Alert.AlertType.ERROR);
                break;
        }
    }

    public void setBookingIdText(String txt){
        Platform.runLater(()->{
            bookingId.setText(txt);
        });
    }
}
