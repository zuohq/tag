#tag

![iamge](https://github.com/zuohq/tag/blob/master/app/tag.png)

java code
    
    mTagView = (TagView) findViewById(R.id.tagview);
            //add List<String>
            mTagView.setData(Arrays.asList(LABELS));
            
            
layout xml
    
    <com.martin.tag.TagView
            android:id="@+id/tagview"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="20dp" />