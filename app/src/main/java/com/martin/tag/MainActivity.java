package com.martin.tag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String[] LABELS = {"90后", "小鲜肉", "文艺青年", "逗比", "美拍", "颜值担当","学霸","运动","青春","萌","hahah"};

    private TagView mTagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTagView = (TagView) findViewById(R.id.tagview);
        mTagView.setData(Arrays.asList(LABELS));
    }
}
