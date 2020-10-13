package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

//de-serialized POJO from Wowza RestAPI JSON object
public class Wowza {
    //annotation for linking JSON and Java variable names to same field
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
