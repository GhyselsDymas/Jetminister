package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class SourceConnectionInformation {
    @SerializedName("primary_server")
    private String primaryServerAddress;
    @SerializedName("host_port")
    private int hostPort;
    @SerializedName("stream_name")
    private String streamName;

}
