package com.dmitro.yakovlev.taksforcodevog;

import android.content.Context;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class GestureListener extends SimpleOnGestureListener {
	private final int SWIPE_MIN_DISTANCE = 120;
	private final int SWIPE_THRESHOLD_VELOCITY = 200;

	private String currentUrlAddress;
	private Context context;
	private ImageActivity imgActivity;

	public GestureListener(String url_address, Context context, ImageActivity imAct) {
		this.currentUrlAddress = url_address;
		this.context = context;
		this.imgActivity = imAct; 
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		if (IsMotionFromRightToLeft(e1, e2, velocityX)) {
			this.currentUrlAddress = imgActivity.OnSwipeEventLoadImageFromSdCardByUrlAddress(currentUrlAddress, context,
					DirectionSwipe.LEFT_TO_RIGHT, imgActivity.imageView);
			return false;
		} else if (IsMotionFromLeftToRight(e1, e2, velocityX)) {
			this.currentUrlAddress = imgActivity.OnSwipeEventLoadImageFromSdCardByUrlAddress(currentUrlAddress, context,
					DirectionSwipe.RIGHT_TO_LEFT, imgActivity.imageView);
			return false;
		}
		return false;
	}

	private boolean IsMotionFromRightToLeft(MotionEvent e1, MotionEvent e2, float velocityX) {
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			return true;
		return false;
	}

	private boolean IsMotionFromLeftToRight(MotionEvent e1, MotionEvent e2, float velocityX) {
		if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
			return true;
		return false;
	}
}