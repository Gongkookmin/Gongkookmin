/* 작성자 : 이재욱
* 작성 일자 : 2019년 12월 1일 2시 14분 */
package com.example.gongkookmin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class SearchActivity extends AppCompatActivity implements ListView.OnItemClickListener
        , AbsListView.OnScrollListener
        , View.OnClickListener {

    ProgressBar search_progressBar;
    ListView listView;
    ListViewAdapter listAdapter;
    Button searchButton;
    EditText searchInput;
    boolean isListEnd = false;
    String nextURL;
    boolean isItemEnd = false;
    TokenHelper tokenHelper;

    BackgroundTask backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));
        searchButton = findViewById(R.id.search_button);
        searchInput = findViewById(R.id.search_input);
        searchButton.setOnClickListener(this);
        initArticleList();
    }

    public void initArticleList() {
        search_progressBar = findViewById(R.id.search_progressBar);
        listView = (ListView) findViewById(R.id.searchedArticlesListView);
        ListViewAdapter adapter = new ListViewAdapter();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        isListEnd = false;
        isItemEnd = false;
        listAdapter = new ListViewAdapter();
        listView.setAdapter(listAdapter);
        backgroundTask = new BackgroundTask(getApplicationContext(),listAdapter);
        String query = searchInput.getText().toString().trim();
        if(query.equals(""))
            return;
        search_progressBar.setVisibility(View.VISIBLE);
        backgroundTask.execute(getResources().getString(R.string.server_address)+"search?keyword="+query
                , HttpRequestHelper.GET,null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {    // item을 클릭하면 ArticleActivity로 넘어간다.

        UserArticlesListViewItem item = (UserArticlesListViewItem) adapterView.getItemAtPosition(i);
        int id = item.getId();

        Intent intent = new Intent(getApplication(), ArticleActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        isListEnd = backgroundTask.isListEnd();
        if(i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isItemEnd){
            if(isListEnd){
                Toast.makeText(this, "마지막 페이지 입니다", Toast.LENGTH_SHORT).show();
                return;
            }
            search_progressBar.setVisibility(View.VISIBLE);
            backgroundTask = new BackgroundTask(getApplicationContext(),listAdapter);
            backgroundTask.execute(nextURL,HttpRequestHelper.GET,null);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        isItemEnd = (i2 > 0) && (i+i1 >= i2);
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
            search_progressBar.setVisibility(View.GONE);
            nextURL = getNextPage();
        }
    }
}