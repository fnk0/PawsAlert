package com.gabilheri.pawsalert.ui.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;

import butterknife.OnClick;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.about));
        enableBackNav();
    }

    @OnClick(R.id.actionEmail)
    public void sendEmail(View v) {
        sendEmail("marcus@gabilheri.com");
    }

    @OnClick(R.id.actionGithub)
    public void openGithub(View v) {
        openURL("https://github.com/fnk0");
    }

    @OnClick(R.id.actionLinkedIn)
    public void openLinkedIn(View v) {
        openURL("https://www.linkedin.com/in/marcusgabilheri");
    }

    @OnClick(R.id.actionGooglePlus)
    public void openGooglePlus(View v) {
        openURL("https://plus.google.com/+MarcusViniciusAndreogabilheri");
    }

    @OnClick(R.id.actionTwitter)
    public void openTwitter(View v) {
        openURL("https://twitter.com/Marcus_fNk");
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_about;
    }
}
