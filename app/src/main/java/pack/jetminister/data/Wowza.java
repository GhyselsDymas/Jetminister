package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class Wowza {

    @SerializedName("name")
    private String wowzaName;
    @SerializedName("output_id")
    private String wowzaOutputId;
    @SerializedName("url")
    private String wowzaURL;

    public String getWowzaName() {
        return wowzaName;
    }

    public String getWowzaOutputId() {
        return wowzaOutputId;
    }

    public String getWowzaURL() {
        return wowzaURL;
    }
}
