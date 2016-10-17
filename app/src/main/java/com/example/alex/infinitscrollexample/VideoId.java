package com.example.alex.infinitscrollexample;

public class VideoId {

    //private variables
    int _id;
    String _videoid;

    // Empty constructor
    public VideoId(){

    }
    // constructor
    public VideoId(int id, String videoId){
        this._id = id;
        this._videoid = videoId;
    }

    // constructor
    public VideoId(String videoId){
        this._videoid = videoId;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_videoid() {
        return _videoid;
    }

    public void set_videoid(String _videoid) {
        this._videoid = _videoid;
    }
}