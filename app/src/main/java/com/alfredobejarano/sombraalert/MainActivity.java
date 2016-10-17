package com.alfredobejarano.sombraalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private TextView percentage;
    private ParserUtils parserUtils = new ParserUtils(this);
    public static final String URL = "http://amomentincrime.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        percentage = (TextView) findViewById(R.id.percentage);

        final Thread percentageThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    final String value = String.valueOf(parserUtils.getPercentage());
                    parserUtils.notifyPercentageChange();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            percentage.setText(value + "%");
                        }
                    });

                    parserUtils.notifyPercentageChange();

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                parserUtils.notifyPercentageChange();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 0, 30000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        percentageThread.start();
    }

    public class SombraThread extends Thread {
        boolean isRunning = false;

        @Override
        public synchronized void start() {
            super.start();
            isRunning = true;
        }

        @Override
        public void run() {
            try {
                sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ParserUtils parserUtils = new ParserUtils(getApplicationContext());

            try {
                parserUtils.notifyPercentageChange();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
}
