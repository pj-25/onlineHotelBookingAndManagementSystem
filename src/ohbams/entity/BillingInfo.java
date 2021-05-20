package ohbams.entity;

import ohbams.messageHandler.MessageFormatHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BillingInfo extends EntityParser implements Entity{

    private int billId;
    private int bookingId;
    private float amount;
    private LocalDateTime payedOn;
    private char status;

    public BillingInfo(int billId, int bookingId, float amount, LocalDateTime payedOn, char status) {
        this.billId = billId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.payedOn = payedOn;
        this.status = status;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDateTime getPayedOn() {
        return payedOn;
    }

    public void setPayedOn(LocalDateTime payedOn) {
        this.payedOn = payedOn;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(billId+"", bookingId+"", amount+"", payedOn+"", status+"");
    }

    public static BillingInfo parse(String ...entityData){
        return new BillingInfo(Integer.parseInt(entityData[0]), Integer.parseInt(entityData[1]), Float.parseFloat(entityData[2]), LocalDateTime.parse(entityData[3]),entityData[4].charAt(0));
    }

    public static ArrayList<BillingInfo> parseList(String ...entities) throws EntityFormatException{
        try{
            ArrayList<BillingInfo> bookingList = new ArrayList<>();
            for(String billingInfo:entities){
                bookingList.add(parse(MessageFormatHandler.decode(billingInfo)));
            }
            return bookingList;
        }
        catch (Exception e){
            System.out.println(e);
            throw new EntityFormatException();
        }
    }
}
