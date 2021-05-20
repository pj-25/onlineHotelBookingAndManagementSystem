package ohbams.clientApplication.userAuthentication.controller;

import ohbams.clientApplication.mainApp.AlertPopup;
import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.MessageDelimiter;
import ohbams.constData.UserType;
import ohbams.eventHandler.EventType;
import javafx.application.Platform;
import ohbams.messageHandler.MessageConsumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import ohbams.messageHandler.MessageFormatHandler;
import ohbams.messageHandler.MessageType;


import java.io.IOException;

public class LoginController implements MessageConsumer {
    @FXML
    TextField loginId;

    @FXML
    TextField password;

    @FXML
    CheckBox showBox;


    @FXML
    public void initialize(){
        Main.getEventDispatcher().add(this);

        Tooltip passwordShow = new Tooltip();
        //passwordShow.setAutoHide(false);
        passwordShow.textProperty().bind(password.textProperty());
        password.setTooltip(passwordShow);
    }

    @FXML
    public void login(ActionEvent actionEvent){
        String errorMsg = "Invalid loginId/Password :(";

        try{
            if(loginId.getText().isEmpty()){
                AlertPopup.alert("Login ID is required!", Alert.AlertType.ERROR);
            }
            else if(password.getText().isEmpty()){
                AlertPopup.alert("Password is required!", Alert.AlertType.ERROR);
            }
            else{
                Main.getServerChannel().send(MessageFormatHandler.encode(MessageType.LOGIN, loginId.getText(), password.getText()));
            }
        }catch(NullPointerException npe){
            AlertPopup.alert("Connection not established :(", Alert.AlertType.ERROR);
        }
        catch (IOException e){
            System.err.println(e);
            AlertPopup.alert(errorMsg, Alert.AlertType.ERROR);
        }
    }


    @FXML
    public void showPassword(ActionEvent actionEvent){
        if(showBox.isSelected()){
            double x = password.getScene().getWindow().getX();
            double y = password.getScene().getWindow().getY();
            Bounds localBounds = password.localToScene(password.getBoundsInLocal());

            password.getTooltip().show(password, x+ localBounds.getMinX(), y+ localBounds.getMaxY());
        }
        else{
            password.getTooltip().hide();
        }
    }

    public void openBaseApp() throws IOException{
        jumpToApp("base");
    }

    public void jumpTo(String layout) throws IOException{
        Platform.runLater(()->{
            password.getTooltip().hide();
        });
        Main.jumpTo(layout);
    }

    public void jumpToApp(String userView) throws IOException {
        jumpTo("/ohbams/clientApplication/mainApp/res/layouts/" +userView);
    }


    @Override
    public void consume(String... data) throws IOException {
        String[] eventData = MessageFormatHandler.decode(MessageDelimiter.EVENT_TYPE_DELIMITER, data[0], 2);
        EventType eventType = EventType.get(Integer.parseInt(eventData[0]));
        if(eventType == EventType.SUCCESS){
            AlertPopup.alert("Login successful :)", Alert.AlertType.INFORMATION);
            Main.setUserId(loginId.getText());
            Main.setUserType(UserType.get(eventData[1]));
            openBaseApp();
        }
        else if(eventType == EventType.FAILED){
            AlertPopup.alert("Invalid loginID/Password", Alert.AlertType.ERROR);
        }
    }
}
