package me.cutmail.disasterapp.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Entry")
public class Entry extends ParseObject {

    public String getTitle() {
        return getString("title");
    }

    public String getUrl() {
        return getString("url");
    }
}
