package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class RTSP {
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
