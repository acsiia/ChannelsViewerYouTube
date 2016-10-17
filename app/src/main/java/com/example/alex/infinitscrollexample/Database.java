package com.example.alex.infinitscrollexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 25.06.2016.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "youtubeManager";
    private static final String TABLE_VIDEO = "video";
    private static final String TABLE_CHANNEL = "channel";

    //  video table columns
    private static final String KEY_ID = "id";
    private static final String KEY_VIDEO_ID = "videoid";

    //  channel table columns
    private static final String KEY_CHANNEL_ID = "channelid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BANNER_URL = "bannerurl";
    private static final String KEY_THUMBNAIL_URL = "thumbnailurl";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_VIDEO + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_VIDEO_ID + " TEXT" + ")";
        db.execSQL(CREATE_VIDEO_TABLE);
        String CREATE_CHANNEL_TABLE = "CREATE TABLE " + TABLE_CHANNEL + "("
                + KEY_CHANNEL_ID + " TEXT PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_BANNER_URL + " TEXT," + KEY_THUMBNAIL_URL + " TEXT" + ")";
        db.execSQL(CREATE_CHANNEL_TABLE);
        db.execSQL("insert into " + TABLE_CHANNEL + "(" + KEY_CHANNEL_ID + ","
                + KEY_TITLE +"," + KEY_BANNER_URL +"," + KEY_THUMBNAIL_URL + ") values('UCNzJmjkZRlUj15RpERotAEg','Netco Sports','https://yt3.ggpht.com/-UIZdGyWbhOQ/VNzCaIkhJ-I/AAAAAAAAAIk/4FK-tOGnIXQ/w2120-fcrop64=1,00000000ffffffff-nd-c0xffffffff-rj-k-no/FIAWEC.jpg','https://yt3.ggpht.com/-YzF5LJ9nLtI/AAAAAAAAAAI/AAAAAAAAAAA/G9ZugB4TT74/s88-c-k-no-rj-c0xffffff/photo.jpg')");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHANNEL);
        // Create tables again
        onCreate(db);
    }

    public boolean getIsVideoFavorite(String videoId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VIDEO, new String[]{KEY_ID,
                        KEY_VIDEO_ID}, KEY_VIDEO_ID + "=?",
                new String[]{String.valueOf(videoId)}, null, null, null, null);
        if (cursor.getCount() > 0)
            return true;
        return false;
    }

    // Adding new videoId
    public void addVideo(VideoId videoId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, videoId.get_videoid()); // VideoId Name

        // Inserting Row
        db.insert(TABLE_VIDEO, null, values);
        db.close(); // Closing database connection
    }

    // Getting single videoId
    public VideoId getVideo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VIDEO, new String[]{KEY_ID,
                        KEY_VIDEO_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        VideoId videoId = new VideoId(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return videoId
        return videoId;
    }

    // Getting All Videos
    public List<String> getAllVideosId() {
        List<String> videoIdList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_VIDEO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding videoId to list
                videoIdList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return videoIdList;
    }

    public int getVideosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_VIDEO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating single videoId
    public int updateVideo(VideoId videoId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, videoId.get_videoid());

        // updating row
        return db.update(TABLE_VIDEO, values, KEY_ID + " = ?",
                new String[]{String.valueOf(videoId.get_id())});
    }

    // Deleting single videoId
    public void deleteVideo(String videoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VIDEO, KEY_VIDEO_ID + " = ?",
                new String[]{videoId});
        db.close();
    }



    // Adding new channel
    public void addChannel(Channel channel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHANNEL_ID, channel.get_channelid());
        values.put(KEY_TITLE, channel.get_title());
        values.put(KEY_BANNER_URL, channel.get_bannerurl());
        values.put(KEY_THUMBNAIL_URL, channel.get_thumbnailurl());

        // Inserting Row
        db.insert(TABLE_CHANNEL, null, values);
        db.close(); // Closing database connection
    }

    // Deleting single channel
    public void deleteChannel(String channelId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CHANNEL, KEY_CHANNEL_ID + " = ?",
                new String[]{channelId});
        db.close();
    }

    // Getting All Channels
    public List<Channel> getAllChannels() {
        List<Channel> channelList = new ArrayList<Channel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CHANNEL;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Channel channel = new Channel();
                channel.set_id(cursor.getInt(0));
                channel.set_title(cursor.getString(1));
                channel.set_bannerurl(cursor.getString(2));
                channel.set_thumbnailurl(cursor.getString(3));
                channelList.add(channel);
            } while (cursor.moveToNext());
        }
        return channelList;
    }

    // Getting single videoId
    public Channel getChannel(String channelId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CHANNEL, new String[]{KEY_CHANNEL_ID,
                        KEY_TITLE,KEY_BANNER_URL,KEY_THUMBNAIL_URL}, KEY_CHANNEL_ID + "=?",
                new String[]{channelId}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Channel channel = new Channel();
        channel.set_channelid(cursor.getString(0));
        channel.set_title(cursor.getString(1));
        channel.set_bannerurl(cursor.getString(2));
        channel.set_thumbnailurl(cursor.getString(3));
        return channel;
    }

}
