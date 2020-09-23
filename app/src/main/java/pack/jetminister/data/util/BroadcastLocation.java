package pack.jetminister.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum BroadcastLocation {
    ASIA_PACIFIC_AUSTRALIA("Asia Pacific Australia"),
    ASIA_PACIFIC_INDIA("Asia Pacific India"),
    ASIA_PACIFIC_JAPAN("Asia Pacific Japan"),
    ASIA_PACIFIC_SINGAPORE("Asia Pacific Singapore"),
    ASIA_PACIFIC_S_KOREA("Asia Pacific South Korea"),
    ASIA_PACIFIC_TAIWAN("Asia Pacific Taiwan"),
    EU_BELGIUM("Europe Belguim"),
    EU_GERMANY("Europe Germany"),
    EU_IRELAND("Europe Ireland"),
    SOUTH_AMERICA_BRAZIL("South America Brazil"),
    US_CENTRAL_IOWA("US Iowa"),
    US_EAST_S_CAROLINA("US Carolina"),
    US_EAST_VIRGINIA("US Virginia"),
    US_WEST_CALIFORNIA("US California"),
    US_WEST_OREGON("US Oregon");


    private static final List<String> VALUES;

    private final String value;

    static {
        VALUES = new ArrayList<>();
        for (BroadcastLocation locationEnum : BroadcastLocation.values()) {
            VALUES.add(locationEnum.value);
        }
    }

    private BroadcastLocation(String value) {
        this.value = value;
    }

    public static List<String> getValues() {
        return Collections.unmodifiableList(VALUES);
    }

}
