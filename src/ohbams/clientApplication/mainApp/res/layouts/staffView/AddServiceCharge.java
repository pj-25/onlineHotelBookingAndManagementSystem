package ohbams.clientApplication.mainApp.res.layouts.staffView;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.entity.EntityFormatException;
import ohbams.entity.ServiceCharge;
import ohbams.eventHandler.EventType;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;

import java.io.IOException;

public class AddServiceCharge implements MessageConsumer {
    public TextField amount;
    public TextField serviceInfo;
    public TextField bookingId;
    public TextArea status;

    public void initialize(){
        Main.getEventDispatcher().add(this);
    }

    public void addServiceCharge(ActionEvent actionEvent) {
        if(serviceInfo.getText().isEmpty()){
            AlertPopup.alert("Service Info is required!", Alert.AlertType.ERROR);
            serviceInfo.requestFocus();
        }
        else if(bookingId.getText().isEmpty()){
            AlertPopup.alert("Booking Id is required!", Alert.AlertType.ERROR);
            serviceInfo.requestFocus();
        }
        else if(amount.getText().isEmpty()){
            AlertPopup.alert("Amount is required!", Alert.AlertType.ERROR);
            amount.requestFocus();
        }
        else{
            try {
                float charges = Float.parseFloat(amount.getText());
                Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.ADD_SERVICE_CHARGE, Main.getUserId(), serviceInfo.getText(), bookingId.getText(), charges+""));
            }catch (NumberFormatException e){
                AlertPopup.alert("Amount must be numeric value", Alert.AlertType.ERROR);
            }catch (IOException e){
                AlertPopup.alert("Server connection down :(", Alert.AlertType.ERROR);
            }
        }

    }

    @Override
    public void consume(String... data) throws IOException {
        String[] eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0]);

        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        switch (eventType){
            case FAILED:
                status.appendText(">> Invalid booking Id: "+ bookingId.getText()+"\n");
                break;
            case SUCCESS:
                try{
                    ServiceCharge serviceCharge = ServiceCharge.parse(MessageFormatHandler.decode(eventData[1]));
                    status.appendText(">> Service Charge added successfully!\n");
                    status.appendText(" [\n  Service Charge Id: "+serviceCharge.getServiceChargeId()+
                            "\n  ChargedBy Id: "+serviceCharge.getChargedById()+
                            "\n  Service Info: "+serviceCharge.getServiceInfo()+
                            "\n  Booking Id: "+serviceCharge.getBookingId()+
                            "\n  Amount: "+serviceCharge.getAmount()+
                            "\n  Charged On: "+serviceCharge.getServiceChargeId()+
                            "\n ]\n");
                }catch (EntityFormatException e){
                    System.err.println("Service charge parsing failed");
                }
                break;


        }
    }
}
