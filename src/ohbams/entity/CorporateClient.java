package ohbams.entity;

import ohbams.constData.UserType;
import ohbams.messageHandler.MessageFormatHandler;

public class CorporateClient extends User implements Entity{

    private String corporateName;
    private float totalAmount;
    private String role;

    public CorporateClient(String userId, String firstName, String lastName, int age, char gender, UserType userType, String emailId, String phoneNumber, String corporateName, float totalAmount, String role) {
        super(userId, firstName, lastName, age, gender, userType, emailId, phoneNumber);
        this.corporateName = corporateName;
        this.totalAmount = totalAmount;
        this.role = role;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



    @Override
    public String toString() {
        return MessageFormatHandler.encode(super.toString(), corporateName, totalAmount+"", role);
    }

    public static CorporateClient parse(String ...entityData){
        return new CorporateClient(entityData[0], entityData[1], entityData[2], Integer.parseInt(entityData[3]), entityData[4].charAt(0), UserType.get(entityData[5]), entityData[6], entityData[7], entityData[8], Float.parseFloat(entityData[9]), entityData[10]);
    }
}
