package com.codepath.apps.mysimpletweets.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.Util;
import com.codepath.apps.mysimpletweets.activity.TimelineActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bkakran on 6/9/16.
 */
public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private User user;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragmentDemo = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        if (Util.isNetworkAvailable(getActivity())) {
            getAccountMetaData();
            populateTimeline(25, 0);
        } else {
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


    protected void populateTimeline(int count, long since_id) {
        TimelineActivity.showProgressBar();
        String screenname = getArguments().getString("screen_name");
        if (since_id > 0) since_id = getTweets().get(getTweets().size() - 1).getUid();
        client.getUserTimeline(screenname, count, since_id, new JsonHttpResponseHandler() {
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
        });
    }

}

