package pack.jetminister.ui.util.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import pack.jetminister.R;

public class TermsConditionsPreference extends Preference {

    private static final String URI_JETMINISTER = "https://jetminister.com/";

    public TermsConditionsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        //set the right XML file
        setWidgetLayoutResource(R.layout.preference_terms_conditions);
    }

    private View.OnClickListener termsConditionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loadWebPage();
        }
    };

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setClickable(false);
        View termsConditionsIV = holder.findViewById(R.id.iv_settings_termsconditions);
        termsConditionsIV.setClickable(true);
        termsConditionsIV.setOnClickListener(termsConditionsListener);
    }

    private void loadWebPage() {
        Context context = getContext();
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
        websiteIntent.setData(Uri.parse(URI_JETMINISTER));
        context.startActivity(websiteIntent);
    }
}
