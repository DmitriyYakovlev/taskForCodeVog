package com.dmitro.yakovlev.taksforcodevog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dmitro.yakovlev.taskforcodevog.database.DataBaseAdapter;

public class ImageListActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	private boolean IsAllDownload;
	private DataBaseAdapter dbHelper;
	private Cursor cursor;
	private ListView listViewForUrl;
	private SimpleCursorAdapter scAdapter;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_list);

		dbHelper = new DataBaseAdapter(this);
		dbHelper.open();

		// заповнюємо базу даних url адресами із ресурсів. Потрібно, якщо
		// додаток запускається вперше і база даних пуста.
		AddDataFromArray(dbHelper);

		GetDataFromDBWriteInLayout();

		handler = new Handler() {
			int countOfPictures = 20;
			int currentcount;

			@Override
			public void handleMessage(Message msg) {
				SetImageForListViewItem(msg.what);

				currentcount++;
				if (currentcount == countOfPictures) {
					Toast toast = Toast.makeText(ImageListActivity.this, "Усі зображення заванажено", Toast.LENGTH_LONG);
					toast.show();
					ReDrawStatusImagesInListView(countOfPictures);
					IsAllDownload = true;
				}
			}
		};

		DownloadPictures();

		listViewForUrl.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (IsAllDownload)
					ReDrawStatusImagesInListView(view.getCount());
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		listViewForUrl.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				View wantedView = listViewForUrl.getChildAt(position);
				// якщо item в ListView проініціалізований, перевірити чи
				// завантажилось зображення.
				if (wantedView != null) {
					ProgressBar progress = (ProgressBar) wantedView.findViewById(R.id.progress);

					if (progress.getVisibility() == View.VISIBLE) {
						Toast toast = Toast.makeText(ImageListActivity.this, "Дане зображення ще не завантажено", Toast.LENGTH_LONG);
						toast.show();
						return;
					} else
						OpenImageActivity(view);
				}

				// якщо item в ListView не проініціалізований, але всі
				// зображення завантажені, можна дозволити переход.
				if (wantedView == null & IsAllDownload)
					OpenImageActivity(view);
			}
		});
	}

	private void OpenImageActivity(View view) {
		String url_address_str = (String) ((TextView) view.findViewById(R.id.tvText)).getText();
		Intent intent = new Intent(ImageListActivity.this, ImageActivity.class);
		intent.putExtra(DataBaseAdapter.KEY_ADDRESS, url_address_str);
		startActivity(intent);
	}

	private void ReDrawStatusImagesInListView(int count) {
		for (int i = 0; i < count; i++) {
			SetImageForListViewItem(i);
		}
	}

	private void SetImageForListViewItem(int position) {
		View wantedView = listViewForUrl.getChildAt(position);

		if (wantedView != null) {
			ImageView im = (ImageView) wantedView.findViewById(R.id.image_test);
			im.setImageResource(R.drawable.button_ok_6622);
			ProgressBar progress = (ProgressBar) wantedView.findViewById(R.id.progress);
			progress.setVisibility(View.INVISIBLE);
		}
	}

	private void AddDataFromArray(DataBaseAdapter dbHelper) {
		String[] urlsArrayFromResources = getResources().getStringArray(R.array.urls_array);
		for (int i = 0; i < urlsArrayFromResources.length; i++) {
			dbHelper.insertUrlWithCheckingOfRepeating(urlsArrayFromResources[i], "image" + (i + 1));
		}

	}

	private void DownloadPictures() {
		ExecutorService service = Executors.newFixedThreadPool(2);
		cursor = dbHelper.fetchAllUrls();

		while (cursor.moveToNext()) {

			Integer currentPosition = cursor.getPosition();
			String nameForNewFile = getResources().getString(R.string.file_name) + Integer.toString(currentPosition + 1);

			String urlAddressStr = DataBaseAdapter.GetUrlAddress(cursor);
			service.execute(new DownloadImageFromURL(urlAddressStr, nameForNewFile, currentPosition, handler));

			Integer urlId = DataBaseAdapter.GetUrlID(cursor);
			SetNameOfDownloadedFileIdDB(urlId, urlAddressStr, nameForNewFile);
		}
	}



	private  void SetNameOfDownloadedFileIdDB(long rowId, String address, String file_name) {
		dbHelper.updateFileNameByRowId(rowId, address, file_name);
	}

	private  void GetDataFromDBWriteInLayout() {
		String[] from = new String[] { DataBaseAdapter.KEY_ADDRESS, DataBaseAdapter.KEY_IMAGE_NAME };
		int[] to = { R.id.tvText, R.id.ivImg };

		scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
		scAdapter.setViewBinder(new ViewBinderForImageListView());

		listViewForUrl = (ListView) findViewById(R.id.lvSimple);
		listViewForUrl.setAdapter(scAdapter);

		getSupportLoaderManager().initLoader(0, null, this);
	}

	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new CursorLoaderDB(this, dbHelper);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	static class CursorLoaderDB extends CursorLoader {

		DataBaseAdapter db;

		public CursorLoaderDB(Context context, DataBaseAdapter db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {
			Cursor cursor = db.fetchAllUrls();
			return cursor;
		}
	}

}
