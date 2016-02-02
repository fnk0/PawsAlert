package com.gabilheri.pawsalert.ui.add;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 1/20/16.
 */
public class AddPetActivity extends BaseActivity {

    @Bind(R.id.edit_text)
    AppCompatEditText mPetNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableBackNav();

        mPetNameEditText.setHint(R.string.pet_name);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add;
    }
}
