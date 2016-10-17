/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.alex.infinitscrollexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.List;


public class PlayerViewActivity extends YouTubeFailureRecoveryActivity {

    public final static String TAG = "Debug";
    public static String EXTRA_VIDEO_ITEM = "youtubecanal.video_item";

    private VideoItem mVideoItem;

    private TextView mTitleView;
    private TextView mPublishedAtView;
    private TextView mLikesView;
    private TextView mDislikesView;
    private TextView mViewCount;
    private TextView mDescriptionView;
    private ImageView mFavoriteImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerview);

        mVideoItem = getIntent().getParcelableExtra(EXTRA_VIDEO_ITEM);

        mTitleView = (TextView) findViewById(R.id.video_title);
        mPublishedAtView = (TextView) findViewById(R.id.video_published_at);
        mLikesView = (TextView) findViewById(R.id.video_likes);
        mDislikesView = (TextView) findViewById(R.id.video_dislikes);
        mDislikesView = (TextView) findViewById(R.id.video_dislikes);
        mFavoriteImage = (ImageView) findViewById(R.id.video_favorite);

        mTitleView.setText(mVideoItem.getTitle());
        mPublishedAtView.setText(getResources().getString(R.string.published_at) + " " + new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(mVideoItem.getPublishedAt()));
        mLikesView.setText(" " + mVideoItem.getLikeCount());
        mDislikesView.setText(" " + mVideoItem.getDislikeCount());

        Database db = new Database(this);


        List<String> videoIds = db.getAllVideosId();

        for (String cn : videoIds) {
            String log = "VideoId: " + cn;
            Log.d("DB: ", log);}
        Log.d("DB: ", String.valueOf(db.getIsVideoFavorite(mVideoItem.getId())));


        if (db.getIsVideoFavorite(mVideoItem.getId()))
            mFavoriteImage.setBackgroundResource(R.drawable.ic_favorite_on);
        else
            mFavoriteImage.setBackgroundResource(R.drawable.ic_favorite_off);

        mFavoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database db = new Database(getApplicationContext());
                Log.d("DB: ", String.valueOf(db.getIsVideoFavorite(mVideoItem.getId())));
                if (db.getIsVideoFavorite(mVideoItem.getId())) {
                    db.deleteVideo(mVideoItem.getId());
                    mFavoriteImage.setBackgroundResource(R.drawable.ic_favorite_off);
                } else {
                    db.addVideo(new VideoId(mVideoItem.getId()));
                    mFavoriteImage.setBackgroundResource(R.drawable.ic_favorite_on);
                }
            }
        });


        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(mVideoItem.getId());
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
