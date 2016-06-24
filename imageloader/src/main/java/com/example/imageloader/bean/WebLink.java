package com.example.imageloader.bean;

/**
 * Created by nBB on 16/6/21.
 */
public class WebLink {
    private int icon;
    private String name;
    private String url;

    public WebLink(String name, int icon, String url) {
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    public WebLink() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WebLink{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
