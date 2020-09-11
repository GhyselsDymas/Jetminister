package pack.jetminister.ui.util.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import pack.jetminister.R;

public class WebsitePreference extends Preference {
    private static final String URI_JETMINISTER = "https://jetminister.com/";

    public WebsitePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        //set the right XML file
        setWidgetLayoutResource(R.layout.preference_website);
    }

    View.OnClickListener websiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadWebPage();
        }
    };

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);
        View websiteIV = holder.findViewById(R.id.textview_settings_website);
        websiteIV.setClickable(true);
        websiteIV.setOnClickListener(websiteListener);
    }

    private void loadWebPage() {
        Context context = getContext();
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(Uri.parse(URI_JETMINISTER));
        context.startActivity(websiteIntent);
    }
}
