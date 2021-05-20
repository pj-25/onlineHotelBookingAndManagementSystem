package ohbams.clientApplication.mainApp.res.layouts.components;

import ohbams.clientApplication.mainApp.Main;
import ohbams.constData.RoomType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ohbams.messageHandler.MessageConsumer;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingInput implements MessageConsumer {
    public DatePicker checkInOnDate;
    public Spinner<Integer> checkInHrs;
    public Spinner<Integer> checkInMins;
    public DatePicker checkOutOnDate;
    public Spinner<Integer> checkOutHrs;
    public Spinner<Integer> checkOutMins;

    @FXML
    TextField totalGuests;
    @FXML
    ComboBox<String> roomTypeChoice;

    public void initialize(){
        Main.getEventDispatcher().add(this);
        for(RoomType roomType: RoomType.values()){
            roomTypeChoice.getItems().add(roomType.toString());
        }
        checkInHrs.setEditable(true);
    }

    public LocalDateTime getCheckInDateTime(){
        return LocalDateTime.of(checkInOnDate.getValue(), LocalTime.of(checkInHrs.getValue(), checkInMins.getValue()));
    }

    public LocalDateTime getCheckOutDateTime(){
        return LocalDateTime.of(checkOutOnDate.getValue(), LocalTime.of(checkOutHrs.getValue(), checkOutMins.getValue()));
    }

    public String[] getBookingInput() throws BookingInputException{
        String[] inputValues = new String[4];
        try {
            inputValues[0] = null;
            inputValues[0] = getCheckInDateTime().toString();
            inputValues[1] = getCheckOutDateTime().toString();
            inputValues[2] = Integer.parseInt(totalGuests.getText())+"";
            inputValues[3] = RoomType.valueOf(roomTypeChoice.getValue()).getCode()+"";
            return inputValues;
        }catch (NullPointerException x){
            if(inputValues[0]==null){
                checkInOnDate.requestFocus();
            }else if(inputValues[1]==null){
                checkOutOnDate.requestFocus();
            }
            else {
                roomTypeChoice.requestFocus();
                throw new BookingInputException("Room Type is required*");
            }
            throw new BookingInputException("Select appropriate dates");
        }
        catch (NumberFormatException y){
            totalGuests.requestFocus();
            throw new BookingInputException("Enter total guests value as integer");
        }
    }

    public void setBookingInput(LocalDateTime checkInValue, LocalDateTime checkOutValue, int totalGuestsValue, RoomType roomTypeValue) throws BookingInputException{
        try{
            checkInOnDate.setValue(checkInValue.toLocalDate());
            checkInHrs.getValueFactory().setValue(checkInValue.toLocalTime().getHour());
            checkInMins.getValueFactory().setValue(checkInValue.toLocalTime().getMinute());
            checkOutOnDate.setValue(checkOutValue.toLocalDate());
            checkOutHrs.getValueFactory().setValue(checkOutValue.toLocalTime().getHour());
            checkOutMins.getValueFactory().setValue(checkOutValue.toLocalTime().getMinute());
            totalGuests.setText(totalGuestsValue+"");
            roomTypeChoice.setValue(roomTypeValue.toString());
        }catch (Exception e){
            throw new BookingInputException("Invalid booking input(s)");
        }
    }


    @Override
    public void consume(String... data) throws IOException {

    }

    public static class BookingInputException extends Exception{
        private String errorMsg;
        BookingInputException(String errorMsg){
            super(errorMsg);
        }
        BookingInputException(){super();}
    }
}
