package ohbams.clientApplication.mainApp.res.layouts.customerView.corporateClientView;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.entity.BillingInfo;
import ohbams.entity.EntityFormatException;
import ohbams.eventHandler.EventType;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;
import java.util.ArrayList;

public class PayMonthlyBill implements MessageConsumer {
    public TableView<BillingInfo> monthlyBillInfoTable;
    public Label totalAmount;
    public Button payBtn;
    public TableColumn<BillingInfo, Integer> column1;
    public TableColumn<BillingInfo, Integer> column2;
    public TableColumn<BillingInfo, Float> column3;
    public TableColumn<BillingInfo, Character> column4;

    private float totalAmountValue= 0.0f;

    public void initialize(){
        Main.getEventDispatcher().add(this);

        column1.setCellValueFactory(new PropertyValueFactory<>("billId"));
        column2.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        column3.setCellValueFactory(new PropertyValueFactory<>("amount"));
        column4.setCellValueFactory(new PropertyValueFactory<>("status"));

        sendRequest();
    }

    private void sendRequest() {
        try {
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.GET_PENDING_MONTHLY_BILLS, Main.getUserId()));
        } catch (IOException ignore) {
        }
    }


    @Override
    public void consume(String... data) throws IOException {
        String []eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0], 2);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){

            case ENTITY_LIST_TRANSFER:
                try{
                    ArrayList<BillingInfo> billingInfoArrayList = BillingInfo.parseList(MessageFormatHandler.decode(MessageDelimiter.ENTITY_LIST_DELIMITER, eventData[1]));
                    loadData(billingInfoArrayList);
                }catch (EntityFormatException e){
                    System.err.println("Billing list parsing failed");
                }
                break;
            case SUCCESS:
                monthlyBillInfoTable.getItems().clear();
                AlertPopup.alert("Bill payed successfully!", Alert.AlertType.INFORMATION);
                break;
            case FAILED:
                AlertPopup.alert("Payment Failed :(", Alert.AlertType.ERROR);
                break;
        }
    }

    public void loadData(ArrayList<BillingInfo> billingInfoArrayList) {
        monthlyBillInfoTable.getItems().clear();
        monthlyBillInfoTable.getItems().addAll(billingInfoArrayList);

        billingInfoArrayList.forEach(billingInfo -> {
            monthlyBillInfoTable.getItems().add(billingInfo);
            totalAmountValue += billingInfo.getAmount();
        });
        totalAmount.setText(totalAmountValue+"");
    }

    public void pay(ActionEvent actionEvent) {
        try{
            Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.PAY_MONTHLY_BILL, Main.getUserId()));
        }catch (IOException ioException){
            AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
        }
    }
}
