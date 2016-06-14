package com.codepath.apps.mysimpletweets.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activity.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.List;

/**
 * Created by bkakran on 6/5/16.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {


    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserId = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserId);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);

        tvUserId.setText("@" + tweet.getUser().getScreenName());
        tvUserName.setText(tweet.getUser().getName());
        tvBody.setText(tweet.getBody());
        tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                User user = tweet.getUser();
                intent.putExtra("user", user);
                v.getContext().startActivity(intent);
            }
        });
        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext())
                .load(tweet.getUser().getProfileImageUrl()).placeholder(R.drawable.user_placeholder).error(R.drawable.user_placeholder_error)
                .override(100,100).fitCenter()
                .into(ivProfileImage);
        //Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).resize(100,100).into(ivProfileImage);
        TextView relativeTimeView = (TextView) convertView.findViewById(R.id.tvTime);
        relativeTimeView.setText(tweet.getRelativeTime());
        return convertView;
    }
}
