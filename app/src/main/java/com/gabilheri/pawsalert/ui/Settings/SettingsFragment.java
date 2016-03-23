package com.gabilheri.pawsalert.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseSettingsFragment;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.ui.profile.UserProfileActivity;
import com.gabilheri.pawsalert.ui.sign_in.SignInActivity;
import com.parse.ParseUser;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class SettingsFragment extends BaseSettingsFragment implements Preference.OnPreferenceClickListener {

    User mUser;
    Preference mUserPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        mUserPref = findPreference("myAccount");

        mUser = (User) ParseUser.getCurrentUser();
        mUserPref.setOnPreferenceClickListener(this);

        if (mUser == null) {
            mUserPref.setTitle("Sign In");
        }

        Preference distancePref = findPreference("notification_range");
        bindPreferenceSummaryToValue(distancePref);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (preference.equals(mUserPref)) {
            if (mUser == null) {
                startActivity(new Intent(getActivity(), SignInActivity.class));
            } else {
                startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        }
        return true;
    }

//    @Override
//    public void onCreatePreferences(Bundle bundle, String s) {
//
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}