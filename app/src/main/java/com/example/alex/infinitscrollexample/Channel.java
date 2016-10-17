package com.example.alex.infinitscrollexample;

/**
 * Created by alex on 27.06.2016.
 */
public class Channel {
    int _id;
    String _channelid;
    String _title;
    String _bannerurl;
    String _thumbnailurl;

    public Channel() {
    }

    public Channel(int _id, String _channelid, String _title, String _bannerurl, String _thumbnailurl) {
        this._id = _id;
        this._channelid = _channelid;
        this._title = _title;
        this._bannerurl = _bannerurl;
        this._thumbnailurl = _thumbnailurl;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_channelid() {
        return _channelid;
    }

    public void set_channelid(String _channelid) {
        this._channelid = _channelid;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_bannerurl() {
        return _bannerurl;
    }

    public void set_bannerurl(String _bannerurl) {
        this._bannerurl = _bannerurl;
    }

    public String get_thumbnailurl() {
        return _thumbnailurl;
    }

    public void set_thumbnailurl(String _thumbnailurl) {
        this._thumbnailurl = _thumbnailurl;
    }
}
