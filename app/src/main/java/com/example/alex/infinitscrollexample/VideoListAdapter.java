package com.example.alex.infinitscrollexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

public abstract class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.CustomViewHolder> {
    public final static String TAG = "Debug";
    private List<VideoItem> feedItemList;
    private Context mContext;
    private boolean finished = false;
    private String nextPageToken = null;
    private int totalResults = 0;

    public VideoListAdapter(Context context, List<VideoItem> feedItemList) {
        Log.d(TAG, "adapter constructor");
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "create VH" + i);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lisr_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int pos) {
        Log.d(TAG, "bind VH" + pos);

        final int position = customViewHolder.getAdapterPosition();

        if ((position == getItemCount() - 5) && (nextPageToken != null))
            load();

        VideoItem feedItem = feedItemList.get(position);

        Glide.with(mContext)
                .load(feedItem.getThumbnail())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .crossFade()
                .dontAnimate()
                .into(customViewHolder.thumdnail);

        //Setting text view title
        customViewHolder.description.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.duration.setText(feedItem.getDuration());
        customViewHolder.published.setText(new TimeAgo().toDuration(System.currentTimeMillis() - feedItem.getPublishedAt().getTime()));


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        mContext, PlayerViewActivity.class);

                intent.putExtra(PlayerViewActivity.EXTRA_VIDEO_ITEM, feedItemList.get(position));

                mContext.startActivity(intent);
            }
        };
        customViewHolder.description.setOnClickListener(clickListener);
    }

    public abstract void load();

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setFeedItemList(List<VideoItem> feedItemList) {
        this.feedItemList = feedItemList;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView thumdnail;
        protected TextView description;
        protected TextView duration;
        protected TextView published;


        public CustomViewHolder(View view) {
            super(view);
            this.thumdnail = (ImageView) view.findViewById(R.id.thumbnail);
            this.description = (TextView) view.findViewById(R.id.title);
            this.duration = (TextView) view.findViewById(R.id.durationText);
            this.published = (TextView) view.findViewById(R.id.published);
        }
    }

}