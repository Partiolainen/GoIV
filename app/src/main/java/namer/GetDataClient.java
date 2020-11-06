package namer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetDataClient extends AsyncTask<String, Integer, String> {
    protected String doInBackground(String... urls) {
        String content = null;

        try {
            URL url = new URL(urls[0]);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            content = "";
            String line;
            while ((line = rd.readLine()) != null) {
            content += line + "\n";
        } } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        // this is executed on the main thread after the process is over
        // update your UI here
        //displayMessage(result);
        super.onPostExecute(result);
    }
}

