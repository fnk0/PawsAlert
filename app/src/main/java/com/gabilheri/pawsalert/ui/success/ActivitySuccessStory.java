package com.gabilheri.pawsalert.ui.success;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseDrawerActivity;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;
import com.gabilheri.pawsalert.helpers.Const;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

import butterknife.Bind;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class ActivitySuccessStory extends BaseDrawerActivity
        implements ItemCallback<TransitionWrapperModel<SuccessStory>>, FindCallback<SuccessStory> {

    public static final int ADD_STORY = 9862;
    public static final int OPEN_STORY = 9621;
    public static final int LIKE_STORY = 3214;

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    StoryAdapter mStoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.success_stories));
        mNavigationView.setCheckedItem(R.id.stories);
        enableFab(true, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStoryAdapter = new StoryAdapter(this);
        mRecyclerView.setAdapter(mStoryAdapter);
        SuccessStory.getQuery().findInBackground(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                startActivityForResult(new Intent(this, ActivityAddSuccessStory.class), ADD_STORY);
                break;
        }
    }

    @Override
    public void onItemCallback(TransitionWrapperModel<SuccessStory> item) {
        switch (item.getState()) {
            case OPEN_STORY:
                Intent intent = new Intent(this, ActivitySuccessDetail.class);
                intent.putExtra(Const.OBJECT_ID, item.getModel().getObjectId());
                String picUrl = item.getModel().getPicture().getUrl();
                intent.putExtra(Const.IMAGE_EXTRA, picUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, item.getView(), Const.IMAGE_EXTRA);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

                break;
            case LIKE_STORY:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            SuccessStory.getQuery().findInBackground(this);
        }
    }

    @Override
    public void done(List<SuccessStory> objects, ParseException e) {
        mStoryAdapter.clear();
        for(SuccessStory ss : objects) {
            ss = ss.fromParseObject(ss);
            mStoryAdapter.add(ss);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_long_toolbar_rv;
    }
}
