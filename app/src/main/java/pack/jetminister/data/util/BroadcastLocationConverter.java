package pack.jetminister.data.util;

public class BroadcastLocationConverter {

    public static BroadcastLocation stringToBroadcastLocation(String locationAsString){
        BroadcastLocation locationAsEnum = BroadcastLocation.valueOf(locationAsString.toUpperCase());
        return locationAsEnum;
    }

    public static String broadcastLocationToString(BroadcastLocation locationAsEnum){
        String locationAsString = locationAsEnum.name().toLowerCase();
        return locationAsString;
    }
}
