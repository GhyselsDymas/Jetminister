package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

//de-serialized POJO from Wowza RestAPI JSON object
public class RTSP {
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("name")
    private String rtspName;
    @SerializedName("output_id")
    private String rtspOutputId;
    @SerializedName("url")
    private String rtspURL;

    public String getRtspName() {
        return rtspName;
    }

    public String getRtspOutputId() {
        return rtspOutputId;
    }

    public String getRtspURL() {
        return rtspURL;
    }
}
