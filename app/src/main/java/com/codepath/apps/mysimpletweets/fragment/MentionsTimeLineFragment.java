package com.codepath.apps.mysimpletweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.Util;
import com.codepath.apps.mysimpletweets.activity.TimelineActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bkakran on 6/9/16.
 */
public class MentionsTimeLineFragment extends TweetsListFragment {

    private TwitterClient client;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        if (Util.isNetworkAvailable(getActivity())) {
            getAccountMetaData();
            populateTimeline(25, 0);
        } else {
            fetchDataFromDB();
            if (getTweets() == null) {
                Toast.makeText(getActivity(), "Internet unavailable !! No data in DB", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void getAccountMetaData() {
        TwitterApplication.getRestClient().getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                user = User.fromJson(json);
            }
        });
    }

    @Override
    protected void populateTimeline(int count, long max_id) {
        TimelineActivity.showProgressBar();
        if (max_id > 0) max_id = getTweets().get(getTweets().size() - 1).getUid();
        client.getMentionsTImeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJsonArray(response));
                swipeContainer.setRefreshing(false);
                TimelineActivity.hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                TimelineActivity.hideProgressBar();
            }
        }, count, max_id);

    }

    private void fetchDataFromDB() {
        user = new Select().from(User.class).executeSingle();
        List<Tweet> tweetList = new Select().from(Tweet.class).execute();
        if (tweetList != null) {
            addAll(tweetList);
        }
    }


}
