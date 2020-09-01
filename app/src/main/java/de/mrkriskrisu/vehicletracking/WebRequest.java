package de.mrkriskrisu.vehicletracking;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebRequest extends AsyncTask<String, Void, String> {

    private URL url;
    private String body;

    public WebRequest(URL url, String body) {
        this.url = url;
        this.body = body;
    }

    @Override
    public String doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        String result = "";

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = urlConnection.getResponseCode();

            System.out.println("HTTP Code erhalten: " + code);


            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                return response.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }
        return result;

    }
}