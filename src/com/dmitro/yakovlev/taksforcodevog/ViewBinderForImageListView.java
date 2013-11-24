package com.dmitro.yakovlev.taksforcodevog;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmitro.yakovlev.taskforcodevog.database.DataBaseAdapter;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

class ViewBinderForImageListView implements SimpleCursorAdapter.ViewBinder {

	private String urlValue;

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		switch (view.getId()) {

		case R.id.tvText:
			this.urlValue = DataBaseAdapter.GetUrlAddress(cursor);
			((TextView) view).setText(urlValue);
			return true;
		case R.id.ivImg:
			UrlImageViewHelper.setUrlDrawable((ImageView) view, urlValue, null, 6000);
			return true;
		}
		return false;

	}

}