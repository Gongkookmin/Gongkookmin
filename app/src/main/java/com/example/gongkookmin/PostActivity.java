package com.example.gongkookmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PostActivity extends AppCompatActivity {

    private final int PICK_FROM_ALBUM = 9999;

    Toolbar toolbar;
    ActionBar actionBar;
    RecyclerView pictureListView;
    EditText titleEditText;
    EditText articleEditText;
    EditText kakaotalkEditText;
    Button btnAddPicture;

    boolean lockPost = false;   // 글쓰기를 여러번 누를시 여러개의 글이 생기는 경우 방지
    boolean isPostComplete = false; // 글 작성이 완료된 경우. lockPost는 글작성 이전까지의 락, isPost는 작성 이후 종료까지의 락

    ArrayList<Bitmap> pictureList = new ArrayList<>();

    TokenHelper tokenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));
        titleEditText = findViewById(R.id.titleEditText);
        articleEditText = findViewById(R.id.articleEditText);
        kakaotalkEditText = findViewById(R.id.kakaotalkEditText);

        initPictureListView();

        toolbar = findViewById(R.id.toolbar_post);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("공국민 모여라!");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initPictureListView(){
        final PictureListViewAdapter adapter = new PictureListViewAdapter(getApplicationContext(),pictureList, PictureListViewAdapter.EDIT_MODE);
        btnAddPicture = findViewById(R.id.btn_addPicture);
        pictureListView = findViewById(R.id.pictureListView);
        pictureListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pictureListView.setAdapter(adapter);
        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pictureList.size() < 3) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_FROM_ALBUM);
                }
                else
                    Toast.makeText(PostActivity.this, "3장 이하로만 올릴 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;

        switch(requestCode){
            case PICK_FROM_ALBUM:{
                Uri uri = data.getData();
                Log.d("uri ",uri.getPath().toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    pictureList.add(bitmap);
                    pictureListView.getAdapter().notifyItemInserted(pictureListView.getAdapter().getItemCount()-1);
                    pictureListView.getAdapter().notifyItemRangeChanged(pictureListView.getAdapter().getItemCount()-1,pictureListView.getAdapter().getItemCount());
                    pictureListView.getAdapter().notifyDataSetChanged();
                }catch(FileNotFoundException e){
                    Log.d("uri ", "file not found");
                    e.printStackTrace();
                }catch (IOException e){
                    Log.d("uri ", "IO exeption");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{    // appbar의 뒤로가기
                finish();
                return true;
            }
            case R.id.action_write_post:{   // appbar의 작성 버튼
                if(lockPost || isPostComplete)
                    return true;
                String title = titleEditText.getText().toString().trim();
                String article = articleEditText.getText().toString().trim();
                String kakaotalk = kakaotalkEditText.getText().toString().trim();

                if(isArticleRuleOK(title,article,kakaotalk)){   // 규칙 확인
                    lockPost = true;
                    JsonMaker json = new JsonMaker();
                    json.putData("title",title);
                    json.putData("body",article);
                    json.putData("open_kakao_link",kakaotalk);

                    json.putData("owner", "1"); // TODO

                    BackgroundTask task = new BackgroundTask();

                    task.execute(getResources().getString(R.string.server_address) + "offer/"
                            ,HttpRequestHelper.POST,json.toString());
                }
                else
                    Toast.makeText(this, "양식에 맞게 글을 작성해 주세요.", Toast.LENGTH_SHORT).show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class BackgroundTask extends CommunicationTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Token = (tokenHelper.getToken());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            lockPost = false;   // 완료된 시점에서 글작성 락 해제
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
            if(values[0]){
                isPostComplete = true;
                Toast.makeText(PostActivity.this, "게시글을 작성했습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                Toast.makeText(PostActivity.this, "서버와의 연결에 문제가 있습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_appbar_action,menu);  // appbar에 menu 입히기
        return true;
    }

    public boolean isArticleRuleOK(String title, String article, String kakaotalk){
        int title_len = title.length();
        int article_len = article.length();
        if(title_len < 2 || title_len > 15) // 제목 길이 2 ~ 15 규칙 확인
            return false;
        if(article_len < 5 || article_len > 300) // 본문 길이 5 ~ 300 규칙 확인
            return false;

        String pattern = "^((http(s)?://)?open.kakao.com/o/)[a-zA-Z0-9]+$"; // 링크가 오픈카톡이 맞는지 확인

        if(Pattern.matches(pattern,kakaotalk)){
            return true;
        }
        return false;
    }
}
