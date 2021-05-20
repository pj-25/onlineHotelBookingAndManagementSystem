package ohbams.clientApplication.mainApp;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertPopup {

    public static void alert(String msg, Alert.AlertType alertType){
        Platform.runLater(()->{
            Alert alert = new Alert(alertType);
            alert.setContentText(msg);
            alert.show();
        });
    }

}
