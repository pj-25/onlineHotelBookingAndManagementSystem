package ohbams.entity;

import ohbams.constData.UserType;
import ohbams.messageHandler.MessageFormatHandler;

import java.io.Serializable;

public class User extends EntityParser implements Entity, Serializable {
    private String userId;
    private String firstName;
    private String lastName;
    private int age;
    private char gender;
    private UserType userType;
    private String emailId;
    private String phoneNumber;

    public User(){

    }
    public User(String userId, String firstName, String lastName, int age, char gender, UserType userType, String emailId, String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.userType = userType;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(userId, firstName, lastName, age+"", gender+"", userType.getCode()+"", emailId, phoneNumber);
    }


    public static User parse(String ...entityData) throws EntityFormatException{
        try{
            return new User(entityData[0], entityData[1], entityData[2], Integer.parseInt(entityData[3]), entityData[4].charAt(0), UserType.get(entityData[5]), entityData[6], entityData[7]);
        }
        catch (Exception e){
            System.out.println(e);
            throw new EntityFormatException();
        }
    }
}
