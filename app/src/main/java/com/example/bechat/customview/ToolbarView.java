package com.example.bechat.customview;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.bechat.R;


public class ToolbarView extends Toolbar {

    private TextView nameActivity;
    private ImageView iconBack;
    private TextView nameBackActivity;
    private TypedArray attributes;
    private LinearLayout backPlace;
    private String style;

    public ToolbarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context,R.layout.toolbar_custom,this);
        nameActivity = findViewById(R.id.nameActivity);
        iconBack = findViewById(R.id.icBack);
        nameBackActivity = findViewById(R.id.nameBackActivity);
        backPlace = findViewById(R.id.backPlace);

        attributes = context.obtainStyledAttributes(attrs,R.styleable.ToolbarView);
        nameActivity.setText(attributes.getString(R.styleable.ToolbarView_text_act));
        nameBackActivity.setText(attributes.getString(R.styleable.ToolbarView_text_back));
        style = attributes.getString(R.styleable.ToolbarView_style);
        setStyle(style);
        attributes.recycle();
    }

    private void setStyle( String mStyle){
        switch (mStyle){
            //toolbar have only name activity
            case "title":{
                nameActivity.setVisibility(VISIBLE);
                iconBack.setVisibility(GONE);
                nameBackActivity.setVisibility(GONE);
                break;
            }
            //toolbar have icon back and name back activity
            case "back":{
                iconBack.setVisibility(VISIBLE);
                nameBackActivity.setVisibility(VISIBLE);
                nameActivity.setVisibility(GONE);
                break;
            }
            default:{

            }
        }
    }
    public void setOnclickIconBack(OnClickListener onclickIconBack){
        backPlace.setOnClickListener(onclickIconBack);
    }
}
