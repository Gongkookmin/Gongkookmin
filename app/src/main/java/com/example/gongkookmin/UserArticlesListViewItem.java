/* 작성자 : 이재욱
*  작성 시간 : 2019년 11월 12일 16시 55분 */
package com.example.gongkookmin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

/* 아이템에 출력될 데이터를 위한 클래스 정의 */
public class UserArticlesListViewItem {
    private Drawable iconDrawable;
    private String titleStr;
    private String authorStr;
    private Date articleDate;

    /* Setter Methods */
    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }
    public void setTitle(String title) {
        titleStr = title;
    }
    public void setAuthor(String author) {
        authorStr = author;
    }
    public void setArticleDate(Date date){ articleDate = date;}

    /* Getter Methods */
    public Drawable getIcon() {
        return this.iconDrawable;
    }
    public String getTitle() {
        return this.titleStr;
    }
    public String getAuthor() {
        return this.authorStr;
    }
    public Date getArticleDate(){return this.articleDate;}
}
