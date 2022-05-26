package ro.pub.cs.systems.eim.practicaltest02.network;

import static ro.pub.cs.systems.eim.practicaltest02.general.Constants.LOG_TAG;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.model.Information;

public class ServerThread extends Thread {
    private final int port;
    private ServerSocket serverSocket;

    HashMap<String, Information> data;

    public ServerThread(int port) {
        this.port = port;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not listen on port: " + port);
            e.printStackTrace();
        }

        data = new HashMap<>();
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "Server started on port: " + port);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                Log.d(LOG_TAG, "Accepted connection from: " + socket.getInetAddress());

                new CommunicationThread(this, socket).start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Accept failed");
                e.printStackTrace();
            }
        }
    }

    public void close() {
        Log.d(LOG_TAG, "Closing server socket");
        interrupt();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not close the connect socket");
                e.printStackTrace();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public HashMap<String, Information> getData() {
        return data;
    }
}
