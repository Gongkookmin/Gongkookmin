package com.example.gongkookmin;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;
import java.util.List;

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
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
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
         * 작성 시간 : 2019년 11월 15일 23시 34분 */
        switch(menuItem.getItemId()){
            case R.id.btn_logout:    // 로그아웃을 큺릭하면 로그인 창으로 넘어간다.
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {

        // TODO

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        UserArticlesListViewItem item = (UserArticlesListViewItem) adapterView.getItemAtPosition(i);
        String titleStr = item.getTitle();
        String authorStr = item.getAuthor();
        Drawable iconDrawable = item.getIcon();

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
        listView.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shark), "Example 1", "Mr. A",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.nurse),  "Example 2", "Mr. B",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.coffee), "Example 3", "Ms. C", new Date());

        listView.setOnItemClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }
}
