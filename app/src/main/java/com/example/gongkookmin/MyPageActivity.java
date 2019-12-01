/* 작성자 : 이재욱
 *  작성 시간 : 2019년 11월 29일 02시 15분 */
package com.example.gongkookmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class MyPageActivity extends AppCompatActivity implements ListView.OnItemClickListener
        , AbsListView.OnScrollListener {

    ListView listView;

    BackgroundTask task;
    TokenHelper tokenHelper;

    ProgressBar progressBar;

    Toolbar toolbar;
    ActionBar actionBar;
    ListViewAdapter adapter;

    String nextURL;
    boolean isListEnd = false;
    boolean isItemEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));
        progressBar = findViewById(R.id.mypage_progressBar);
        toolbar = findViewById(R.id.toolbar_mypage);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("내 공구함");
        actionBar.setDisplayHomeAsUpEnabled(true);

        initArticleList();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        isListEnd = task.isListEnd();
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isItemEnd){
            if(isListEnd){
                Toast.makeText(this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            task = new BackgroundTask(getApplicationContext(),adapter);
            task.execute(nextURL,HttpRequestHelper.GET,null);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        isItemEnd = (i2 > 0) && (i+i1 >= i2);
    }


    public void initArticleList(){

        listView = (ListView) findViewById(R.id.mypage_articlesListView);
        adapter = new ListViewAdapter();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);

        task = new BackgroundTask(getApplicationContext(),adapter);
        task.execute(getResources().getString(R.string.server_address)+"my-offer"
                ,HttpRequestHelper.GET
                ,null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {    // appbar의 뒤로가기
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        UserArticlesListViewItem item = (UserArticlesListViewItem) adapterView.getItemAtPosition(i);
        int id = item.getId();

        Intent intent = new Intent(getApplication(), ArticleActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    class BackgroundTask extends ListGetTask{

        public BackgroundTask(Context context, ListViewAdapter listViewAdapter){
            super(context,listViewAdapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Token = (tokenHelper.getToken());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.GONE);
            nextURL = getNextURL();
        }
    }

}