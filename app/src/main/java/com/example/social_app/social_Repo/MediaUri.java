package com.example.social_app.social_Repo;

import android.net.Uri;

public class MediaUri {
    Uri media_uri;
    String type;

    public MediaUri(Uri media_uri) {
        this.type = type;
        this.media_uri = media_uri;
    }

    public MediaUri() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getMedia_uri() {
        return media_uri;
    }

    public void setMedia_uri(Uri media_uri) {
        this.media_uri = media_uri;
    }
}
