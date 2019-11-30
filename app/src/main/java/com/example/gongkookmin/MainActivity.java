package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, ListView.OnItemClickListener{

    Toolbar toolbar;
    ActionBar actionBar;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.baseline_menu_black_18dp);

        initDrawerLayout();
        initArticleList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_action,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_search:
                Intent intent_search = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent_search);
                return true;
            case R.id.action_post:
                Intent intent_post = new Intent(getApplicationContext(),PostActivity.class);
                startActivity(intent_post);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        /*작성자 : 이재욱
         * 작성 시간 : 2019년 11월 15일 23시 34분
         * 업데이트 : 2019년 11월 29일 16시 43분 - 내 공구상자로 넘어갈 수 있게 추가 */
        switch(menuItem.getItemId()){
            case R.id.btn_logout:    // 로그아웃을 큺릭하면 로그인 창으로 넘어간다.
                Intent intent_logout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent_logout);
                break;
            case R.id.btn_mypage:    // 내 공구상자를 클릭하면 MyPageActivity로 넘어간다.
                Intent intent_mypage = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(intent_mypage);
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {

        // TODO
        swipeRefreshLayout.setRefreshing(false);
    }

    /* 작성자 : 이재욱
       업데이트 : 2019년 11월 29일 2시 30분
       작성자 이름과 사진을 인텐트를 통해서 ArticleActivity로 보낼 수 있게 기능을 추가하였다. */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {    // item을 클릭하면 ArticleActivity로 넘어간다.

        UserArticlesListViewItem item = (UserArticlesListViewItem) adapterView.getItemAtPosition(i);
        String authorStr = item.getAuthor();
        String titleStr = item.getTitle();
        Drawable iconDrawable = item.getIcon();

        /* Change drawable object to bitmap object for sending via intent */
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap iconBitmap = ((BitmapDrawable) iconDrawable).getBitmap();
        float scale = (float) (1024 / (float)iconBitmap.getWidth());
        int width = (int) (iconBitmap.getWidth() * scale);
        int height = (int) (iconBitmap.getHeight() * scale);
        Bitmap resizedIcon = Bitmap.createScaledBitmap(iconBitmap, width, height, true);
        resizedIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = new Intent(getApplication(), ArticleActivity.class);
        intent.putExtra("author", authorStr);
        intent.putExtra("iconBitmap", byteArray);
        startActivity(intent);

    }

    public void initDrawerLayout(){
        drawer = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        drawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawer.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tv_linkToTOS = findViewById(R.id.tv_linkToTOS);
        tv_linkToTOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),TOSActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initArticleList(){

        listView = (ListView) findViewById(R.id.articlesListView);
        ListViewAdapter adapter = new ListViewAdapter();

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shark), "Example 1", "Mr. A",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.nurse),  "Example 2", "Mr. B",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.coffee), "Example 3", "Ms. C", new Date());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }
}
