package ro.pub.cs.systems.eim.practicaltest02.network;

import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.LOG_TAG;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.PUT;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientPutThread extends Thread {
    private String address;
    private int port;
    private String key;
    private String value;

    private Socket socket;

    public ClientPutThread(String address, int port, String key, String value) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.value = value;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.d(LOG_TAG, "Sending key: " + key);

            printWriter.println(PUT + "," + key + "," + value);
            printWriter.flush();

            /*String weatherInformation;

            while ((weatherInformation = bufferedReader.readLine()) != null) {
                String finalWeatherInformation = weatherInformation;
                textView.post(() -> textView.setText(finalWeatherInformation));
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
