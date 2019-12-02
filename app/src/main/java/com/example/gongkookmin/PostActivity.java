package com.example.gongkookmin;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    RadioGroup radioGroup;

    boolean lockPost = false;   // 글쓰기를 여러번 누를시 여러개의 글이 생기는 경우 방지
    boolean isPostComplete = false; // 글 작성이 완료된 경우. lockPost는 글작성 이전까지의 락, isPost는 작성 이후 종료까지의 락

    ArrayList<Uri> pictureList = new ArrayList<>();

    TokenHelper tokenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        tokenHelper = new TokenHelper(getSharedPreferences(TokenHelper.PREF_NAME,MODE_PRIVATE));
        titleEditText = findViewById(R.id.titleEditText);
        articleEditText = findViewById(R.id.articleEditText);
        kakaotalkEditText = findViewById(R.id.kakaotalkEditText);
        radioGroup = findViewById(R.id.deadlineRadiGroup);

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
                Log.d("uri ",uri.toString());
                pictureList.add(uri);
                pictureListView.getAdapter().notifyItemInserted(pictureListView.getAdapter().getItemCount()-1);
                pictureListView.getAdapter().notifyItemRangeChanged(pictureListView.getAdapter().getItemCount()-1,pictureListView.getAdapter().getItemCount());
                pictureListView.getAdapter().notifyDataSetChanged();

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
                String expires = "";
                switch(radioGroup.getCheckedRadioButtonId()){
                    case R.id.btn3Hour:
                        expires = "3";
                        break;
                    case R.id.btn24Hour:
                        expires = "24";
                        break;
                    case R.id.btn7days:
                        expires = "168";
                        break;
                    case R.id.btnInfiniteHour:
                        expires = "none";
                        break;
                }

                if(isArticleRuleOK(title,article,kakaotalk)){   // 규칙 확인
                    lockPost = true;
                    HashMap<String,String> json = new HashMap<>();
                    json.put("url",getResources().getString(R.string.server_address)+"offer/");
                    json.put("title",title);
                    json.put("body",article);
                    json.put("open_kakao_link",kakaotalk);
                    json.put("expires",expires);

                    for(int i = 0;i<pictureListView.getAdapter().getItemCount();i++){
                        String label = "";
                        switch(i){
                            case 0: label = "image"; break;
                            case 1: label = "image2"; break;
                            case 2: label = "image3"; break;
                        }
                        PictureListViewAdapter adapter = (PictureListViewAdapter)pictureListView.getAdapter();
                        json.put(label,adapter.getItem(i).toString());
                    }

                    BackgroundTask task = new BackgroundTask();
                    task.execute(json);
                }
                else
                    Toast.makeText(this, "양식에 맞게 글을 작성해 주세요.", Toast.LENGTH_SHORT).show();

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class BackgroundTask extends AsyncTask<HashMap<String,String>,Boolean,Boolean>{

        class httpHelper{
            public static final String requestMethod = "POST";
            String boundary;
            String lineEnd = "\r\n";
            private HttpURLConnection httpConn;
            private String charset;
            private OutputStream outputStream;
            private PrintWriter writer;
            private String TOKEN;


            public httpHelper(String requestURL, String charset)
                    throws IOException {
                this.charset = charset;
                TOKEN = tokenHelper.getToken();
                // creates a unique boundary based on time stamp
                boundary = "===" + System.currentTimeMillis() + "===";

                URL url = new URL(requestURL);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod(requestMethod);
                httpConn.setConnectTimeout(10000);
                httpConn.setReadTimeout(10000);
                httpConn.setDoOutput(true);
                httpConn.setDoInput(true);
                httpConn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);
                httpConn.setRequestProperty("authorization",
                        "Bearer "+TOKEN);
                httpConn.setRequestProperty("charset","UTF-8");
                httpConn.setRequestProperty("Accept-Charset", "UTF-8");
                outputStream = httpConn.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                        true);
            }

            public void addFormField(String name, String value) {
                writer.append("--" + boundary).append(lineEnd);
                writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                        .append(lineEnd);
                writer.append("Content-Type: text/plain; charset=" + charset).append(
                        lineEnd);
                writer.append(lineEnd);
                writer.append(value).append(lineEnd);
                writer.flush();
            }

            public void addFilePart(String fieldName, File uploadFile)
                    throws IOException {
                String fileName = uploadFile.getName();
                writer.append("--" + boundary).append(lineEnd);
                writer.append(
                        "Content-Disposition: form-data; name=\"" + fieldName
                                + "\"; filename=\"" + fileName + "\"")
                        .append(lineEnd);
                writer.append(
                        "Content-Type: "
                                + URLConnection.guessContentTypeFromName(fileName))
                        .append(lineEnd);
                writer.append("Content-Transfer-Encoding: binary").append(lineEnd);
                writer.append(lineEnd);
                writer.flush();

                FileInputStream inputStream = new FileInputStream(uploadFile);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                writer.append(lineEnd);
                writer.flush();
            }

            String result;
            public Boolean finish() {
                writer.append(lineEnd).flush();
                writer.append("--" + boundary + "--").append(lineEnd);
                writer.close();

                BufferedReader reader;
                result = "";
                try {
                    int status = httpConn.getResponseCode();
                    if (status == HttpURLConnection.HTTP_OK ||
                            status == HttpURLConnection.HTTP_CREATED ||
                            status == HttpURLConnection.HTTP_ACCEPTED) {

                        reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
                    }
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                    reader.close();
                    httpConn.disconnect();
                    Log.d("data received",result);
                    return true;
                }catch(IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
            public String getData(){
                return result;
            }
        }

        ProgressDialog dialog = new ProgressDialog(PostActivity.this);

        @Override
        protected Boolean doInBackground(HashMap<String, String>... hashMaps) {
            HashMap<String,String> map = hashMaps[0];
            String url = map.get("url");
            String title = map.get("title");
            String body = map.get("body");
            String kakao = map.get("open_kakao_link");
            String expires = map.get("expires");
            ArrayList<String> file_path = new ArrayList<>();
            if(map.containsKey("image")){
                file_path.add(map.get("image"));
            }
            if(map.containsKey("image2")){
                file_path.add(map.get("image2"));
            }
            if(map.containsKey("image3")){
                file_path.add(map.get("image3"));
            }

            File file[] = new File[file_path.size()];
            for(int i = 0;i<file_path.size();i++){
                Uri uri = Uri.parse(file_path.get(i));
                String string = getPath(getApplicationContext(),uri);
                file[i] = new File(string);
            }

            try{
                httpHelper httphelper = new httpHelper(url,"UTF-8");
                httphelper.addFormField("url",url);
                httphelper.addFormField("title",title);
                httphelper.addFormField("body",body);
                httphelper.addFormField("open_kakao_link",kakao);
                httphelper.addFormField("expires",expires);
                for(int i = 0;i<file_path.size();i++){
                    String label = "";
                    switch(i){
                        case 0: label = "image"; break;
                        case 1: label = "image2"; break;
                        case 2: label = "image3"; break;
                    }
                    httphelper.addFilePart(label,file[i]);
                }
                boolean flag = httphelper.finish();
                publishProgress(flag);
            }catch(IOException e){
                e.printStackTrace();
                publishProgress(false);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("작성중");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
            lockPost = false;
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

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
