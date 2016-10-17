package com.example.alex.infinitscrollexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.services.youtube.YouTube;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "Debug";
    private static final int REQUEST_CHANNEL_ID = 1;
    public static final String PREFS_NAME = "PrefsFile";
    public static final String KEY_CHANNEL_ID_CURRENT = "ChannelIdCurrent";

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private ImageView mChannelAvatar;
    private ImageButton mSearchButton;
    private TextView mChannelTitle;
    private ImageView mChannelBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"****onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        View header = nvDrawer.getHeaderView(0);
        mChannelAvatar = (ImageView) header.findViewById(R.id.channel_avatar);
        mSearchButton = (ImageButton) header.findViewById(R.id.button_search);
        mChannelTitle = (TextView) header.findViewById(R.id.channel_title);
        mChannelBanner = (ImageView) header.findViewById(R.id.channel_banner);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), SearchChannelActivity.class);
                startActivityForResult(intent, REQUEST_CHANNEL_ID);

            }
        });
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = fragmentManager.findFragmentById(R.id.flContent);
//        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragment = new VideoListFragment();
                break;
            case R.id.nav_second_fragment:
                fragment = new VideoListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(VideoListFragment.KEY_FAVORITE, true);
                fragment.setArguments(bundle);
                break;
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(TAG,"****onPostCreate");
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"****onActivityResult");
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQUEST_CHANNEL_ID) {
            String channelId = data.getStringExtra(SearchChannelActivity.EXTRA_CHANNEL_NAME);
            if (channelId.equals(YoutubeService.ERROR_SEARCH_CHANNEL))
                Toast.makeText(getApplicationContext(), R.string.massege_no_such_channel, Toast.LENGTH_SHORT).show();
            else {
                getSharedPreferences(PREFS_NAME, 0).edit().putString(KEY_CHANNEL_ID_CURRENT, channelId).commit();
                mDrawer.closeDrawer(Gravity.LEFT);
            }
        }
    }

    private void replaceFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment  = new VideoListFragment();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        setTitle(nvDrawer.getMenu().getItem(0).getTitle());

        Database db = new Database(getApplicationContext());
        Log.d("DB","********************************************");
       List<Channel> list = db.getAllChannels();
        for(Channel channel : list){
            Log.d("DB",channel.get_title());
        }

        Channel channel = db.getChannel(getSharedPreferences(PREFS_NAME, 0).getString(KEY_CHANNEL_ID_CURRENT,getResources().getString(R.string.default_channel)));


        mChannelTitle.setText(channel.get_title());
        Glide.with(getApplicationContext())
                .load(channel.get_thumbnailurl())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .crossFade()
                .dontAnimate()
                .into(mChannelAvatar);

        Glide.with(getApplicationContext())
                .load(channel.get_bannerurl())
                .centerCrop()
                .crossFade()
                .dontAnimate()
                .into(mChannelBanner);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        replaceFragment();
        Log.d(TAG,"****onPostResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"****onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"****onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"****onSaveInstanceState_1_Bundle");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"****onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"****onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"****onStart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG,"****onSaveInstanceState_2_Bundle");
    }
}