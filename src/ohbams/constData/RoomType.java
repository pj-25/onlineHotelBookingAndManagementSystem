package ohbams.constData;

import java.util.HashMap;

public enum RoomType{

    SINGLE(1),
    TWIN(2),
    DOUBLE(3)
    ;

    private final int ROOM_CODE;

    RoomType(int roomCode){
        this.ROOM_CODE = roomCode;
    }

    public int getCode(){
        return this.ROOM_CODE;
    }

    private static final HashMap<Integer, RoomType> roomTypeMap = new HashMap<>();

    static{
        for(RoomType roomType: RoomType.values()){
            roomTypeMap.put(roomType.getCode(), roomType);
        }
    }

    public static RoomType get(int roomCode){
        return roomTypeMap.get(roomCode);
    }
}
