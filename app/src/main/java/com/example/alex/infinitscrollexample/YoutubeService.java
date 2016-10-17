package com.example.alex.infinitscrollexample;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


public class YoutubeService {

    public final static String TAG = "Debug";
    public static String ERROR_SEARCH_CHANNEL = "errorSearchChannel";
    private static YouTube youtube;
    public static final String KEY = DeveloperKey.DEVELOPER_KEY;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static String nextPageToken = null;
    private static int totalResults = 0;
    private static List<VideoItem> items;
    private static ArrayList<String> videoIds;
    private static int lastStatusCode;


    public static void search(String channelId) throws IOException {

        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        }).setApplicationName("MyApp").build();

        YouTube.Search.List query = youtube.search().list("id,snippet");
        query.setKey(KEY);
        query.setType("video");
//        query.setChannelId("UCNzJmjkZRlUj15RpERotAEg");//netco sprt
        Log.d(TAG, "**************************" + channelId);
        query.setChannelId(channelId);
        query.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
        query.setPageToken(nextPageToken);
        query.setOrder("date");

        SearchListResponse response = query.execute();

        Log.d(TAG, query.getLastStatusMessage());
        Log.d(TAG, String.valueOf(query.getLastStatusCode()));

        lastStatusCode = query.getLastStatusCode();

        nextPageToken = response.getNextPageToken();
        totalResults = response.getPageInfo().getTotalResults();
        List<SearchResult> results = response.getItems();
        items = new ArrayList<VideoItem>();
        videoIds = new ArrayList<String>();

        for (SearchResult result : results) {
            VideoItem item = new VideoItem();
            item.setTitle(result.getSnippet().getTitle());
            item.setDescription(result.getSnippet().getDescription());
            item.setThumbnail(result.getSnippet().getThumbnails().getMedium().getUrl());
            item.setId(result.getId().getVideoId());
            item.setPublishedAt(new Date(Long.parseLong(String.valueOf(result.getSnippet().getPublishedAt().getValue()))));

            videoIds.add(item.getId());
            items.add(item);
        }

        YouTube.Videos.List queryDuration = youtube.videos().list("id,contentDetails");
        queryDuration.setKey(KEY);
        queryDuration.setPart("contentDetails,statistics");
        queryDuration.set("id", StringUtils.join(videoIds, ","));
        VideoListResponse responseDuration = queryDuration.execute();
        List<Video> resultsDuration = responseDuration.getItems();

        for (int i = 0; i < resultsDuration.size(); i++) {
            items.get(i).setDuration(YouTubeDurationUtils.convertYouTubeDuration(resultsDuration.get(i).getContentDetails().getDuration()));
            items.get(i).setViewCount((resultsDuration.get(i).getStatistics().getViewCount()).intValue());
            items.get(i).setLikeCount((resultsDuration.get(i).getStatistics().getLikeCount()).intValue());
            items.get(i).setDislikeCount((resultsDuration.get(i).getStatistics().getDislikeCount()).intValue());
        }
    }

    public static void searchFavorite(Context context) throws IOException {

        Database db = new Database(context);
        videoIds = new ArrayList<>(db.getAllVideosId());

        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        }).setApplicationName("MyApp").build();

        YouTube.Videos.List query = youtube.videos().list("id,contentDetails");
        query.setKey(KEY);
        query.setPart("contentDetails,statistics,snippet");
        query.set("id", StringUtils.join(videoIds, ","));

        VideoListResponse response = query.execute();

        lastStatusCode = query.getLastStatusCode();
        nextPageToken = null;
        totalResults = response.getPageInfo().getTotalResults();
        List<Video> results = response.getItems();
        items = new ArrayList<VideoItem>();

        for (int i = 0; i < results.size(); i++) {
            VideoItem item = new VideoItem();
            item.setTitle(results.get(i).getSnippet().getTitle());
            item.setDescription(results.get(i).getSnippet().getDescription());
            item.setThumbnail(results.get(i).getSnippet().getThumbnails().getMedium().getUrl());
            item.setId(results.get(i).getId());
            item.setPublishedAt(new Date(Long.parseLong(String.valueOf(results.get(i).getSnippet().getPublishedAt().getValue()))));
            item.setDuration(YouTubeDurationUtils.convertYouTubeDuration(results.get(i).getContentDetails().getDuration()));
            item.setViewCount((results.get(i).getStatistics().getViewCount()).intValue());
            item.setLikeCount((results.get(i).getStatistics().getLikeCount()).intValue());
            item.setDislikeCount((results.get(i).getStatistics().getDislikeCount()).intValue());
            items.add(item);
        }

    }

    public static String getChannelIdByName(Context context, String name) throws IOException {


        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        }).setApplicationName("MyApp").build();

        YouTube.Channels.List query = youtube.channels().list("brandingSettings,snippet");
        query.setKey(KEY);
        query.setForUsername(name);

        ChannelListResponse response = query.execute();
        lastStatusCode = query.getLastStatusCode();

        if (response.getItems().size() == 0)
            return ERROR_SEARCH_CHANNEL;
        else {
            Database db = new Database(context);
            Channel channel = new Channel();
            String channelId = response.getItems().get(0).getId();
            channel.set_channelid(channelId);
            channel.set_title(response.getItems().get(0).getSnippet().getTitle());
            channel.set_thumbnailurl(response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl());
            channel.set_bannerurl(response.getItems().get(0).getBrandingSettings().getImage().getBannerTvImageUrl());
            context.getSharedPreferences(MainActivity.PREFS_NAME, 0).edit().putString(MainActivity.KEY_CHANNEL_ID_CURRENT, channelId).commit();
            db.addChannel(channel);
            return channelId;

        }

    }


    public static int getTotalResults() {
        return totalResults;
    }

    public static String getNextPageToken() {
        return nextPageToken;
    }

    public static List<VideoItem> getItems() {
        return items;
    }

    public static int getLastStatusCode() {
        return lastStatusCode;
    }
}


class YouTubeDurationUtils {
    /**
     * @param duration
     * @return "01:02:30"
     */
    public static String convertYouTubeDuration(String duration) {
        String youtubeDuration = duration; //"PT1H2M30S"; // "PT1M13S";
        Calendar c = new GregorianCalendar();
        try {
            DateFormat df = new SimpleDateFormat("'PT'mm'M'ss'S'");
            Date d = df.parse(youtubeDuration);
            c.setTime(d);
        } catch (ParseException e) {
            try {
                DateFormat df = new SimpleDateFormat("'PT'hh'H'mm'M'ss'S'");
                Date d = df.parse(youtubeDuration);
                c.setTime(d);
            } catch (ParseException e1) {
                try {
                    DateFormat df = new SimpleDateFormat("'PT'ss'S'");
                    Date d = df.parse(youtubeDuration);
                    c.setTime(d);
                } catch (ParseException e2) {
                }
            }
        }
        c.setTimeZone(TimeZone.getDefault());

        String time = "";
        if (c.get(Calendar.HOUR) > 0) {
            if (String.valueOf(c.get(Calendar.HOUR)).length() == 1) {
                time += "0" + c.get(Calendar.HOUR);
            } else {
                time += c.get(Calendar.HOUR);
            }
            time += ":";
        }
        // test minute
        if (String.valueOf(c.get(Calendar.MINUTE)).length() == 1) {
            time += "0" + c.get(Calendar.MINUTE);
        } else {
            time += c.get(Calendar.MINUTE);
        }
        time += ":";
        // test second
        if (String.valueOf(c.get(Calendar.SECOND)).length() == 1) {
            time += "0" + c.get(Calendar.SECOND);
        } else {
            time += c.get(Calendar.SECOND);
        }
        return time;
    }
}