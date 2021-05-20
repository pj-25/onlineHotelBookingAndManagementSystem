package ohbams.messageHandler;

import ohbams.entity.*;

public class MessageParser {
    public static EntityParser parseEntity(Class<? extends EntityParser> entityClass, String entityMsg) throws EntityFormatException {
        String[] entityData = MessageFormatHandler.decode(entityMsg);
        if(BillingInfo.class.equals(entityClass)){
            return BillingInfo.parse(entityData);
        }
        else if(BookingInfo.class.equals(entityClass)){
            return BookingInfo.parse(entityData);
        }
        else if(CorporateClient.class.equals(entityClass)){
            return CorporateClient.parse(entityData);
        }
        else if(Receptionist.class.equals(entityClass)){
            return Receptionist.parse(entityData);
        }
        else if(RegularCustomer.class.equals(entityClass)){
            return RegularCustomer.parse(entityData);
        }
        else if(Room.class.equals(entityClass)){
            return Room.parse(entityData);
        }
        else if(RoomAllocationStatus.class.equals(entityClass)){
            return RoomAllocationStatus.parse(entityData);
        }
        else if(ServiceCharge.class.equals(entityClass)){
            return ServiceCharge.parse(entityData);
        }
        else if(User.class.equals(entityClass)){
            return User.parse(entityData);
        }

        //return (Class.forName(entityClass.getName()).newInstance()).parse(entityData); //not working
        return null;
    }
}
