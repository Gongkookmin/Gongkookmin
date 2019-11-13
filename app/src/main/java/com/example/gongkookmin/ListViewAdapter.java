/* 작성자 : 이재욱
* 작성 시간 : 2019년 11월 12일 16시 55분 */
package com.example.gongkookmin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;/* user_articles_listview.xml 과 UserArticlesListViewItem class 사이의 연결을 위한 Adapter */
import java.util.Date;

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가될 데이터를 저장할 변수
    private ArrayList<UserArticlesListViewItem> listViewItems = new ArrayList<UserArticlesListViewItem>();

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // position에 위치한 데이터를 추력하기 위해 사용될 View 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.user_articles_listview, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.articleIcon);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.authorTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);


        UserArticlesListViewItem listViewItem = listViewItems.get(position);

        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        authorTextView.setText(listViewItem.getAuthor());
        dateTextView.setText(listViewItem.getArticleDate().getHours() + ":" + listViewItem.getArticleDate().getMinutes());

        return convertView;
    }

    public void addItem(Drawable icon, String title, String author, Date date) {
        UserArticlesListViewItem item = new UserArticlesListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setAuthor(author);
        item.setArticleDate(date);

        listViewItems.add(item);
    }
}
