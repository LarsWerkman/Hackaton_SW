package org.dutchaug.hackathon.soakingwet.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.dutchaug.hackathon.soakingwet.R;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

public class WeatherDataCollector {

	private static final String EXTREME_RAIN = "extreme rain";
	private static final String HEAVY_RAIN = "heavy rain";
	private static final String RAIN = "rain";
	private static final String LIGHT_RAIN = "light rain";
	private static final String DRY = "dry";
	private OkHttpClient client = new OkHttpClient();

	public Map<String, Integer> getPrecipitationForLatLong(double latitude, double longtitude) throws IOException {
		// between 0 and 255

		String buienRadarUrl = String.format("http://gps.buienradar.nl/getrr.php?lat=%.2f&lon=%.2f", latitude, longtitude);
		Log.i("org.dutchaug.hackathon", buienRadarUrl);
		String pipeSeperatedFile = get(new URL(buienRadarUrl));
		String[] preciptitationOnLocation = pipeSeperatedFile.split("\r\n");
		Map<String, Integer> preciptitationOnLocationMap = new HashMap<String, Integer>();
		for (String preciptitation : preciptitationOnLocation) {
			String[] temp = preciptitation.split("\\|");
			NumberFormat nf = NumberFormat.getInstance(Locale.US);
			int precipitation;
			try {
				precipitation = nf.parse(temp[0]).intValue();
				preciptitationOnLocationMap.put(temp[1], precipitation);
			} catch (ParseException e) {
				Log.w("org.dutchaug.hackathon",
						String.format("%s could not be parsed", temp[1]));
			}
		}
		return preciptitationOnLocationMap;
	}

	private Map<String, String> getRainTypeMap(Map<String, Integer> preciptitationOnLocationMap) {
		Map<String, String> drySmallMediumHeavyExtremeRain = new HashMap<String, String>();
		for (String time : preciptitationOnLocationMap.keySet()) {
			Integer preciptitation = preciptitationOnLocationMap.get(time);
			if (preciptitation < 20) {
				drySmallMediumHeavyExtremeRain.put(DRY, time);
			} else if (20 <= preciptitation && preciptitation <= 77) {
				drySmallMediumHeavyExtremeRain.put(LIGHT_RAIN, time);
			} else if (77 >= preciptitation && preciptitation <= 154) {
				drySmallMediumHeavyExtremeRain.put(RAIN, time);
			} else if (154 >= preciptitation && preciptitation <= 231) {
				drySmallMediumHeavyExtremeRain.put(HEAVY_RAIN, time);
			} else if (preciptitation >= 231) {
				drySmallMediumHeavyExtremeRain.put(EXTREME_RAIN, time);
			}
		}
		return drySmallMediumHeavyExtremeRain;
	}
	
	public String getUpcommingWeather(Map<String, Integer> precipitationForLatLong) {
		Map<String, String> rainTypesComming = getRainTypeMap(precipitationForLatLong);
		String commingUpNext = DRY;
		if(rainTypesComming.containsKey(EXTREME_RAIN)) {
			commingUpNext = String.format("%s at %s", EXTREME_RAIN, rainTypesComming.get(EXTREME_RAIN));
		} else if(rainTypesComming.containsKey(HEAVY_RAIN)) {
			commingUpNext = String.format("%s at %s", HEAVY_RAIN, rainTypesComming.get(HEAVY_RAIN));
		} else if(rainTypesComming.containsKey(RAIN)) {
			commingUpNext = String.format("%s at %s", RAIN, rainTypesComming.get(RAIN));
		} else if(rainTypesComming.containsKey(LIGHT_RAIN)) {
			commingUpNext = String.format("%s at %s", LIGHT_RAIN, rainTypesComming.get(LIGHT_RAIN));
		} else if(rainTypesComming.containsKey(DRY)) {
			commingUpNext = String.format("%s till %s", DRY, rainTypesComming.get(DRY));
		}
		return commingUpNext;
	}
	
	public int getIconForUpcomingWeather(Map<String, Integer> precipitationForLatLong) {
		Map<String, String> rainTypesComming = getRainTypeMap(precipitationForLatLong);
		int iconId = R.drawable.ic_dry;
		if(rainTypesComming.containsKey(EXTREME_RAIN)) {
			iconId = R.drawable.ic_extreme_rain;
		} else if(rainTypesComming.containsKey(HEAVY_RAIN)) {
			iconId = R.drawable.ic_heavy_rain;
		} else if(rainTypesComming.containsKey(RAIN)) {
			iconId = R.drawable.ic_rain;
		} else if(rainTypesComming.containsKey(LIGHT_RAIN)) {
			iconId = R.drawable.ic_light_rain;
		} else if(rainTypesComming.containsKey(DRY)) {
			iconId = R.drawable.ic_dry;
		}
		return iconId;
	}

	private String get(URL url) throws IOException {
		HttpURLConnection connection = client.open(url);
		InputStream in = null;
		try {
			// Read the response.
			in = connection.getInputStream();
			byte[] response = readFully(in);
			return new String(response, "UTF-8");
		} finally {
			if (in != null)
				in.close();
		}
	}

	private byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int count; (count = in.read(buffer)) != -1;) {
			out.write(buffer, 0, count);
		}
		return out.toByteArray();
	}

}
