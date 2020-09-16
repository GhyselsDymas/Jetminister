package pack.jetminister.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChooserActivity extends AppCompatActivity {

    public static final String INTENT_KEY_URI = "uri";
    public static final String INTENT_KEY_TYPE = "type";
    public static final String RTMP_URL = "rtmp://184.72.239.149/vod/mp4:bigbuckbunny_1500.mp4";

    public static final String HLS_URL = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8";
    public static final String DASH_URL = "http://www.youtube.com/api/manifest/dash/id/3aa39fa2cc27967f/source/youtube?as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams="
            + "ip,ipbits,expire,source,id,as&ip=0.0.0.0&ipbits=0&expire=19000000000&signature=A2716F75795F5D2AF0E88962FFCD10DB79384F29.84308FF04844498" +
            "CE6FBCE4731507882B8307798&key=ik0";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
