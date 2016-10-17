package com.example.alex.infinitscrollexample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
public class VideoListActivity extends FragmentActivity {
    private static final String TAG = "Debug";
    private static final String KEY_ADAPTER = "adapter";
    private static final String BANDLE_ARRAY = "adapter";

    private List<VideoItem> feedsList;
    private RecyclerView mRecyclerView;
    private  VideoListAdapter adapter;
    private ProgressBar progressBar;

    private boolean firstPage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"Video list activity OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter = new VideoListAdapter(VideoListActivity.this, null) {
            @Override
            public void load() {
                Log.d(TAG, "load paginate");
                new AsyncHttpTask().execute();
            }
        };
        mRecyclerView.setAdapter(adapter);

        new AsyncHttpTask().execute();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public class AsyncHttpTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"AsyncTask preExecute");
            progressBar.setVisibility(View.VISIBLE);
            if (firstPage)
                setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.d(TAG, "AsyncTask do in background");
            Integer result = 0;
            try {

                YoutubeService.search("UCNzJmjkZRlUj15RpERotAEg");
                int statusCode = YoutubeService.getLastStatusCode();
                if (statusCode == 200) {
                    if (firstPage) {
                        feedsList = YoutubeService.getItems();
                        Log.d(TAG, "GET" + feedsList.toString());
                    }
                    else {
                        feedsList.addAll(YoutubeService.getItems());
                        Log.d(TAG, "ADD");
                    }
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "AsyncTask post execute");
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);


            if (result == 1) {
                if (firstPage) {
                    firstPage = false;
                    adapter.setFeedItemList(feedsList);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "notifyDataSetChanged1");
                } else {
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "notifyDataSetChanged2");
                }

                adapter.setNextPageToken(YoutubeService.getNextPageToken());
                Log.d(TAG, "next page token    " + YoutubeService.getNextPageToken());

            } else {
                Toast.makeText(VideoListActivity.this, R.string.massege_internet_not_connected, Toast.LENGTH_SHORT).show();
            }

        }
    }




//    private List<VideoItem> cheat() {
//        List<VideoItem> videoFileList = new ArrayList<>();
//        VideoItem videoItem = new VideoItem();
//        videoItem.setId("VCelOWeqrc8");
//        videoItem.setDescription("describtion1");
//        videoItem.setThumbnail("https://i.ytimg.com/vi/VCelOWeqrc8/mqdefault.jpg");
//        videoItem.setTitle("TVA SPORTS   GAME CONNECT 2587");
//        videoFileList.add(videoItem);
//
//        VideoItem videoItem2 = new VideoItem();
//        videoItem2.setId("dBBqcPOKjlg");
//        videoItem2.setDescription("describtion2");
//        videoItem2.setThumbnail("https://i.ytimg.com/vi/dBBqcPOKjlg/mqdefault.jpg");
//        videoItem2.setTitle("Vincennes Hippodrome Enjoy the thrill of being connected");
//        videoFileList.add(videoItem2);
//
//        VideoItem videoItem3 = new VideoItem();
//        videoItem3.setId("-7q-01whL0g");
//        videoItem3.setDescription("describtion3");
//        videoItem3.setThumbnail("https://i.ytimg.com/vi/-7q-01whL0g/mqdefault.jpg");
//        videoItem3.setTitle("VSEA Games Singapore App");
//        videoFileList.add(videoItem3);
//
//        VideoItem videoItem4 = new VideoItem();
//        videoItem4.setId("YA1AF1LQ1UQ");
//        videoItem4.setDescription("describtion4");
//        videoItem4.setThumbnail("https://i.ytimg.com/vi/YA1AF1LQ1UQ/mqdefault.jpg");
//        videoItem4.setTitle("NETCO win NUMIX PRICE 2015 @MONTREAL");
//        videoFileList.add(videoItem4);
//
//        VideoItem videoItem5 = new VideoItem();
//        videoItem5.setId("DhGbo2-OCQU");
//        videoItem5.setDescription("describtion5");
//        videoItem5.setThumbnail("https://i.ytimg.com/vi/DhGbo2-OCQU/mqdefault.jpg");
//        videoItem5.setTitle("TVA SPORTS HOCKEY SECOND SCREEN");
//        videoFileList.add(videoItem5);
//
//        return videoFileList;
//    }
}