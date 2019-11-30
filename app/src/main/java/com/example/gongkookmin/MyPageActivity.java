/* 작성자 : 이재욱
 *  작성 시간 : 2019년 11월 29일 02시 15분 */
package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class MyPageActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initArticleList();
    }

    public void initArticleList(){

        listView = (ListView) findViewById(R.id.articlesListView);
        ListViewAdapter adapter = new ListViewAdapter();

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shark), "Example 1", "Mr. A",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.coffee), "Example 2", "Ms. B", new Date());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    /* 업데이트 날짜 : 2019년 12월 1일 - MyPageActivity 속 ListView 아이템에도 클릭 리스너 설정 */
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
}