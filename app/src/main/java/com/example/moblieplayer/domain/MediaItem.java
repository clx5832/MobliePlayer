package com.example.moblieplayer.domain;
import java.io.Serializable;

//作用代表视频或者音频
public class MediaItem implements Serializable {
    private String name;
    private long duration;
    private long size;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    private String artist;

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", artist='" + artist + '\'' +
                ", data='" + data + '\'' +
                ", vpic='" + vpic + '\'' +
                ", shortTitle='" + shortTitle + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", redirect_url='" + redirect_url + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }

    public void setVpic(String vpic) {
        this.vpic = vpic;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    private String data;

    public String getVpic() {
        return vpic;
    }

    private String vpic;
    private String shortTitle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    //*********************************************************************************
    private  String title;
    private String desc;
    private String redirect_url;
    private String pic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    //*********************************************************************************

}
