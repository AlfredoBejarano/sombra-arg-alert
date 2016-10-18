package com.alfredobejarano.sombraalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            percentage.setText(value + "%");
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG);
                        }
                    });
                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            parserUtils.notifyPercentageChange();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        percentage.setText(parserUtils.getPercentage()+"%");
                                    } catch (final Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG);
                                            }
                                        });
                                    }
                                }
                            });
                        } catch (final Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), String.valueOf(e), Toast.LENGTH_LONG);
                                }
                            });
                        }
                    }
                }, 0, 300000);
            }
        });

        percentageThread.start();
    }
}
