package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    public String name;
    public String screenName;
    public String publicImageUrl;

    public static User fromJson (JSONObject jsonUser) throws JSONException {
        User user = new User();
        user.name = jsonUser.getString("name");
        user.screenName = jsonUser.getString("screen_name");
        user.publicImageUrl = jsonUser.getString("profile_image_url_https");
        return user;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getPublicImageUrl() {
        return publicImageUrl;
    }
}
