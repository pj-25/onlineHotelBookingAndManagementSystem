package ohbams.messageHandler;

import java.util.HashMap;

public enum UpdateEventType implements MessageCode {


    ;

    private final int UPDATE_EVENT_CODE;

    UpdateEventType(int updateEventCode){
        this.UPDATE_EVENT_CODE = updateEventCode;
    }

    public int getUPDATE_EVENT_CODE() {
        return UPDATE_EVENT_CODE;
    }

    private static final HashMap<Integer, UpdateEventType> updateEventTypeMap = new HashMap<>();

    public int getCode(){
        return UPDATE_EVENT_CODE;
    }
}
