package googleSheetsApi;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class execute {

	private static String SHEET_URL = "https://sheets.googleapis.com/v4/spreadsheets/11BCnspCt2Mut3nhc4WMY6CYTd0zF9C3eCzsk1AEpKLM";
	private static String RANGE = "/values/A:Z";
	private static String API_KEY = "AIzaSyDwxpicDSa3GBcLJmgE1yxdtjYpIJFogcA";

	private static String readAll(Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = reader.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(br);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) {
		try {
			String title = (String)readJsonFromUrl(SHEET_URL + "?key=" + API_KEY + "&fields=properties.title").getJSONObject("properties").get("title");
			JSONObject json = readJsonFromUrl(SHEET_URL +RANGE +"?key=" + API_KEY);
			JSONArray jsonArray = json.getJSONArray("values");
			System.out.println(title);
			for(Object obj : jsonArray) {
				System.out.println(obj.toString());
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
