package com.dmitro.yakovlev.taksforcodevog;

import java.io.File;

import com.dmitro.yakovlev.taskforcodevog.database.DataBaseAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ImageActivity extends Activity {

	public ImageView imageView;
	private GestureDetector gestureDetector;
	private DataBaseAdapter dbHelper;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view_layout);

		imageView = (ImageView) findViewById(R.id.imageView1);

		Intent intent = getIntent();
		String urlAddressOfImageFromImageListActivity = intent.getStringExtra(DataBaseAdapter.KEY_ADDRESS);

		LoadImageFromSdCardByUrlAddress(urlAddressOfImageFromImageListActivity);
		gestureDetector = new GestureDetector(new GestureListener(urlAddressOfImageFromImageListActivity, this, this));

		imageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				gestureDetector.onTouchEvent(event);
				return true;
			}
		});
	}

	private  void LoadImageFromSdCardByUrlAddress(String urlAddressOfImageFromFirstActivity) {
		dbHelper = new DataBaseAdapter(this);
		dbHelper.open();
		Cursor cursor = dbHelper.GetCursorByUrlAddress(urlAddressOfImageFromFirstActivity);

		SetPictureInImageViewByFileNameOnSdCard(GetImageNameNameFromCursor(cursor), imageView);

		dbHelper.close();
	}

	private  String GetImageNameNameFromCursor(Cursor cursor) {
		String imageName = null;
		while (cursor.moveToNext()) {
			imageName = DataBaseAdapter.GetImageNameForUrl(cursor);
		}
		return imageName;
	}

	public String OnSwipeEventLoadImageFromSdCardByUrlAddress(String urlAddressOfCurrentImage, Context context, DirectionSwipe currentDirection,
			ImageView imageView) {
		
		dbHelper = new DataBaseAdapter(context);
		dbHelper.open();
		Cursor cursor = dbHelper.fetchAllUrls();

		while (cursor.moveToNext()) {
			String urlAddressOnThisIteration = DataBaseAdapter.GetUrlAddress(cursor);

			if (urlAddressOnThisIteration.equals(urlAddressOfCurrentImage)) {

				if ((currentDirection == DirectionSwipe.LEFT_TO_RIGHT) & (!cursor.isLast()))
					cursor.moveToNext();
				if ((currentDirection == DirectionSwipe.RIGHT_TO_LEFT) & (!cursor.isFirst()))
					cursor.moveToPrevious();

				String nameOfNewImageForImageView = DataBaseAdapter.GetImageNameForUrl(cursor);
				String urlAddressForNewImage = DataBaseAdapter.GetUrlAddress(cursor);

				SetPictureInImageViewByFileNameOnSdCard(nameOfNewImageForImageView, imageView);
				
				return urlAddressForNewImage;
			}
		}
		return null;
	}

	private  void SetPictureInImageViewByFileNameOnSdCard(String file_name, ImageView img) {
		String directory = Environment.getExternalStorageDirectory().getPath() + "/" + DownloadImageFromURL.FOLDER_NAME + "/" + file_name;
		File file = new File(directory);
		Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
		img.setImageBitmap(bmp);
	}

}
