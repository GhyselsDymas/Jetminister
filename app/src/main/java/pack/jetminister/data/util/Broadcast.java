package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

import pack.jetminister.data.LiveStream;

public class Broadcast {
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
