package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class StreamTarget {

    @SerializedName("id")
    private String streamTargetId;

    public String getStreamTargetId() {
        return streamTargetId;
    }
}
