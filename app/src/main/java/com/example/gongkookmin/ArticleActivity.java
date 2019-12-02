/* 작성자 : 이재욱
* 작성 일자 : 2019년 11월 29일 2시 30분 */

package com.example.gongkookmin;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
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
    ArrayList<Uri> imageUri = new ArrayList<Uri>();

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
        actionBar.setTitle("");

        initPicureList();

        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        BackgroundTask task = new BackgroundTask();
        task.execute(getResources().getString(R.string.server_address)+"offer/"+id
                ,HttpRequestHelper.GET,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.btn_abuseUser:
                // TODO
                return true;
            case R.id.btn_deleteArticle:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("정말 삭제하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        BackgroundTask task = new BackgroundTask();
                        task.execute(getResources().getString(R.string.server_address)+"offer/"+id+"/"
                                , HttpRequestHelper.DELETE,null);
                    }
                });
                builder.show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void initPicureList() {
        pictureListView = (RecyclerView) findViewById(R.id.pictureViewerListView);
        pictureListView.setLayoutManager(new LinearLayoutManager(this));
        pictureListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        picListAdapter = new PictureListViewAdapter(getApplicationContext(), imageUri, PictureListViewAdapter.VIEW_MODE);

        pictureListView.setAdapter(picListAdapter);
    }

    class BackgroundTask extends CommunicationTask{

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.GONE);
            if(picListAdapter.getItemCount() == 0){
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.basic_image)+
                        '/'+getResources().getResourceTypeName(R.drawable.basic_image)+
                        '/'+getResources().getResourceEntryName(R.drawable.basic_image));
                imageUri.add(uri);
                pictureListView.getAdapter().notifyItemRangeChanged(0,1);
                pictureListView.getAdapter().notifyItemInserted(0);
                pictureListView.getAdapter().notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Token = tokenHelper.getToken();
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if(values[0]){
                if(httpRequestHelper.requestMethod == httpRequestHelper.DELETE){
                    String code = httpRequestHelper.getData();
                    Log.d("Response Code",code);
                    if(code.equals("204")){
                        Toast.makeText(ArticleActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }else if(code.equals("401") || code.equals("403")){
                        Toast.makeText(ArticleActivity.this, "권한이 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ArticleActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
            }
            JSONObject jsonObject;
            jsonObject = httpRequestHelper.getDataByJSONObject();   // 서버에게 받은 json
            if (jsonObject == null) {
                Toast.makeText(ArticleActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                return;
            }
            if (values[0]) {
                btnKakaotalk.setVisibility(View.VISIBLE);
                pictureListView.setVisibility(View.VISIBLE);
                detailBody.setVisibility(View.VISIBLE);
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
