package com.codepath.apps.mysimpletweets.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {

    TextView userId;
    EditText tweetBody;
    Button compose;
    Button cancel;
    User user;
    TextView count;
    private static int MAX_CHARACTER = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        user = getIntent().getParcelableExtra("user");
        count = (TextView) findViewById(R.id.tvCount);
        compose = (Button) findViewById(R.id.btCompose);
        cancel = (Button) findViewById(R.id.btCancel);
        tweetBody = (EditText) findViewById(R.id.etComposeTweet);
        userId = (TextView) findViewById(R.id.tvUserId);
        user = (User) getIntent().getParcelableExtra("user");
        userId.setText("@" + user.getScreenName());
        getSupportActionBar().hide();
        registerKeyListner();
    }

    private void registerKeyListner() {
        tweetBody.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int length = tweetBody.getText().length();
                Integer remaining_character = MAX_CHARACTER - length;
                count.setText(remaining_character.toString());
                return false;
            }
        });
    }

    public void composeTweet(View view) {
        String tweet = String.valueOf(tweetBody.getText());
        if (TextUtils.isEmpty(tweet)) {
            Toast.makeText(ComposeTweetActivity.this, "OOps !!! Empty Tweet", Toast.LENGTH_SHORT).show();
            return;
        }
        TwitterClient client = TwitterApplication.getRestClient();
        client.postTweet(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent();
                intent.putExtra("tweet", Tweet.fromJson(response));
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ComposeTweetActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(ComposeTweetActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }, tweet);
    }


    public void cancel(View view) {
        this.finish();
    }
}
