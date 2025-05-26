package com.example.jomphoto;

public class Photo {
    private String uri;
    private String annotate;

    public Photo(String uri, String annotate) {
        this.uri = uri;
        this.annotate = annotate;
    }

    public String getUri() {
        return uri;
    }

    public String getAnnotate() {
        return annotate;
    }
}

