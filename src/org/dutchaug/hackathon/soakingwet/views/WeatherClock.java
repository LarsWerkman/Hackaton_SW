package org.dutchaug.hackathon.soakingwet.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class WeatherClock extends View {

	public WeatherClock(Context context) {
		super(context);
		init(null, 0);
	}

	public WeatherClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public WeatherClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}
	
	public void init(AttributeSet attrs, int defStyle){
		
	}

}
