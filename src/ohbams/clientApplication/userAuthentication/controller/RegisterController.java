package ohbams.clientApplication.userAuthentication.controller;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.constData.UserType;
import ohbams.eventHandler.EventType;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import ohbams.messageHandler.MessageConsumer;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;
import ohbams.serverApplication.databaseHandler.InvalidDataException;


import java.io.IOException;

public class RegisterController implements MessageConsumer {
    @FXML
    StackPane mainPane;
    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    PasswordField cpassword;
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField age;

    @FXML
    TextField emailId;
    @FXML
    TextField phoneNumber;
    @FXML
    RadioButton male;
    @FXML
    RadioButton female;
    @FXML
    RadioButton other;

    @FXML
    RadioButton regularCustomer;
    @FXML
    TextField creditCardNumber;
    @FXML
    DatePicker cardExpiryDate;
    @FXML
    TextField cardType;

    @FXML
    TextField corporateName;
    @FXML
    TextField role;

    @FXML
    TextField receptionCode;

    @FXML
    RadioButton corporateClient;
    @FXML
    RadioButton receptionist;
    @FXML
    RadioButton barOrRestaurantStaff;



    private ToggleGroup gender = new ToggleGroup();
    private ToggleGroup userType = new ToggleGroup();
    private Group regularCustomerInfo = new Group();

    @FXML
    public void initialize(){
        Main.getEventDispatcher().add(this);

        male.setToggleGroup(gender);
        female.setToggleGroup(gender);
        other.setToggleGroup(gender);

        regularCustomer.setToggleGroup(userType);
        corporateClient.setToggleGroup(userType);
        receptionist.setToggleGroup(userType);
        barOrRestaurantStaff.setToggleGroup(userType);
    }

    @FXML
    public void register(){
        try{
            if(password.getText().equals(cpassword.getText())){
                char genderChoice = 'M';
                if(female.isSelected()){
                    genderChoice = 'F';
                }
                else if(other.isSelected()){
                    genderChoice = 'O';
                }
                else if(!male.isSelected()){
                    throw new InvalidDataException("Gender not selected!");
                }

                UserType userType = UserType.REGULAR_CUSTOMER;
                String userData;

                if(regularCustomer.isSelected()){
                    userData = MessageFormatHandler.encode(creditCardNumber.getText(), cardExpiryDate.getValue().toString(), cardType.getText());
                }
                else if(corporateClient.isSelected()){
                    userData = MessageFormatHandler.encode(corporateClient.getText(), role.getText());
                    userType = UserType.CORPORATE_CLIENT;
                }
                else if(receptionist.isSelected()){
                    userData = receptionCode.getText();
                    userType = UserType.RECEPTIONIST;
                }
                else if(barOrRestaurantStaff.isSelected()){
                    userData = "";
                    userType = UserType.RESTAURANT_STAFF;
                }
                else{
                    throw new InvalidDataException("User Type not selected!");
                }

                String requestMsg = MessageFormatHandler.encode(MessageType.REGISTER, userId.getText(), password.getText(), firstName.getText(),lastName.getText(), age.getText(), genderChoice+"",  userType.getCode(), emailId.getText(), phoneNumber.getText(), userData);
                Main.getServerChannel().send(requestMsg);
            }
            else{
                AlertPopup.alert("Password failed to verify:(", Alert.AlertType.ERROR);
            }
        }
        catch (IOException ioException){
            AlertPopup.alert("Enable to register!", Alert.AlertType.ERROR);
        }
        catch (NumberFormatException numberFormatException) {
            AlertPopup.alert("Invalid age!", Alert.AlertType.ERROR);
        }
        catch (NullPointerException npe){
            AlertPopup.alert("All fields are required*", Alert.AlertType.ERROR);
        }
        catch (InvalidDataException e){
            AlertPopup.alert("Error: "+ e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @Override
    public void consume(String... data) throws IOException {
        String []eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0]);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        if(eventType == EventType.SUCCESS){
            AlertPopup.alert("Registered successful :)", Alert.AlertType.INFORMATION);
        }
        else if(eventType == EventType.FAILED){
            AlertPopup.alert("UserID already used!", Alert.AlertType.ERROR);
        }
    }
}
