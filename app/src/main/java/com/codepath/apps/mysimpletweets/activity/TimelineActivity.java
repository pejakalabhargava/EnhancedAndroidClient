package com.codepath.apps.mysimpletweets.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.Adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    private TwitterClient client;
    private TweetsArrayAdapter aTweets;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    public static final int COMPOSETWEETRESULT = 20;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ActiveAndroid.initialize(this);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        client = TwitterApplication.getRestClient();
        setUpInfinteScroll();
        setUpRefreshListner();
        if (isNetworkAvailable()) {
            getAccountMetaData();
            populateTimeline(25, 1);
        } else {
            fetchDataFromDB();
            if (tweets == null) {
                Toast.makeText(this, "Internet unavailable !! No data in DB", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchDataFromDB() {
        user = new Select().from(User.class).executeSingle();
        List<Tweet> tweetList = new Select().from(Tweet.class).execute();
        if (tweetList != null) {
            aTweets.addAll(tweetList);
        }
    }

    private void setUpRefreshListner() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                aTweets.clear();
                if (isNetworkAvailable()) {
                    populateTimeline(25, 1);
                } else {
                    fetchDataFromDB();
                }
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void setUpInfinteScroll() {
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populateTimeline(25, page * 25);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    private void populateTimeline(int count, int since_id) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                aTweets.addAll(Tweet.fromJsonArray(response));
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                swipeContainer.setRefreshing(false);
                fetchDataFromDB();
            }
        }, count, since_id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_tweet) {
            launchComposeTweetActivity();
        }
        //navigateToURL(null);

        return super.onOptionsItemSelected(item);
    }

    private void launchComposeTweetActivity() {
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        i.putExtra("user", user);
        startActivityForResult(i, COMPOSETWEETRESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (COMPOSETWEETRESULT == requestCode && resultCode == RESULT_OK) {
            Tweet tweet = (Tweet) data.getParcelableExtra("tweet");
            Toast.makeText(this, "Tweet posted!!", Toast.LENGTH_SHORT).show();
            aTweets.insert(tweet, 0);
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

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user != null) {
            new Delete().from(Tweet.class).execute();
            new Delete().from(User.class).execute();
            user.save();
        }
        if (tweets != null && tweets.size() > 0) {
            for (Tweet tweet : tweets) {
                tweet.getUser().save();
                tweet.save();
            }
        }

    }
}
