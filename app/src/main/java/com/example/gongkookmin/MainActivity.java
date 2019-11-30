package com.example.gongkookmin;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , SwipeRefreshLayout.OnRefreshListener
        , ListView.OnItemClickListener
        , AbsListView.OnScrollListener {

    Toolbar toolbar;
    ActionBar actionBar;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    ListViewAdapter listAdapter;
    boolean isItemEnd = false;

    String nextURL;
    boolean isListEnd = false;

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
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isItemEnd){
            if(isListEnd){
                Toast.makeText(this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            BackgroundTask task = new BackgroundTask();
            task.execute(nextURL,HttpRequestHelper.GET,null);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        isItemEnd = (i2 > 0) && (i+i1 >= i2);
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
                TokenHelper tokenHelper = new TokenHelper(getSharedPreferences("pref",MODE_PRIVATE));
                tokenHelper.clearToken();
                Intent intent_logout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent_logout);
                finish();
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

        isListEnd = false;
        isItemEnd = false;
        listAdapter = new ListViewAdapter();
        listView.setAdapter(listAdapter);
        BackgroundTask task = new BackgroundTask();
        task.execute(getResources().getString(R.string.server_address)+"offer/"
                ,HttpRequestHelper.GET,null);
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
        listAdapter = new ListViewAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        swipeRefreshLayout = findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = findViewById(R.id.progressBar);
        listView.setOnScrollListener(this);

        BackgroundTask task = new BackgroundTask();
        task.execute(getResources().getString(R.string.server_address)+"offer/"
                ,HttpRequestHelper.GET,null);
    }

    class BackgroundTask extends CommunicationTask{
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            JSONObject jsonObject;
            jsonObject = httpRequestHelper.getDataByJSONObject();   // 서버에게 받은 json
            if(jsonObject == null) {
                Toast.makeText(MainActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if(values[0]){
                try {
                    if(jsonObject.isNull("next")){
                        isListEnd = true;
                    }
                    else{
                        String next = jsonObject.getString("next");
                        nextURL = (next);
                    }
                    JSONArray list = jsonObject.getJSONArray("results");
                    for(int i = 0;i<list.length();i++){
                        JSONObject offer = list.getJSONObject(i);
                        Drawable image;
                        if(offer.isNull("image")){
                            image = ContextCompat.getDrawable(MainActivity.this,R.drawable.basic_image);
                        }
                        else{
                            byte[] encodeByte = Base64.decode(offer.getString("image"),Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                            image = new BitmapDrawable(getApplicationContext().getResources(),bitmap);
                        }
                        String title = offer.getString("title");
                        String updateTime = offer.getString("updated_at");
                        String owner = offer.getString("owner");
                        listAdapter.addItem(image,title,owner,new Date());
                    }
                    listAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(MainActivity.this, ""+httpRequestHelper.getData(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


}
