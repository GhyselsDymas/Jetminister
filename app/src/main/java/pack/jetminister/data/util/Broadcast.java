package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

import pack.jetminister.data.LiveStream;

//de-serialized POJO from Wowza RestAPI JSON object
public class Broadcast {
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("live_stream")
    private LiveStream liveStream;

    public Broadcast() {
    }

    public Broadcast(LiveStream liveStream) {
        this.liveStream = liveStream;
    }

    public LiveStream getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(LiveStream liveStream) {
        this.liveStream = liveStream;
    }

}
