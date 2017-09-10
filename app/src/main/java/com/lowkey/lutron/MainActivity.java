package com.lowkey.lutron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lowkey.lutron.tcp.TcpClient;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TcpClient mTcpClient;
    ArrayList<String> commands;

    ReceiveMessages myReceiver;

    Boolean[] lights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commands = new ArrayList<>();
        myReceiver = new ReceiveMessages();
        lights = new Boolean[6];
        for (int i = 0; i < 6; i++) {
            lights[i] = false;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Button living_chandelier = (Button) findViewById(R.id.toggle_living_chandelier);
        living_chandelier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[0]) {
                    runCommand("#OUTPUT,12,1,0");
                } else {
                    runCommand("#OUTPUT,12,1,100");
                }
                lights[0] = !lights[0];
            }
        });

        final Button living_lamps = (Button) findViewById(R.id.toggle_living_lamps);
        living_lamps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[1]) {
                    runCommand("#OUTPUT,13,1,0");
                } else {
                    runCommand("#OUTPUT,13,1,100");
                }
                lights[1] = !lights[1];
            }
        });

        final Button kitchen_downlights = (Button) findViewById(R.id.toggle_kitchen_downlights);
        kitchen_downlights.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[2]) {
                    runCommand("#OUTPUT,16,1,0");
                } else {
                    runCommand("#OUTPUT,16,1,100");
                }
                lights[2] = !lights[2];
            }
        });

        final Button kitchen_cabinet = (Button) findViewById(R.id.toggle_kitchen_cabinet);
        kitchen_cabinet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[3]) {
                    runCommand("#OUTPUT,14,1,0");
                } else {
                    runCommand("#OUTPUT,14,1,100");
                }
                lights[3] = !lights[3];
            }
        });

        final Button bedroom_lamps = (Button) findViewById(R.id.toggle_bedroom_lamps);
        bedroom_lamps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[4]) {
                    runCommand("#OUTPUT,15,1,0");
                } else {
                    runCommand("#OUTPUT,15,1,100");
                }
                lights[4] = !lights[4];
            }
        });

        final Button bathroom_vanity = (Button) findViewById(R.id.toggle_bathroom_vanity);
        bathroom_vanity.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(lights[5]) {
                    runCommand("#OUTPUT,17,1,0");
                } else {
                    runCommand("#OUTPUT,17,1,100");
                }
                lights[5] = !lights[5];
            }
        });

        new ConnectTask().execute();
        // Login
        runCommand("pennapps\r\nintegration");

        // Turn off lights
        runCommand("#OUTPUT,12,1,0");
        runCommand("#OUTPUT,13,1,0");
        runCommand("#OUTPUT,14,1,0");
        runCommand("#OUTPUT,15,1,0");
        runCommand("#OUTPUT,16,1,0");
        runCommand("#OUTPUT,17,1,0");
    }

    private void runCommand(String command) {
        commands.add(command + "\r\n");
        for(String commandItem : commands) {
            Log.i("command", commandItem);
        }
    }

    private void onResponse() {
        try {
            if (commands.size() != 0) {
                mTcpClient.sendMessage(commands.remove(0));
            } else {
                while (commands.size() == 0) {
                    TimeUnit.SECONDS.sleep(1);
                }
                mTcpClient.sendMessage(commands.remove(0));
            }
        }catch (InterruptedException e) {
            Log.e("TcpClient", "Error sending command");
        }
    }

    private class ConnectTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            //we create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                    onResponse();
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("response", values[0]);
            //process server response here....
        }

    }

    public class ReceiveMessages extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            runCommand(intent.getStringExtra("command"));
        }
    }
}

