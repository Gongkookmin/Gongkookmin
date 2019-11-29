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

import java.util.Date;

public class MyPageActivity extends AppCompatActivity {

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
        listView.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shark), "Example 1", "Mr. A",new Date());
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.coffee), "Example 2", "Ms. B", new Date());
    }
}