/* 작성자 : 이재욱
* 작성 일자 : 2019년 12월 1일 2시 14분 */
package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.Date;


public class SearchActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initArticleList();    // MainActivity에서 사용한 ListViewAdapter를 SearchActivity에서도 똑같이 적용
    }

    public void initArticleList() {
        listView = (ListView) findViewById(R.id.searchedArticlesListView);
        ListViewAdapter adapter = new ListViewAdapter();

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.coffee), "Example 3", "Ms. C", new Date(), 3);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.shark), "Example 1", "Mr. A",new Date(), 1);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.nurse),  "Example 2", "Mr. B",new Date(), 2);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

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
