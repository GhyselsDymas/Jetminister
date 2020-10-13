package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

//de-serialized POJO from Wowza RestAPI JSON object
public class RTMP {
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("name")
    private String rtmpName;
    @SerializedName("output_id")
    private String rtmpOutputId;
    @SerializedName("url")
    private String rtmpURL;

    public String getRtmpName() {
        return rtmpName;
    }

    public String getRtmpOutputId() {
        return rtmpOutputId;
    }

    public String getRtmpURL() {
        return rtmpURL;
    }
}
