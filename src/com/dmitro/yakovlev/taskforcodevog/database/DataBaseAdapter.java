package com.dmitro.yakovlev.taskforcodevog.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_IMAGE_NAME = "imageview_name";

	private static final String DATABASE_TABLE = "Urls";

	final String LOG_TAG = "myLogs";

	private DBHelper dbHelper;
	private Context context;
	private SQLiteDatabase database;

	public DataBaseAdapter(Context context) {
		this.context = context;
	}

	public DataBaseAdapter open() throws SQLException {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public boolean CheckRepeatingOfUrls(String urlNew) {

		Cursor cursor = fetchAllUrls();
		while (cursor.moveToNext()) {
			String currentUrl = cursor.getString(cursor.getColumnIndex(DataBaseAdapter.KEY_ADDRESS));
			if (currentUrl.equals(urlNew))
				return true;
		}

		return false;
	}

	public static String GetUrlAddress(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(DataBaseAdapter.KEY_ADDRESS));
	}

	public static Integer GetUrlID(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(DataBaseAdapter.KEY_ROWID));
	}

	public static String GetImageNameForUrl(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(DataBaseAdapter.KEY_IMAGE_NAME));
	}

	public void insertUrlWithCheckingOfRepeating(String url_address, String image_name) {
		if (!CheckRepeatingOfUrls(url_address))
			insertUrl(url_address, image_name);

	}

	public long insertUrl(String url_address, String image_name) {
		ContentValues initialValues = createContentValues(url_address, image_name);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteUrlById(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public void clearTable() {
		database.delete(DATABASE_TABLE, null, null);
	}

	private ContentValues createContentValues(String url_address, String image_name) {
		ContentValues values = new ContentValues();

		values.put(KEY_ADDRESS, url_address);
		values.put(KEY_IMAGE_NAME, image_name);

		return values;
	}

	public boolean updateFileNameByRowId(long rowId, String address, String file_name) {
		ContentValues updateValues = createContentValues(address, file_name);
		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor GetCursorByUrlAddress(String address) {
		String selection = KEY_ADDRESS + " = ?";
		String[] selectionArgs = new String[] { address };
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_ADDRESS, KEY_IMAGE_NAME }, selection, selectionArgs, null,
				null, null);
	}

	public Cursor fetchAllUrls() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_ADDRESS, KEY_IMAGE_NAME }, null, null, null, null, null);
	}

	public Cursor fetchUrlById(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_ADDRESS, KEY_IMAGE_NAME }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void close() {
		dbHelper.close();
	}
}