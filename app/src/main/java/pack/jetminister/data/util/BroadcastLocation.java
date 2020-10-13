package pack.jetminister.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// contains all valid server locations for Wowza RestAPI
public enum BroadcastLocation {
    ASIA_PACIFIC_AUSTRALIA("Asia Pacific Australia"),
    ASIA_PACIFIC_INDIA("Asia Pacific India"),
    ASIA_PACIFIC_JAPAN("Asia Pacific Japan"),
    ASIA_PACIFIC_SINGAPORE("Asia Pacific Singapore"),
    ASIA_PACIFIC_S_KOREA("Asia Pacific South Korea"),
    ASIA_PACIFIC_TAIWAN("Asia Pacific Taiwan"),
    EU_BELGIUM("Europe Belgium"),
    EU_GERMANY("Europe Germany"),
    EU_IRELAND("Europe Ireland"),
    SOUTH_AMERICA_BRAZIL("South America Brazil"),
    US_CENTRAL_IOWA("U.S.A. Iowa"),
    US_EAST_S_CAROLINA("U.S.A. Carolina"),
    US_EAST_VIRGINIA("U.S.A. Virginia"),
    US_WEST_CALIFORNIA("U.S.A. California"),
    US_WEST_OREGON("U.S.A. Oregon");

    private static final List<String> BROADCAST_LOCATION_LIST;

    private final String location;

    static {
        BROADCAST_LOCATION_LIST = new ArrayList<>();
        for (BroadcastLocation locationEnum : BroadcastLocation.values()) {
            BROADCAST_LOCATION_LIST.add(locationEnum.location);
        }
    }

    private BroadcastLocation(String location) {
        this.location = location;
    }

    public static List<String> getBroadcastLocationList() {
        return Collections.unmodifiableList(BROADCAST_LOCATION_LIST);
    }

}
