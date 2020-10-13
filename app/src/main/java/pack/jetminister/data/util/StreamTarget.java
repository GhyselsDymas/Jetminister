package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;

//de-serialized POJO from Wowza RestAPI JSON object
public class StreamTarget {
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("id")
    private String streamTargetId;

    public String getStreamTargetId() {
        return streamTargetId;
    }
}
