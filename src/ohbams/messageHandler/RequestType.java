package ohbams.messageHandler;

import java.util.HashMap;

public enum RequestType implements MessageCode {


    ;
    private final int REQUEST_CODE;

    RequestType(int responseCode){
        this.REQUEST_CODE = responseCode;
    }

    public int getCode(){
        return REQUEST_CODE;
    }

    private static final HashMap<Integer, MessageType> requestTypeMap = new HashMap<>();

    static{
        for(MessageType messageType : MessageType.values()){
            requestTypeMap.put(messageType.getCode(), messageType);
        }
    }

    public static MessageType get(int msgCode) {
        return requestTypeMap.get(msgCode);
    }
}
