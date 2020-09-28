package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DirectPlaybackURL {
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
