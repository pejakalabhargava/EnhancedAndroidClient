package com.codepath.apps.mysimpletweets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.Util;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {


    public static final int COMPOSETWEETRESULT = 20;
    private User user;
    private TweetsPagerAdapter tweetPageAdapter;
    private static MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        initialize();

    }

    private void initialize() {
        if (!Util.isNetworkAvailable(this)) {
            Toast.makeText(this, "Internet unavailable !! No data in DB", Toast.LENGTH_SHORT).show();
        } else {
            TwitterClient client = TwitterApplication.getRestClient();
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    user = User.fromJson(response);
                    getSupportActionBar().setTitle("@" + user.getScreenName());
                    setUpViews();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    setUpViews();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    setUpViews();
                }
            });
        }
    }

    private void setUpViews() {
        //Get view pager
        ViewPager vpPage = (ViewPager) findViewById(R.id.viewpager);
        tweetPageAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPage.setAdapter(tweetPageAdapter);
        //Set the viewpager adapter for the pager
        //FInd the pager slidings tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPage);
        //Attach the tabstrip to the viewpager
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
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
        }
    }


    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public static void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public static  void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

}

