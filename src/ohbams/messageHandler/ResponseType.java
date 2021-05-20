package ohbams.messageHandler;

import java.util.HashMap;

public class ResponseType {


    ;
    private final int RESPONSE_CODE;

    ResponseType(int responseCode){
        this.RESPONSE_CODE = responseCode;
    }

    public int getCode(){
        return RESPONSE_CODE;
    }

    private static final HashMap<Integer, MessageType> responseTypeMap = new HashMap<>();

    static{
        for(MessageType messageType : MessageType.values()){
            responseTypeMap.put(messageType.getCode(), messageType);
        }
    }

    public static MessageType get(int msgCode) {
        return responseTypeMap.get(msgCode);
    }
}
