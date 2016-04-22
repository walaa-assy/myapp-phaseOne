package com.example.android.movieguide.app;

/**
 * Created by Administrator on 4/21/2016.
 */
public class ExtraBase {

    String movieNumber;
    String tagID;
    String type;
}

class Trailers extends  ExtraBase{

    String key;
    String name;
    String site;
    String size;

    public Trailers(String key, String name){
        super();
        this.key=key;
        this.name=name;
    }
}