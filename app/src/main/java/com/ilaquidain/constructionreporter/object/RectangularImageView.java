package com.ilaquidain.constructionreporter.object;

import android.content.Context;
import android.util.AttributeSet;

public class RectangularImageView extends android.support.v7.widget.AppCompatImageView {
    public RectangularImageView(Context context) {
        super(context);
    }

    public RectangularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredWidth()*4/9;
        setMeasuredDimension(width, height); //Snap to width
    }
}
