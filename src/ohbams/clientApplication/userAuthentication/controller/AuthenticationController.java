package ohbams.clientApplication.userAuthentication.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;


public class AuthenticationController {
    @FXML
    TabPane mainPane;
    @FXML
    Tab loginTab;
    @FXML
    Tab registerTab;

    @FXML
    public void initialize() throws IOException {
        ScrollPane loginPage = FXMLLoader.load(getClass().getResource("/ohbams/clientApplication/userAuthentication/res/layouts/login.fxml"));
        loginTab.setContent(loginPage);

        ScrollPane registerPage = FXMLLoader.load(getClass().getResource("/ohbams/clientApplication/userAuthentication/res/layouts/register.fxml"));
        registerTab.setContent(registerPage);
    }
}
