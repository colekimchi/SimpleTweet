package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    public String body;
    public String createdAt;
    public Long id;
    public User user;
    public int numRetweets;
    public int numFavorites;

    public static Tweet fromJson(JSONObject jsonTweet) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonTweet.getString("text");
        tweet.createdAt = jsonTweet.getString("created_at");
        tweet.user = User.fromJson(jsonTweet.getJSONObject("user"));
        tweet.id = jsonTweet.getLong("id");
        tweet.numFavorites = jsonTweet.getInt("favorite_count");
        tweet.numRetweets = jsonTweet.getInt("retweet_count");
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(createdAt);
    }


    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public int getNumRetweets() {
        return numRetweets;
    }

    public int getNumFavorites() {
        return numFavorites;
    }
}
