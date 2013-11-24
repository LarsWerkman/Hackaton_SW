package org.dutchaug.hackathon.soakingwet.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.squareup.okhttp.OkHttpClient;

public class WeatherDataCollector {

	private OkHttpClient client = new OkHttpClient();

	public int getPrecipitationForLatLong(double latitude, double longtitude) throws IOException {
		// between 0 and 255

		String buienRadarUrl = String.format("http://gps.buienradar.nl/getrr.php?lat=%1$,.2f&lon=%1$,.2f", latitude, longtitude);
		String pipeSeperatedFile = get(new URL(buienRadarUrl));

		System.out.println(pipeSeperatedFile);
		return 0;
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
