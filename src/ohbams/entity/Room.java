package ohbams.entity;

import ohbams.constData.RoomType;
import ohbams.messageHandler.MessageFormatHandler;

public class Room extends EntityParser implements Entity{

    private int roomId;
    private RoomType roomType;
    private int bookingId;

    public Room(int roomId, RoomType roomType, int bookingId) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public String toString() {
        return MessageFormatHandler.encode(super.toString(), roomId+"", roomType.getCode()+"", bookingId+"");
    }

    public static Room parse(String ...entityData) throws EntityFormatException{
        return new Room(Integer.parseInt(entityData[0]), RoomType.get(Integer.parseInt(entityData[1])), Integer.parseInt(entityData[2]));
    }
}
