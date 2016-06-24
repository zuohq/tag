package com.martin.tag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/***
 * @Description: 标签列表
 * @author: Created by Martin on 15-9-1
 */
public class TagView extends ViewGroup {

    public static final int MAXLENGHT = 15;
    public static final String HINTCOLOR = "#119ce6";
    public static final String COLORS[] = {"#ff85ad", "#71c5ff", "#ffb073", "#9a8cff"};

    public static final int TYPE_DISTINCT_ROW = 1;
    public static final int TYPE_DISTINCT_ITEM = 2;
    private int colorType;

    private TextPaint mPaint;


    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        obtainStyledAttributes(attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        obtainStyledAttributes(attrs);
    }

    private void initPaint() {
        mPaint = new TextPaint();
        mPaint.setColor(Color.parseColor(HINTCOLOR));
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(getResources().getDimension(R.dimen.font_14));
    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.TagView);

        colorType = attributes.getInt(R.styleable.TagView_color_type, TYPE_DISTINCT_ROW);

        attributes.recycle();
    }

    /***
     * 测量子View 的宽高
     *
     * @param child
     * @param parentWidthMeasureSpec
     * @param parentHeightMeasureSpec
     */
    private MarginLayoutParams measureChildView(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, lp.leftMargin + lp.bottomMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        return lp;
    }

    /***
     * 标签的背景
     *
     * @return
     */
    public ShapeDrawable getTagDrawer(String color) {
        final int corner = getContext().getResources().getDimensionPixelSize(R.dimen.tag_rectf_corners);
        float[] outerRadii = new float[]{corner, corner, corner, corner, corner, corner, corner, corner};
        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);

        Paint paint = shapeDrawable.getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(color));
        return shapeDrawable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = getDefaultSize(0, widthMeasureSpec);
        int heightSize = 0;

        int count = getChildCount();

        final int verticalPadding = getPaddingTop() + getPaddingBottom();
        final int horizontalPadding = getPaddingLeft() + getPaddingRight();

        // 每行已测量子View 的宽度和
        int mTotalLength = 0;

        for (int index = 0; index < count; index++) {

            View child = getChildView(index);
            MarginLayoutParams lp = measureChildView(child, widthMeasureSpec, heightMeasureSpec);

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            //需要换行
            if (mTotalLength + childWidth > widthSize) {
                //换行之后,下一行已使用的宽度更改为初始值,高度累加
                mTotalLength = horizontalPadding + childWidth;
                heightSize += childHeight;

            } else {
                //同一行高度不变,宽度累加
                heightSize = Math.max(heightSize, childHeight);
                mTotalLength += childWidth;
            }
        }

        heightSize += verticalPadding;

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        int left = getPaddingLeft();
        int top = getPaddingTop(), right = 0, bottom = 0;

        int mTotalLength = 0;
        // 行号
        int row = 0;

        for (int index = 0; index < count; index++) {

            View child = getChildAt(index);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            //子View 的宽/高
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            left += lp.leftMargin;

            right = left + childWidth;
            bottom = top + childHeight;

            child.layout(left, top, right, bottom);

            ShapeDrawable drawable = null;
            switch (colorType) {
                case TYPE_DISTINCT_ROW:
                    drawable = getTagDrawer(COLORS[row % COLORS.length]);
                    break;
                case TYPE_DISTINCT_ITEM:
                    drawable = getTagDrawer(COLORS[index % COLORS.length]);
                    break;
                default:
                    drawable = getTagDrawer(COLORS[row % COLORS.length]);
                    break;
            }
            //设置每一行子View的颜色
            child.setBackgroundDrawable(drawable);

            left += childWidth;

            if (index + 1 < count) {
                View nextChild = nextChild(index + 1);
                mTotalLength = left + nextChild.getMeasuredWidth();
            }

            //验证是否需要换行
            if (mTotalLength + lp.leftMargin + getPaddingRight() > getWidth()) {
                mTotalLength = 0;
                left = getPaddingLeft();
                top += childHeight + lp.topMargin;
                row++;
            }
        }
    }

    /***
     * 下一个子View
     *
     * @param nextIndex
     * @return
     */
    public View nextChild(int nextIndex) {
        return getChildAt(nextIndex);
    }

    public View getChildView(int index) {
        return getChildAt(index);
    }

    public View getChildView(String label) {
        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_tag, this, false);
        if (!TextUtils.isEmpty(label)) {
            if (label.length() > MAXLENGHT) {//最大15个
                label = label.substring(0, MAXLENGHT);
            }
        }
        view.setText(label);
        return view;
    }

    public void setData(List<String> labels) {
        if (labels == null)
            return;
        removeAllViews();

        for (String label : labels) {
            addView(getChildView(label));
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}