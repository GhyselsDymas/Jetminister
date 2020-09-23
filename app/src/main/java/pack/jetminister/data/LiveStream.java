package pack.jetminister.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LiveStream {
    @SerializedName("id")
    private String streamId;
    @SerializedName("created_at")
    private String timeCreated;
    @SerializedName("updated_at")
    private String timeUpdated;
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


}
