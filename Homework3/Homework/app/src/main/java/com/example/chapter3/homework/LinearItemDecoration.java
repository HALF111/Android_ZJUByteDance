package com.example.chapter3.homework;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LinearItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public LinearItemDecoration(int color) {
        // 直接绘制颜色  只是用来测试
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        // 获取需要绘制的区域
        Rect rect = new Rect();
        rect.left = parent.getPaddingLeft();
        rect.right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            rect.top = childView.getBottom();
            rect.bottom = rect.top + 20;
            // 直接利用Canvas去绘制一个矩形 在留出来的地方
            c.drawRect(rect, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 每个item留出20px高度
        outRect.bottom += 20;
    }

}