/* 작성자 : 이재욱
* 작성 일자 : 2019년 11월 29일 2시 30분 */

package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class ArticleActivity extends AppCompatActivity {

    RecyclerView pictureListView;
    Toolbar toolbar;
    ActionBar actionBar;
    ArrayList<Bitmap> bitmapImages = new ArrayList<Bitmap>();

    /* MainActivity 에서 보낸 데이터틀을 Intent를 통해서 받고 activity_article 위에 띄운다. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();
        String authorStr = intent.getStringExtra("author");

        /* Convert byte[] to Bitmap */
        byte[] byteArray =  intent.getByteArrayExtra("iconBitmap");
        Bitmap iconBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        bitmapImages.add(iconBitmap);

        toolbar = findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(authorStr);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* 될까 싶어서 아무 오픈 카톡 주소를 넣어봤지만 작성자의 AVD에 카톡이 없어서 그런지
        * 제대로 확인이 되지 않음. */
        Button btnKakaoLink = (Button) findViewById(R.id.btnOpenKakaoLink);    // 버튼을 누르면 오픈 카톡방으로 연결
        btnKakaoLink.setOnClickListener(new View.OnClickListener() {    // 버튼 클릭 시 오픈 카톡 채팅방으로 연결.
            @Override
            public void onClick(View v) {
                String testLink = "https://open.kakao.com/o/sXFmeQO";
                Intent intent_link = new Intent(Intent.ACTION_VIEW, Uri.parse(testLink));
                startActivity(intent_link);
            }
        });

        initPicureList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.piclistview_appbar_action,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:    // actionBar 안에 있는 뒤로 가기 화살표를 누르면 액티비티 종료.
                finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    public void initPicureList() {
        pictureListView = (RecyclerView) findViewById(R.id.pictureViewerListView);
        pictureListView.setLayoutManager(new LinearLayoutManager(this));
        PictureListViewAdapter picListAdapter = new PictureListViewAdapter(getApplicationContext(), bitmapImages, 1);

        pictureListView.setAdapter(picListAdapter);
    }
}
