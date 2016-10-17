package com.example.alex.infinitscrollexample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class SearchChannelActivity extends AppCompatActivity {

    public static final String EXTRA_CHANNEL_NAME = "extra_channel_name";

    private EditText mChannelName;
    private ImageButton mSearchButton;
    private ImageButton mBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.search_channel);
        mChannelName = (EditText) findViewById(R.id.searh_channel_text);
        mSearchButton = (ImageButton) findViewById(R.id.search_channel_button);
        mBackButton = (ImageButton) findViewById(R.id.button_back);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncHttpTask().execute(mChannelName.getText().toString());
            }
        });
    }

    private class AsyncHttpTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String result;
            try {
                result = YoutubeService.getChannelIdByName(getApplicationContext(), name);
            } catch (IOException e) {
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (YoutubeService.getLastStatusCode() != 200)
                Toast.makeText(getApplicationContext(), R.string.massege_internet_not_connected, Toast.LENGTH_SHORT).show();
            else {
                Intent i = new Intent();
                i.putExtra(EXTRA_CHANNEL_NAME, result);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        }
    }
}