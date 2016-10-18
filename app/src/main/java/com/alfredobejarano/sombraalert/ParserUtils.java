package com.alfredobejarano.sombraalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by jacorona on 10/17/16.
 */
public class ParserUtils extends AsyncTask<Void, String, String>{

    private Context context;
    private static String key = "sombra-percentage";
    public ParserUtils(Context context) { this.context = context; }

    /**
     * Calls "amomentincrime.com" website to retrieve the current percentage.
     */
    public Double getPercentage() throws IOException, ParserConfigurationException {
        String body = String.valueOf(doInBackground());
        Pattern pattern = Pattern.compile("\\d{2}.\\d{4}");
        Matcher matcher = pattern.matcher(body);
        return matcher.find() ? Double.valueOf(matcher.group(0)) : 0;
    }

    /**
     * Stores the value of the percentage for future uses.
     * @return boolean - If the value was successfully stored or not.
     * @throws IOException - If the value can't be retrieved.
     * @throws ParserConfigurationException - If the value can't be retrieved.
     */
    private boolean storeValue() throws IOException, ParserConfigurationException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);

        String value = String.valueOf(getPercentage());
        String previous = sharedPreferences.getString(key, String.valueOf(value));

        if(previous.equals(value)) {
            return false;
        } else {
            sharedPreferences.edit().putString(key, String.valueOf(value)).commit();
            return true;
        }
    }

    /**
     * Sends a notification to the user, alerting for percentage changes.
     */
    public void notifyPercentageChange() throws IOException, ParserConfigurationException {
        if(storeValue()) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("¡Ha aumentado el porcentaje!")
                    .setContentText("El porcentaje esta ahora a " + getPercentage()+"%");

            Intent intent = new Intent(context, context.getClass());

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(context.getClass());
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            builder.setLights(context.getResources().getColor(R.color.colorAccent), 3000, 3000);

            Notification notification = builder.build();
            notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.shadows);

            notificationManager.notify(23, notification);

        }
    }

    @Override
    protected String doInBackground(Void... params) {
        URL url = null;
        try {
            url = new URL(MainActivity.URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line.trim());
            }
            return  builder.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException logOrIgnore) {}
        }
    }
}
