package ohbams.entity;

import ohbams.constData.UserType;
import ohbams.messageHandler.MessageFormatHandler;

import java.time.LocalDate;

public class RegularCustomer extends User implements Entity{
    private String creditCardNumber;
    private LocalDate expiryDate;
    private String cardType;

    public RegularCustomer(String userId, String firstName, String lastName, int age, char gender, UserType userType, String emailId, String phoneNumber, String creditCardNumber, LocalDate expiryDate, String cardType) {
        super(userId, firstName, lastName, age, gender, userType, emailId, phoneNumber);
        this.creditCardNumber = creditCardNumber;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(super.toString(), creditCardNumber, expiryDate.toString(), cardType);
    }

    public static RegularCustomer parse(String ...entityData) throws EntityFormatException{
        try{
            return new RegularCustomer(entityData[0], entityData[1], entityData[2], Integer.parseInt(entityData[3]), entityData[4].charAt(0), UserType.get(entityData[5]), entityData[6], entityData[7], entityData[8], LocalDate.parse(entityData[9]), entityData[10]);
        }
        catch (Exception e){
            throw new EntityFormatException();
        }
    }
}
