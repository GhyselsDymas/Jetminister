package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

public class LiveStream {
    @SerializedName("id")
    private String streamId;
    @SerializedName("name")
    private String streamUsername;
    @SerializedName("broadcast_location")
    private String streamLocation;
    @SerializedName("username")
    private String authUsername;
    @SerializedName("password")
    private String authPassword;
    @SerializedName("disable_authentication")
    private boolean authenticationDisabled;

    @SerializedName("source_connection_information")
    private SourceConnectionInformation sourceConnectionInformation;
    @SerializedName("encoder")
    private String encoderType;
    @SerializedName("transcoder_type")
    private String transcoderType;
    @SerializedName("delivery_protocol")
    private String publishProtocol;
    @SerializedName("target_delivery_protocol")
    private String playbackProtocol;
    @SerializedName("low_latency")
    private boolean lowLatency;
    @SerializedName("aspect_ratio_height")
    private int aspectRatioHeight;
    @SerializedName("aspect_ratio_width")
    private int aspectRatioWidth;

    @SerializedName("hosted_page")
    private boolean hostedPage;
    @SerializedName("hosted_page_sharing_icons")
    private boolean hostedPageSharingIcons;

    @SerializedName("billing_mode")
    private String billingMode;

    public LiveStream(String streamUsername, String streamLocation, String authUsername, String authPassword) {
        this.streamUsername = streamUsername;
        this.streamLocation = streamLocation;
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.authenticationDisabled = false;
        this.encoderType = "other_rtmp";
        this.transcoderType = "transcoded";
        this.publishProtocol = "rtmp";
        this.playbackProtocol = "hls-https";
        this.lowLatency = true;
        this.aspectRatioHeight = 1080;
        this.aspectRatioWidth = 1920;
        this.hostedPage = false;
        this.hostedPageSharingIcons = false;
        this.billingMode = "pay_as_you_go";
    }

    public String getStreamUsername() {
        return streamUsername;
    }

    public String getStreamLocation() {
        return streamLocation;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public boolean isAuthenticationDisabled() {
        return authenticationDisabled;
    }

    public String getEncoderType() {
        return encoderType;
    }

    public String getTranscoderType() {
        return transcoderType;
    }

    public String getPublishProtocol() {
        return publishProtocol;
    }

    public String getPlaybackProtocol() {
        return playbackProtocol;
    }

    public boolean isLowLatency() {
        return lowLatency;
    }

    public int getAspectRatioHeight() {
        return aspectRatioHeight;
    }

    public int getAspectRatioWidth() {
        return aspectRatioWidth;
    }

    public boolean isHostedPage() {
        return hostedPage;
    }

    public boolean isHostedPageSharingIcons() {
        return hostedPageSharingIcons;
    }

    public String getBillingMode() {
        return billingMode;
    }
}
