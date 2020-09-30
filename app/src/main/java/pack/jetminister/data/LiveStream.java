package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import pack.jetminister.data.util.DirectPlaybackURL;
import pack.jetminister.data.util.SourceConnectionInformation;
import pack.jetminister.data.util.StreamTarget;

public class LiveStream {private static final String TAG = "LiveStream";

    public static final String KEY_LIVE_STREAMS = "liveStreams";
    public static final String KEY_LIVE_STREAM = "liveStream";
    public static final String KEY_STREAM_ID = "streamId";
    public static final String KEY_STREAM_USERNAME = "streamUsername";
    public static final String KEY_STREAM_PLAYBACK_URL = "playbackURL";
    public static final String KEY_STREAM_LIKES = "likes";
    public static final String KEY_STREAM_VIEWERS = "viewers";
    public static final String KEY_STREAM_THEME = "theme";


    @SerializedName("id")
    private String streamId;
    @SerializedName("state")
    private String streamState;
    @SerializedName("ip_address")
    private String streamIPAdress;
    @SerializedName("created_at")
    private String timeCreated;
    @SerializedName("updated_at")
    private String timeUpdated;
    @SerializedName("name")
    private String streamUsername;
    @SerializedName("broadcast_location")
    private String streamLocation;
    private String theme;
    @SerializedName("username")
    private String authUsername;
    @SerializedName("password")
    private String authPassword;
    @SerializedName("disable_authentication")
    private boolean authenticationDisabled;
    @SerializedName("use_stream_source")
    private boolean streamSource;
    @SerializedName("stream_targets")
    private ArrayList<StreamTarget> streamTargets;
    @SerializedName("direct_playback_urls")
    private DirectPlaybackURL directPlaybackURL;
    @SerializedName("source_connection_information")
    private SourceConnectionInformation sourceConnectionInformation;
    @SerializedName("encoder")
    private String encoderType;
    @SerializedName("transcoder_type")
    private String transcoderType;
    @SerializedName("delivery_method")
    private String deliveryMethod;
    @SerializedName("delivery_protocols")
    private ArrayList<String> publishProtocols;
    @SerializedName("target_delivery_protocol")
    private String playbackProtocol;
    @SerializedName("low_latency")
    private boolean lowLatency;
    @SerializedName("aspect_ratio_height")
    private int aspectRatioHeight;
    @SerializedName("aspect_ratio_width")
    private int aspectRatioWidth;
    @SerializedName("vod_stream")
    private boolean VODStream;
    private boolean recording;
    @SerializedName("closed_caption_type")
    private String closedCaption;
    @SerializedName("player_id")
    private String playerID;
    @SerializedName("player_type")
    private String playerType;
    @SerializedName("player_responsive")
    private boolean playerResponsive;
    @SerializedName("player_width")
    private int playerWidth;
    @SerializedName("player_countdown")
    private boolean playerCountdown;
    @SerializedName("player_embed_code")
    private String playerEmbedCode;
    @SerializedName("player_hls_playback_url")
    private String playbackURL;
    @SerializedName("hosted_page")
    private String pubishURL;
    private boolean hostedPage;
    @SerializedName("hosted_page_title")
    private String hostedPageTitle;
    @SerializedName("hosted_page_url")
    private String hostedPageURL;
    @SerializedName("hosted_page_sharing_icons")
    private boolean hostedPageSharingIcons;
    @SerializedName("hosted_page_logo_image_url")
    private boolean hostedPageLogoImageURL;
    @SerializedName("billing_mode")
    private String billingMode;
    private int likes;
    private int viewers;

    private LiveStream(){}

    public LiveStream(String streamUsername, String streamLocation, String theme, String authUsername, String authPassword) {
        this.streamUsername = streamUsername;
        this.streamLocation = streamLocation;
        this.theme = theme;
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.authenticationDisabled = false;
        this.encoderType = "other_rtmp";
        this.transcoderType = "transcoded";
        this.lowLatency = true;
        this.aspectRatioHeight = 1920;
        this.aspectRatioWidth = 1080;
        this.hostedPage = false;
        this.hostedPageSharingIcons = false;
        this.billingMode = "pay_as_you_go";
        this.viewers = 0;
        this.likes = 0;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public void setPlaybackURL(String playbackURL) {
        this.playbackURL = playbackURL;
    }

    public void setPubishURL(String pubishURL) {
        this.pubishURL = pubishURL;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getStreamState() {
        return streamState;
    }

    public String getStreamIPAdress() {
        return streamIPAdress;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public String getTimeUpdated() {
        return timeUpdated;
    }

    public String getStreamUsername() {
        return streamUsername;
    }

    public String getTheme() {
        return theme;
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

    public boolean isStreamSource() {
        return streamSource;
    }

    public ArrayList<StreamTarget> getStreamTargets() {
        return streamTargets;
    }

    public DirectPlaybackURL getDirectPlaybackURL() {
        return directPlaybackURL;
    }

    public SourceConnectionInformation getSourceConnectionInformation() {
        return sourceConnectionInformation;
    }

    public String getEncoderType() {
        return encoderType;
    }

    public String getTranscoderType() {
        return transcoderType;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public ArrayList<String> getPublishProtocols() {
        return publishProtocols;
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

    public boolean isVODStream() {
        return VODStream;
    }

    public boolean isRecording() {
        return recording;
    }

    public String getClosedCaption() {
        return closedCaption;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getPlayerType() {
        return playerType;
    }

    public boolean isPlayerResponsive() {
        return playerResponsive;
    }

    public int getPlayerWidth() {
        return playerWidth;
    }

    public boolean isPlayerCountdown() {
        return playerCountdown;
    }

    public String getPlayerEmbedCode() {
        return playerEmbedCode;
    }

    public String getPlaybackURL() {
        return playbackURL;
    }

    public String getPubishURL() {
        return pubishURL;
    }

    public boolean isHostedPage() {
        return hostedPage;
    }

    public String getHostedPageTitle() {
        return hostedPageTitle;
    }

    public String getHostedPageURL() {
        return hostedPageURL;
    }

    public boolean isHostedPageSharingIcons() {
        return hostedPageSharingIcons;
    }

    public boolean isHostedPageLogoImageURL() {
        return hostedPageLogoImageURL;
    }

    public String getBillingMode() {
        return billingMode;
    }

    public int getLikes() {
        return likes;
    }

    public int getViewers() {
        return viewers;
    }}
