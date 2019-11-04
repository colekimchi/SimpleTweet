package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView timeline;
    List<Tweet> tweets = new ArrayList<Tweet>();
    TweetsAdapter tweetsAdapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        //set up swipe refresh with an OnRefreshListener
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });


        //find the recyclerview
        timeline = findViewById(R.id.timeline);
        //tweets = Tweet.fromJsonArray(//tweet JSON here)
        //init tweets + adapter
        tweetsAdapter = new TweetsAdapter(this, tweets);
        //recycler view setup
        LinearLayoutManager llm = new LinearLayoutManager(this);
        timeline.setLayoutManager(llm);
        timeline.setAdapter(tweetsAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        timeline.addOnScrollListener(scrollListener);

        populateHomeTimeline();
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //  --> Deserialize and construct new model objects from the API response
                Log.i(TAG,"Onsuccess for loadmoredata "+json.toString());
                //Log.i(TAG,"Current last maxId "+tweets.get(tweets.size()-1).getId());

                try {
                    //int oldSize = tweets.size();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    //tweetsAdapter.notifyItemRangeInserted(oldSize,tweets.size());
                    Log.i(TAG,"New maxId "+tweets.get(tweets.size()-1).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "jsonexc, failed to get more data", throwable);
            }
        }, tweets.get(tweets.size()-1).getId());
    }

    public void fetchTimelineAsync(int page){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                //clear out old items before appending new ones
                tweetsAdapter.clear();
                try {
                    tweetsAdapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "jsonexc", e );
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "onFailure", throwable);
            }
        });
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG,"onSuccess");
                try {
                    //tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    //tweetsAdapter.notifyDataSetChanged();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "jsonexc", e );

                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"onSuccess", throwable);
            }
        });
    }
}
