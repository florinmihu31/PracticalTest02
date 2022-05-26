package ro.pub.cs.systems.eim.practicaltest02.network;

import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.GET;
import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.LOG_TAG;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientGetThread extends Thread {
    private String address;
    private int port;
    private String key;
    private TextView textView;

    private Socket socket;

    public ClientGetThread(String address, int port, String key, TextView textView) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.textView = textView;
    }

    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.d(LOG_TAG, "Sending key: " + key);

            printWriter.println(GET + "," + key + "\n");
            printWriter.flush();

            String information;

            while ((information = bufferedReader.readLine()) != null) {
                String finalInformation = information;
                textView.post(() -> textView.setText(finalInformation));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
