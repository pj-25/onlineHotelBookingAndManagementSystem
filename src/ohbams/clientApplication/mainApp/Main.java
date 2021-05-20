package ohbams.clientApplication.mainApp;

import ohbams.clientApplication.networkConnection.ServerChannel;
import ohbams.constData.UserType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static EventDispatcher eventDispatcher = new EventDispatcher();
    private static ServerChannel serverChannel = null;
    private static String userId;
    private static UserType userType;
    private static Stage mainStage;


    @Override
    public void start(Stage primaryStage) throws Exception{
        mainStage = primaryStage;

        jumpTo("/ohbams/clientApplication/userAuthentication/res/layouts/authentication");

        primaryStage.setTitle("Luton Hotel: Online Hotel Booking and Management System");
            primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest((windowEvent)->{
            if(serverChannel!=null)
                serverChannel.close();
        });
        primaryStage.show();
        createConnection();
    }

    public static EventDispatcher getEventDispatcher(){
        return eventDispatcher;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void setMainStage(Stage mainStage) {
        Main.mainStage = mainStage;
    }

    public static void setEventConsumeHandler(EventDispatcher eventDispatcher) {
        Main.eventDispatcher = eventDispatcher;
    }

    public static UserType getUserType(){
        return userType;
    }

    public static String getUserId(){
        return userId;
    }

    public static void setUserId(String userId) {
        Main.userId = userId;
    }

    public static void setUserType(UserType userType) {
        Main.userType = userType;
    }

    public static void jumpTo(String layout) {
        Platform.runLater(()->{
            try {
                Scene scene =new Scene(FXMLLoader.load(Main.class.getResource(layout+".fxml")));
                scene.getStylesheets().add("/ohbams/clientApplication/res/stylesheets/style.css");
                Main.getMainStage().setScene(scene);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public static ServerChannel getServerChannel() {
        return serverChannel;
    }

    public static void setServerChannel(ServerChannel serverChannel) {
        Main.serverChannel = serverChannel;
    }

    public void createConnection(){
        try{
            serverChannel = new ServerChannel((data)->{
                new Thread(()->{
                    try {
                        eventDispatcher.consume(data);
                    } catch (IOException ioException) {
                        System.err.println("Enable to dispatch event!");
                    }
                }).start();
            });
            serverChannel.run();
        }catch (IOException e){
            AlertPopup.alert("Server down :(", Alert.AlertType.ERROR);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
