package ohbams.clientApplication.mainApp.res.layouts;

import ohbams.clientApplication.mainApp.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class Base {

    @FXML
    TabPane tabPane;

    @FXML
    public void initialize(){
        switch (Main.getUserType()){
            case RECEPTIONIST:
                addReceptionistTabs();
                break;
            case BAR_STAFF:
            case RESTAURANT_STAFF:
                addTab("Add Service Charge", "staffView/addServiceCharge");
                break;
            case REGULAR_CUSTOMER:
                addCustomerTabs();
                break;
            case CORPORATE_CLIENT:
                addCustomerTabs();
                addTab("Pay Monthly Bill", "customerView/corporateClientView/payMonthlyBill");
                break;
        }
    }

    public void addCustomerTabs(){
        String path = "customerView/";
        addTab("Make Booking", path+"makeBooking");
        addTab("View & Edit Booking", path+"viewAndEditBooking");
    }

    public void addReceptionistTabs(){
        String path = "staffView/receptionistView/";
        addTab("Add Booking", path+"addBooking");
        addTab("Add service charge", "staffView/addServiceCharge");
        addTab("Check In", path+"checkInGuest");
        addTab("Check Out", path+"checkOutGuest");
        addTab("Bill Payment", path+"billPayment");
    }

    public void addTab(String tabName, String layout){
        try{
            Tab tab = new Tab(tabName);
            tab.setContent(FXMLLoader.load(getClass().getResource("/ohbams/clientApplication/mainApp/res/layouts/" +layout+".fxml")));
            tabPane.getTabs().add(tab);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
