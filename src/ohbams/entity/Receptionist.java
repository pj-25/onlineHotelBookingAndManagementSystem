package ohbams.entity;

import ohbams.constData.UserType;
import ohbams.messageHandler.MessageFormatHandler;

public class Receptionist extends User implements Entity{

    private String receptionCode;

    public Receptionist(String userId, String firstName, String lastName, int age, char gender, UserType userType, String emailId, String phoneNumber, String receptionCode) {
        super(userId, firstName, lastName, age, gender, userType, emailId, phoneNumber);
        this.receptionCode = receptionCode;
    }

    public String getReceptionCode() {
        return receptionCode;
    }

    public void setReceptionCode(String receptionCode) {
        this.receptionCode = receptionCode;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(super.toString(), receptionCode);
    }

    public static Receptionist parse(String ...entityData){
        return new Receptionist(entityData[0], entityData[1], entityData[2], Integer.parseInt(entityData[3]), entityData[4].charAt(0), UserType.get(entityData[5]), entityData[6], entityData[7], entityData[8]);
    }
}
