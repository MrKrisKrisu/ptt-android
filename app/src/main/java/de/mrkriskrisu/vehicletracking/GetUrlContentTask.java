package de.mrkriskrisu.vehicletracking;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUrlContentTask extends AsyncTask<String, Integer, String> {

    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
    }
}