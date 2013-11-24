package com.dmitro.yakovlev.taksforcodevog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class DownloadImageFromURL implements Runnable {

	private String urlForDownload;
	private String nameOfNewImage;
	private Handler myHandlerFromUI;
	private Integer positionInListView;
	
	public static final String FOLDER_NAME = "codevog";

	private static final String TAG = "DownloadManager";

	public DownloadImageFromURL(String url, String name, int positionInListView, Handler handler) {

		this.urlForDownload = url;
		this.nameOfNewImage = name;
		this.myHandlerFromUI = handler;
		this.positionInListView = positionInListView;
	}

	@Override
	public void run() {
		DownloadFromUrl(urlForDownload, nameOfNewImage);
	}

	private void DownloadFromUrl(String downloadUrl, String fileName) {

		try {
			IsEnableSDCard();
			File file = CreateFileAndSetPath(fileName);
			ByteArrayBuffer baf = UseURLConnection(downloadUrl);
			EndWorkWithFileOutputStream(file, baf);

			myHandlerFromUI.sendEmptyMessage(positionInListView);

		} catch (IOException e) {
			Log.d(TAG, "Error: " + e);
		}
	}

	private void IsEnableSDCard() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
			return;
		}
	}

	private File CreateFileAndSetPath(String fileName) {
		File sdPath = Environment.getExternalStorageDirectory();
		sdPath = new File(sdPath.getAbsolutePath() + "/" + FOLDER_NAME);

		if (sdPath.exists() == false) {
			sdPath.mkdirs();
		}
		return new File(sdPath, fileName);
	}

	private ByteArrayBuffer UseURLConnection(String DownloadUrl) throws IOException {
		URL url = new URL(DownloadUrl);
		URLConnection ucon = url.openConnection();
		InputStream inputStream = ucon.getInputStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		ByteArrayBuffer byteAB = new ByteArrayBuffer(5000);
		int current = 0;
		while ((current = bufferedInputStream.read()) != -1) {
			byteAB.append((byte) current);
		}

		return byteAB;
	}

	private void EndWorkWithFileOutputStream(File file, ByteArrayBuffer byteAB) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(byteAB.toByteArray());
		fos.flush();
		fos.close();
	}

}
