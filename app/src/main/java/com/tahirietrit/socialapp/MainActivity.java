package com.tahirietrit.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tahirietrit.socialapp.api.ApiService;
import com.tahirietrit.socialapp.api.Servicefactory;
import com.tahirietrit.socialapp.callbacks.AdapterCallbacks;
import com.tahirietrit.socialapp.model.feed.FeedResponse;
import com.tahirietrit.socialapp.model.feed.Post;
import com.tahirietrit.socialapp.prefs.AppPreferences;
import com.tahirietrit.socialapp.ui.DetailActvity;
import com.tahirietrit.socialapp.ui.ImageActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterCallbacks {
    ListView listView;
    ProgressBar progressBar;
    FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.feed_listview);
        progressBar = findViewById(R.id.loader);
        adapter = new FeedAdapter(getLayoutInflater(), this);
        listView.setAdapter(adapter);
        getFeed();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Post Feed");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menuUpload:
                Intent intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
                break;

            case R.id.menuProfile:
                Toast.makeText(this, "You clicked profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLogout:
                Toast.makeText(this, "You clicked logout", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void getFeed() {
        ApiService apiService = Servicefactory.retrofit.create(ApiService.class);
        Call<FeedResponse> call = apiService.getFeed("",
                AppPreferences.getUserid());
        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                adapter.setFeedPosts(clearPosts(response.body().postet));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Wrong combination",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayList<Post> clearPosts(ArrayList<Post> feed) {
        for (int i = 0; i < feed.size(); i++) {
            if (!feed.get(i).photoUrl.endsWith(".jpg") || !feed.get(i).photoUrl.endsWith(".png")) {
                feed.remove(i);
            }
        }
        return feed;
    }

    @Override
    public void openPost(Post post) {
        Intent intent = new Intent(this, DetailActvity.class);
        intent.putExtra("extra_parcel", post);
        startActivity(intent);
    }
}
