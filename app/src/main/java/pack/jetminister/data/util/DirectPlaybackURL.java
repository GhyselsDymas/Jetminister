package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//de-serialized POJO from Wowza RestAPI JSON object
public class DirectPlaybackURL {
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("rtmp")
    private ArrayList<RTMP> rtmp;
    @SerializedName("rtsp")
    private ArrayList<RTSP> rtsp;
    @SerializedName("wowz")
    private ArrayList<Wowza> wowza;

    public ArrayList<RTMP> getRtmp() {
        return rtmp;
    }

    public ArrayList<RTSP> getRtsp() {
        return rtsp;
    }

    public ArrayList<Wowza> getWowza() {
        return wowza;
    }
}
