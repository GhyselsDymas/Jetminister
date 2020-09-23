package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class RTMP {
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
