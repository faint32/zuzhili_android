package com.zuzhili.bussiness.socket.model;

/**
 * Created by liutao on 14-4-14.
 */
public class AttachmentChat extends ChatMessage {
    /** attachment name */
    private String name;


    /** attachment url */
    private String url;

    /** attachment size */
    private String size;

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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
