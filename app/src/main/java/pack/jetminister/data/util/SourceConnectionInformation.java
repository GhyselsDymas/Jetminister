package pack.jetminister.data.util;

import com.google.gson.annotations.SerializedName;


//de-serialized POJO from Wowza RestAPI JSON object
public class SourceConnectionInformation {
    //constant accessed in code as key for passing/bundling String
    //or as child name in Firebase Realtime Database
    public static final String KEY_STREAM_PUBLISH_URL = "publishURL";
    //annotation for linking JSON and Java variable names to same field
    @SerializedName("primary_server")
    private String primaryServerAddress;
    @SerializedName("host_port")
    private int hostPort;
    @SerializedName("stream_name")
    private String streamName;
    @SerializedName("disable_authentication")
    private boolean disableAuthentication;
    @SerializedName("username")
    private String authUsername;
    @SerializedName("password")
    private String authPassword;


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

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(60);
        builder.append(primaryServerAddress.substring(0, 7))
                .append(authUsername)
                .append(":")
                .append(authPassword)
                .append("@")
                .append(primaryServerAddress.substring(7).split("/")[0])
                .append(":")
                .append(hostPort)
                .append("/")
                .append(primaryServerAddress.substring(7).split("/")[1])
                .append("/")
                .append(streamName);
        return builder.toString();
    }
}
