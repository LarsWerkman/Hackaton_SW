package org.dutchaug.hackathon.soakingwet.utils;

import java.io.IOException;

import org.dutchaug.hackathon.soakingwet.services.WeatherDataCollector;

import android.app.Activity;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseDownloadAsyncTask<D> extends AsyncTaskLoader<D> {

	private Activity mActivity;
	private WeatherDataCollector weatherDataCollector;

	public BaseDownloadAsyncTask(Activity activity) {
		super(activity);
		weatherDataCollector = new WeatherDataCollector();
	}
	
	public String getData(double latitude, double longtitude) throws IOException {
		return String.format("%d", weatherDataCollector.getPrecipitationForLatLong(latitude, longtitude));
	}

	
	public Activity getActivity(){
		return mActivity;
	}
	

}
