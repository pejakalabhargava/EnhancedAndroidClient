package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by bkakran on 6/5/16.
 */

@Table(name = "Tweet")
public class Tweet extends Model implements Parcelable {
    @Column(name = "body")
    private String body;

    @Column(name = "uid")
    private long uid;//unique id for the tweet

    @Column(name = "user")
    private User user;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "relativeTime")
    private String relativeTime;


    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.relativeTime = getRelativeTimeAgo(tweet.createdAt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }

    public static Collection<? extends Tweet> fromJsonArray(JSONArray response) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int x = 0; x < response.length(); x++) {
            try {
                Tweet tweet = fromJson(response.getJSONObject(x));
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.uid);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.createdAt);
        dest.writeString(this.relativeTime);
    }

    public Tweet() {
        super();
    }

    protected Tweet(Parcel in) {
        this.body = in.readString();
        this.uid = in.readInt();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.createdAt = in.readString();
        this.relativeTime = in.readString();
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where("uid = ?", id).executeSingle();
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy("id DESC").limit("300").execute();
    }
}
