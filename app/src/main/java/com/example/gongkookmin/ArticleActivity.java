/* 작성자 : 이재욱
* 작성 일자 : 2019년 11월 29일 2시 30분 */

package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ArticleActivity extends AppCompatActivity {

    int id;

    Button btnKakaotalk;
    ProgressBar progressBar;
    TextView detailBody;
    RecyclerView pictureListView;

    Toolbar toolbar;
    ActionBar actionBar;
    ArrayList<Bitmap> bitmapImages = new ArrayList<Bitmap>();

    TokenHelper tokenHelper;
    PictureListViewAdapter picListAdapter;

    /* MainActivity 에서 보낸 데이터틀을 Intent를 통해서 받고 activity_article 위에 띄운다. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        detailBody = findViewById(R.id.detail_Body_TextView);
        btnKakaotalk = findViewById(R.id.btnOpenKakaoLink);
        progressBar = findViewById(R.id.detail_progressBar);

        toolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initPicureList();

        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        BackgroundTask task = new BackgroundTask();
        task.execute(getResources().getString(R.string.server_address)+"offer/"+id
                ,HttpRequestHelper.GET,null);

        if(picListAdapter.getItemCount() == 0){
            bitmapImages.add(BitmapFactory.decodeResource(getResources(),R.drawable.basic_image));
            pictureListView.getAdapter().notifyItemRangeChanged(0,1);
            pictureListView.getAdapter().notifyItemInserted(0);
            pictureListView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piclistview_appbar_action,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    public void initPicureList() {
        pictureListView = (RecyclerView) findViewById(R.id.pictureViewerListView);
        pictureListView.setLayoutManager(new LinearLayoutManager(this));
        pictureListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        picListAdapter = new PictureListViewAdapter(getApplicationContext(), bitmapImages, PictureListViewAdapter.VIEW_MODE);

        pictureListView.setAdapter(picListAdapter);
    }

    class BackgroundTask extends CommunicationTask{
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Token = tokenHelper.getToken();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            JSONObject jsonObject;
            jsonObject = httpRequestHelper.getDataByJSONObject();   // 서버에게 받은 json
            if (jsonObject == null) {
                Toast.makeText(ArticleActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if (values[0]) {
                try {
                    Iterator<String> keys = jsonObject.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        switch(key){
                            case "id":{
                                //DO NOTHING
                            }
                            case "owner":{
                                //TODO
                            }
                            case "owner_name":{
                                //TODO
                                break;
                            }
                            case "title":{
                                String value = jsonObject.getString(key);
                                actionBar.setTitle(value);
                                break;
                            }
                            case "body":{
                                String value = jsonObject.getString(key);
                                detailBody.setText(value);
                                break;
                            }
                            case "created_at":{
                                // TODO
                                break;
                            }
                            case "open_kakao_link":{
                                final String value = jsonObject.getString(key);
                                btnKakaotalk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent_link = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
                                        startActivity(intent_link);
                                    }
                                });
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ArticleActivity.this, "" + httpRequestHelper.getData(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
