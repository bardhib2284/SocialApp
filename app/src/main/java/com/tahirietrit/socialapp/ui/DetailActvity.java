package com.tahirietrit.socialapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tahirietrit.socialapp.R;
import com.tahirietrit.socialapp.model.feed.Post;

public class DetailActvity extends Activity {

    ImageView profilePic;
    ImageView postPic;
    TextView username;
    TextView uploadTime;
    TextView desc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Post post = getIntent().getParcelableExtra("extra_parcel");
        System.out.println("post "+ post.toString());
        profilePic = findViewById(R.id.profile_picture);
        postPic = findViewById(R.id.post_picture);
        username = findViewById(R.id.username_textview);
        uploadTime = findViewById(R.id.upload_time);
        desc = findViewById(R.id.post_desc);
        Glide.with(this).load(post.photoUrl).into(postPic);
        username.setText(post.username);
        uploadTime.setText(post.createdDate);
        desc.setText(post.pershkrimi);
    }
}
