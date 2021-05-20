package ohbams.entity;

import ohbams.messageHandler.MessageFormatHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ServiceCharge extends EntityParser implements Entity{

    private int serviceChargeId;
    private String chargedById;
    private String serviceInfo;
    private int bookingId;
    private float amount;
    private LocalDateTime chargedOn;

    public ServiceCharge(int serviceChargeId, String chargedById, String serviceInfo, int bookingId, float amount, LocalDateTime chargedOn) {
        this.serviceChargeId = serviceChargeId;
        this.chargedById = chargedById;
        this.serviceInfo = serviceInfo;
        this.bookingId = bookingId;
        this.amount = amount;
        this.chargedOn = chargedOn;
    }

    public int getServiceChargeId() {
        return serviceChargeId;
    }

    public void setServiceChargeId(int serviceChargeId) {
        this.serviceChargeId = serviceChargeId;
    }

    public String getChargedById() {
        return chargedById;
    }

    public void setChargedById(String chargedById) {
        this.chargedById = chargedById;
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
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

    public LocalDateTime getChargedOn() {
        return chargedOn;
    }

    public void setChargedOn(LocalDateTime chargedOn) {
        this.chargedOn = chargedOn;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(serviceChargeId+"", chargedById+"", serviceInfo, bookingId+"", amount+"", chargedOn.toString());
    }

    public static ServiceCharge parse(String... entityData) throws EntityFormatException {
        try{
            return new ServiceCharge(Integer.parseInt(entityData[0]), entityData[1] , entityData[2], Integer.parseInt(entityData[3]), Float.parseFloat(entityData[4]), LocalDateTime.parse(entityData[5]));
        }
        catch (Exception e){
            throw new EntityFormatException();
        }
    }

    public static ArrayList<ServiceCharge> parseList(String... entities) throws EntityFormatException{
        try{
            ArrayList<ServiceCharge> serviceChargeArrayList = new ArrayList<>();
            for(String serviceCharge:entities){
                serviceChargeArrayList.add(parse(MessageFormatHandler.decode(serviceCharge)));
            }
            return serviceChargeArrayList;
        }
        catch (Exception e){
            System.out.println(e);
            throw new EntityFormatException();
        }
    }
}
