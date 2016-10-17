package com.example.alex.infinitscrollexample;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class VideoListFragment extends Fragment {
    private static final String TAG = "Debug";
    public static final String KEY_FAVORITE = "mIsFavorite";

    private List<VideoItem> feedsList;
    private RecyclerView mRecyclerView;
    private VideoListAdapter adapter;
    private ProgressBar progressBar;

    private boolean firstPage = true;
    private boolean mIsFavorite = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_feed_list, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null)
            mIsFavorite = bundle.getBoolean(KEY_FAVORITE, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        // Initialize recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        adapter = new VideoListAdapter(getActivity(), null) {
            @Override
            public void load() {
                Log.d(TAG, "load paginate");
                new AsyncHttpTask().execute();
            }
        };
        mRecyclerView.setAdapter(adapter);

        new AsyncHttpTask().execute();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class AsyncHttpTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AsyncTask preExecute");
            progressBar.setVisibility(View.VISIBLE);
            if (firstPage)
                getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.d(TAG, "AsyncTask do in background");
            Integer result = 0;
            try {

                if (mIsFavorite)
                    YoutubeService.searchFavorite(getActivity());
                else {
                    String channelId = getContext().getSharedPreferences(MainActivity.PREFS_NAME,0).getString(MainActivity.KEY_CHANNEL_ID_CURRENT,getResources().getString(R.string.default_channel));
                    YoutubeService.search(channelId);
                }
                int statusCode = YoutubeService.getLastStatusCode();
                if (statusCode == 200) {
                    if (firstPage) {
                        feedsList = YoutubeService.getItems();
                        Log.d(TAG, "GET" + feedsList.toString());
                    } else {
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
                Toast.makeText(getActivity(), R.string.massege_internet_not_connected, Toast.LENGTH_SHORT).show();
            }

        }
    }

}