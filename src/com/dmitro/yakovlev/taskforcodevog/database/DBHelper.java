package com.dmitro.yakovlev.taskforcodevog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "DownloadPicturesDataBase", null, 1);
	}

	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table Urls ("
				+ "_id integer primary key autoincrement," + "address text,"
				+ "imageview_name text" + ");");
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}