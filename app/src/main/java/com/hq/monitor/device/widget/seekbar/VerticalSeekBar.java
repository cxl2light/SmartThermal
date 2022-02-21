package com.hq.monitor.device.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

//https://blog.csdn.net/mulanlong/article/details/53229608
public class VerticalSeekBar extends AppCompatSeekBar {

	private boolean horizontal = true;

	public VerticalSeekBar(Context context) {
		super(context);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d("ZeSize","horizontal=" + horizontal);
		if (horizontal){
			super.onSizeChanged(w, h, oldw, oldh);
		} else {
			super.onSizeChanged(h, w, oldh, oldw);
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
		Log.d("ZeSize","horizontal=" + horizontal);
		if (horizontal){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		} else {
			super.onMeasure(heightMeasureSpec, widthMeasureSpec);
			setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
		}
	}
 
	protected void onDraw(Canvas c) {
		if (!horizontal){
			// 将SeekBar转转90度
			c.rotate(-90);
			// 将旋转后的视图移动回来
			c.translate(-getHeight(), 0);
			Log.i("getHeight()", getHeight() + "");
		}
		super.onDraw(c);
	}
 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!horizontal){
			if (!isEnabled()) {
				return false;
			}

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					Log.i("ACTION_UP", "getProgress=" + getProgress() + ",getMax=" + getMax() + ",getY=" + event.getY() + ",getHeight=" + getHeight() + ",getWidth=" + getWidth());
					int i = 0;
					// 获取滑动的距离
					i = getMax() - (int) (getMax() * event.getY() / getHeight());
					// 设置进度
					setProgress(i);
					Log.i("Progress", getProgress() + "");
					notifyProgress();
					Log.i("getWidth()", getWidth() + "");
					Log.i("getHeight()", getHeight() + "");
					break;

				case MotionEvent.ACTION_CANCEL:
					break;
			}
			return true;
		} else {
			return super.onTouchEvent(event);
		}

	}

	void notifyProgress() {
		// 每次拖动SeekBar都会调用
		onSizeChanged(getWidth(), getHeight(), 0, 0);
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public boolean getHorizontal() {
		return horizontal;
	}
}