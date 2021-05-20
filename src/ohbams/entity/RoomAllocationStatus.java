package ohbams.entity;

import ohbams.constData.RoomType;
import ohbams.messageHandler.MessageFormatHandler;

public class RoomAllocationStatus extends EntityParser implements Entity{

    private int roomTypeId;
    private RoomType roomType;
    private int bookedCount;
    private int totalRooms;

    public RoomAllocationStatus(int roomTypeId, RoomType roomType, int bookedCount, int totalRooms) {
        this.roomTypeId = roomTypeId;
        this.roomType = roomType;
        this.bookedCount = bookedCount;
        this.totalRooms = totalRooms;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(roomTypeId+"", roomType.getCode()+"", bookedCount+"", totalRooms+"");
    }

    public static RoomAllocationStatus parse(String...entityData) throws EntityFormatException{
        try{
            return new RoomAllocationStatus(Integer.parseInt(entityData[0]), RoomType.get(Integer.parseInt(entityData[1])), Integer.parseInt(entityData[2]),Integer.parseInt(entityData[3]));
        }
        catch (Exception e){
            throw new EntityFormatException();
        }
    }
}
