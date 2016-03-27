package com.gabilheri.pawsalert.ui.success;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseActivity;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.data.models.User;
import com.gabilheri.pawsalert.helpers.Const;
import com.gabilheri.pawsalert.helpers.FileUriUtils;
import com.gabilheri.pawsalert.helpers.PictureUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import info.hoang8f.android.segmented.SegmentedGroup;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ActivityAddSuccessStory extends BaseActivity {

    int PHOTO_PICKER_REQUEST = 1499;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.storyImage)
    AppCompatImageView mStoryCoverIV;

    @Bind(R.id.storyTitle)
    AppCompatEditText mStoryTitleET;

    @Bind(R.id.storyPet)
    AppCompatEditText mStoryPetET;

    @Bind(R.id.segmentFoundAdopted)
    SegmentedGroup mSegmentFoundAdopted;

    @Bind(R.id.storyDescription)
    AppCompatEditText mStoryET;

    SuccessStory mSuccessStory;
    User mCurrentUser;
    String mPhotoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackNav();
        setTitle("");
        mCollapsingToolbarLayout.setTitle("");
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    @OnTextChanged(R.id.storyTitle)
    public void onShelterNameChanged(CharSequence text) {
        setTitle(text);
        mCollapsingToolbarLayout.setTitle(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_PICKER_REQUEST) {
                Uri imageURI = data.getData();
                mPhotoPath = FileUriUtils.getPath(imageURI);
                Glide.with(this)
                        .load(mPhotoPath)
                        .into(mStoryCoverIV);
            }
        }
    }

    @OnClick(R.id.fabAddPicture)
    public void addPicture(View v) {
        Intent intent = new Intent();
        intent.setType(Const.MIME_IMAGE_ALL);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PHOTO_PICKER_REQUEST);
    }

    @OnClick(R.id.createStory)
    public void createStory(View v) {
        mSuccessStory = new SuccessStory();
        mSuccessStory.setTitle(mStoryTitleET.getText().toString());
        mSuccessStory.setPetName(mStoryPetET.getText().toString());
        mSuccessStory.setStory(mStoryET.getText().toString());
        mSuccessStory.setUser(mCurrentUser);
        mSuccessStory.setAdoptionStory(mSegmentFoundAdopted.getCheckedRadioButtonId() == R.id.adopted);

        showProgressDialog("Creating Success Story...", null);

        String strippedName = mSuccessStory.getTitle().replaceAll("\\W", "").toLowerCase();

        ParseFile file = PictureUtils.getParseFileFromPath(mPhotoPath, strippedName, mCurrentUser.getUsername());
        mSuccessStory.setPicture(file);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mSuccessStory.toParseObject();
                    mSuccessStory.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                dismissDialog();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                showSnackbar("Oops! Something went wrong...");
                            }
                        }
                    });
                } else {
                    Timber.e(e, "Error Saving image: " + e.getLocalizedMessage());
                    dismissDialog();
                    showSnackbar("Error saving image.");
                }
            }
        });


    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_story;
    }
}
