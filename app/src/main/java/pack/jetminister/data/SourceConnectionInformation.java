package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class SourceConnectionInformation {
    @SerializedName("primary_server")
    private String primaryServerAddress;
    @SerializedName("host_port")
    private int hostPort;
    @SerializedName("stream_name")
    private String streamName;
    @SerializedName("disable_authentication")
    private boolean disableAuthentication;
    @SerializedName("username")
    private String streamUsername;
    @SerializedName("password")
    private String streamPassword;


    public String getPrimaryServerAddress() {
        return primaryServerAddress;
    }

    public int getHostPort() {
        return hostPort;
    }

    public String getStreamName() {
        return streamName;
    }

    public boolean isDisableAuthentication() {
        return disableAuthentication;
    }

    public String getStreamUsername() {
        return streamUsername;
    }

    public String getStreamPassword() {
        return streamPassword;
    }
}
