package ro.pub.cs.systems.eim.practicaltest02.network;

import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.BASE_URL;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.GET;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.LOG_TAG;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.NONE;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.PUT;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.UNIX_TIME;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.Information;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "CommunicationThread started");

        if (socket == null) {
            Log.e(LOG_TAG, "Error: socket is null");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            String request = bufferedReader.readLine();
            Log.d(LOG_TAG, "Message received: " + request);

            String[] infos = request.split(",");

            String type = infos[0];
            String key = null;
            String value = null;

            if (type.equals(GET)) {
                key = infos[1];
            } else if (type.equals(PUT)) {
                key = infos[1];
                value = infos[2];
            }

            Log.d(LOG_TAG, "Type: " + type + "; Key: " + key + "; Value: " + value);

            if (key == null || key.isEmpty()) {
                Log.e(LOG_TAG, "Error: city or informationData is null or empty");
                return;
            }

            HashMap<String, Information> data = serverThread.getData();
            Information information = null;
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";

            if (type.equals(GET)) {
                if (data.containsKey(key)) {
                    Log.d(LOG_TAG, "Found key in cache: " + key);
                    information = data.get(key);

                    long timestamp = information.getUnixTime();
                    HttpGet httpGet = new HttpGet(BASE_URL);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    if (httpEntity != null) {
                        pageSourceCode = EntityUtils.toString(httpEntity);
                    }

                    if (pageSourceCode == null || pageSourceCode.isEmpty()) {
                        Log.e(LOG_TAG, "Error: pageSourceCode is null or empty");
                        return;
                    }

                    Log.d(LOG_TAG, "pageSourceCode: " + pageSourceCode);
                    JSONObject jsonObject = new JSONObject(pageSourceCode);
                    long currentTimestamp = jsonObject.getLong(UNIX_TIME);

                    if (currentTimestamp - timestamp > 60) {
                        serverThread.data.remove(key);
                        Log.d(LOG_TAG, "Removed key from cache: " + key);
                        information = null;
                    } else {
                        printWriter.println(information.getValue());
                    }
                } else {
                    Log.d(LOG_TAG, "Did not find key in cache: " + key);
                    printWriter.println(NONE);
                    printWriter.flush();
                }
            } else {
                HttpGet httpGet = new HttpGet(BASE_URL);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpEntity);
                }

                if (pageSourceCode == null || pageSourceCode.isEmpty()) {
                    Log.e(LOG_TAG, "Error: pageSourceCode is null or empty");
                    return;
                }

                Log.d(LOG_TAG, "pageSourceCode: " + pageSourceCode);
                JSONObject jsonObject = new JSONObject(pageSourceCode);
                long currentTimestamp = jsonObject.getLong(UNIX_TIME);

                serverThread.data.put(key, new Information(value, currentTimestamp));
                Log.d(LOG_TAG, "Added key to cache: " + key);
            }

            /*Log.d(LOG_TAG, "Weather info for city " + city + " not found in cache");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            String uri = BASE_URL + "?" + QUERY + "=" + city + "&" + APP_ID + "=" + API_KEY + "&" + UNITS + "=" + METRIC;
            Log.d(LOG_TAG, "uri: " + uri);

            HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                pageSourceCode = EntityUtils.toString(httpEntity);
            }

            if (pageSourceCode == null || pageSourceCode.isEmpty()) {
                Log.e(LOG_TAG, "Error: pageSourceCode is null or empty");
                return;
            }

            Log.d(LOG_TAG, "pageSourceCode: " + pageSourceCode);

                JSONObject jsonObject = new JSONObject(pageSourceCode);
                double temperature = jsonObject.getJSONObject(MAIN).getDouble(TEMP);
                double windSpeed = jsonObject.getJSONObject(WIND).getDouble(SPEED);
                String description = jsonObject.getJSONArray(WEATHER).getJSONObject(0).getString(DESCRIPTION);
                double pressure = jsonObject.getJSONObject(MAIN).getDouble(PRESSURE);
                double humidity = jsonObject.getJSONObject(MAIN).getDouble(HUMIDITY);

                weatherInfoForCity = new WeatherInfo(temperature, windSpeed, description, pressure, humidity);
                serverThread.setCacheData(city, weatherInfoForCity);

                Log.d(LOG_TAG, "Weather info for city " + city + " added to cache");*/
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
