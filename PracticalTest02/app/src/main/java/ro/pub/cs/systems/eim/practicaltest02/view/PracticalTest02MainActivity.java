package ro.pub.cs.systems.eim.practicaltest02.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientGetThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientPutThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {
    public EditText serverPortEditText;
    public EditText addressEditText;
    public EditText clientPortEditText;
    public EditText putKeyEditText;
    public EditText putValueEditText;
    public EditText getKeyEditText;

    public Button startServerButton;
    public Button putDataButton;
    public Button getDataButton;

    public TextView dataTextView;

    public ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.serverPortEditText);
        addressEditText = findViewById(R.id.addressEditText);
        clientPortEditText = findViewById(R.id.clientPortEditText);
        putKeyEditText = findViewById(R.id.putKeyEditText);
        putValueEditText = findViewById(R.id.putValueEditText);
        getKeyEditText = findViewById(R.id.getKeyEditText);

        startServerButton = findViewById(R.id.startServerButton);
        putDataButton = findViewById(R.id.putDataButton);
        getDataButton = findViewById(R.id.getDataButton);

        dataTextView = findViewById(R.id.dataTextView);

        startServerButton.setOnClickListener(v -> {
            int serverPort = Integer.parseInt(serverPortEditText.getText().toString());

            serverThread = new ServerThread(serverPort);
            serverThread.start();
        });

        putDataButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString();
            int clientPort = Integer.parseInt(clientPortEditText.getText().toString());
            String key = putKeyEditText.getText().toString();
            String value = putValueEditText.getText().toString();

            new ClientPutThread(address, clientPort, key, value).start();
        });

        getDataButton.setOnClickListener(v -> {
            String address = addressEditText.getText().toString();
            int clientPort = Integer.parseInt(clientPortEditText.getText().toString());
            String key = getKeyEditText.getText().toString();

            new ClientGetThread(address, clientPort, key, dataTextView).start();
        });
    }
}