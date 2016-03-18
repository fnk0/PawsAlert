package com.gabilheri.pawsalert.ui.profile;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BasePagerActivity;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.ui.home.PetListFragment;
import com.parse.ParseUser;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/18/16.
 */
public class UserProfileActivity extends BasePagerActivity {

    @Bind(R.id.profileBackdrop)
    AppCompatImageView mProfileBackdrop;

    @Bind(R.id.profilePicture)
    CircleImageView mProfileImage;

    User activeUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        activeUser = (User) ParseUser.getCurrentUser();
        addFragment("Profile", UserDetailsFragment.newInstance());
        addFragment("Pets", PetListFragment.newInstance(PetListFragment.USER_ID, activeUser.getObjectId()));

        setTitle(activeUser.getFullName());
        initPager();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_profile;
    }
}
