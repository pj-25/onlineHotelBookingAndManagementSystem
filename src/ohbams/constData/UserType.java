package ohbams.constData;

import java.util.HashMap;

public enum UserType {
    REGULAR_CUSTOMER("RC"),
    CORPORATE_CLIENT("CC"),
    RECEPTIONIST("RE"),
    RESTAURANT_STAFF("ST"),
    BAR_STAFF("BS")
    ;

    private final String USER_CODE;

    UserType(String userCode){
        this.USER_CODE = userCode;
    }

    public String getCode() {
        return USER_CODE;
    }

    private static final HashMap<String, UserType> userTypeMap = new HashMap<>();

    static{
        for(UserType userType: UserType.values()){
            userTypeMap.put(userType.getCode(), userType);
        }
    }

    public static UserType get(String userCode){
        return userTypeMap.get(userCode);
    }
}
