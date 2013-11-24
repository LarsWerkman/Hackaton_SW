package org.dutchaug.hackathon.soakingwet.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseDownloadAsyncTask<D> extends AsyncTaskLoader<D> {

	private Activity mActivity;
	private ConnectionCallback mConnectionCallback;
	private String mUrl;

	public BaseDownloadAsyncTask(Activity activity,
			ConnectionCallback connectionCalback, String url) {
		super(activity);
		mActivity = activity;
		mConnectionCallback = connectionCalback;
		mUrl = url;
	}

	public String download() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(//Server availeble testConnection.SERVER
					"").openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			if (200 <= responseCode && responseCode <= 399) {
				
				InputStream is = null;
				
				try {
					URL url = new URL(mUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(10000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					is = conn.getInputStream();
					String contentAsString = convertStreamToString(is);
					return contentAsString;

				} catch (IOException exception) {
					onDownloadFail();
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}
		} catch (IOException exception) {
			onConnectionFail();
		}

		return null;
	}

	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	private void onConnectionFail() {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mConnectionCallback.onConnectionFail(ConnectionCallback.DATA);
			}
		});
	}

	private void onDownloadFail() {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mConnectionCallback.onDownloadFail(ConnectionCallback.DATA);
			}
		});
	}
	
	public Activity getActivity(){
		return mActivity;
	}
	
	public interface ConnectionCallback {
		
		public final int DATA = 1;
		
		public void onConnectionFail(int code);
		public void onDownloadFail(int code);
		public ConnectionCallback getConnectionCallback();
	}

}
