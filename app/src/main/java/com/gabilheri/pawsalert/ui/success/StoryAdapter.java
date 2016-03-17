package com.gabilheri.pawsalert.ui.success;

import android.view.View;

import com.gabilheri.pawsalert.R;
import com.gabilheri.pawsalert.base.BaseRecyclerAdapter;
import com.gabilheri.pawsalert.base.ItemCallback;
import com.gabilheri.pawsalert.data.models.SuccessStory;
import com.gabilheri.pawsalert.data.models.TransitionWrapperModel;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 3/13/16.
 */
public class StoryAdapter extends BaseRecyclerAdapter<SuccessStory, StoryViewHolder> {

    ItemCallback<TransitionWrapperModel<SuccessStory>> mItemCallback;

    public StoryAdapter(ItemCallback<TransitionWrapperModel<SuccessStory>> itemCallback) {
        mItemCallback = itemCallback;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.list_item_story;
    }

    @Override
    public StoryViewHolder inflateViewHolder(View v) {
        return new StoryViewHolder(v, mItemCallback);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position) {
        holder.bind(mElements.get(position));
    }
}
