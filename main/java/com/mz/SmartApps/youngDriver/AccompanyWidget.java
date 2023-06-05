package com.mz.SmartApps.youngDriver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of App Widget functionality.
 */
public class AccompanyWidget extends AppWidgetProvider {
    private static SharedPreferences sharedPreferences;
    private static String gotDLdate, title, mainStr;
    private static int maxPB, progressPB;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static long timeEndDayA, timeEndNightA;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = remoteViewsWidget(context);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("update_widget")) {
            RemoteViews remoteViews = remoteViewsWidget(context);
            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, AccompanyWidget.class), remoteViews);
        }
    }

    private static RemoteViews remoteViewsWidget(Context context) {
        sharedPreferences = context.getSharedPreferences("file1", 0);
        gotDLdate = sharedPreferences.getString("gotDLdate", null);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.acoompany_widget);

        if (gotDLdate != null) {
            setCounters();
            views.setTextViewText(R.id.days_widget, mainStr);
            views.setTextViewText(R.id.title_widget, title);
            views.setViewVisibility(R.id.progressB_widget, View.VISIBLE);
            views.setProgressBar(R.id.progressB_widget, maxPB, progressPB, false);
        } else {
            views.setTextViewText(R.id.title_widget, "עדיין לא הוגדר");
            views.setTextViewText(R.id.days_widget, "לחץ כאן כדי להגדיר");
            views.setViewVisibility(R.id.progressB_widget, View.INVISIBLE);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("widget");
        intent.putExtra("type", 1);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //intent.addFlags(Intent.FLAG);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
        else
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        views.setOnClickPendingIntent(R.id.widgetBody, pendingIntent);
        views.setOnClickPendingIntent(R.id.show_more, pendingIntent);
        // Log.d(TAG, "remoteViewsWidget: updating");
        return views;
    }

    private static void setCounters() {
        Date gotDL = str2Date(gotDLdate);

        Date endDayAccompany = getEndAccompany(gotDL, AccompanyFragment.Accompany.day);
        Date endNightAccompany = getEndAccompany(gotDL, AccompanyFragment.Accompany.night);
        Date currentDate = Calendar.getInstance().getTime();

        timeEndDayA = endDayAccompany.getTime() - currentDate.getTime();
        long timeBetweenGot2EndDay = endDayAccompany.getTime() - gotDL.getTime();
        timeEndNightA = endNightAccompany.getTime() - currentDate.getTime();
        long timeBetweenGot2EndNight = endNightAccompany.getTime() - gotDL.getTime();

        if (timeEndDayA > 0) {//still need day accompany
            maxPB = (int) (timeBetweenGot2EndDay / (1000 * 60 * 60 * 24));
            progressPB = (int) ((timeBetweenGot2EndDay - timeEndDayA) / (1000 * 60 * 60 * 24));
            title = "מלווה יום ולילה";
            mainStr = getCounterText(timeEndDayA);
        } else {
            maxPB = (int) (timeBetweenGot2EndNight / (1000 * 60 * 60 * 24));
            progressPB = (int) ((timeBetweenGot2EndNight - timeEndNightA) / (1000 * 60 * 60 * 24));
            title = "מלווה לילה";
            mainStr = getCounterText(timeEndNightA);
        }
    }

    private static Date getEndAccompany(Date gotDriverL, AccompanyFragment.Accompany which) {
        Date date;
        int month = 3, hour = 6;
        if (which == AccompanyFragment.Accompany.night) {
            month = 6;
            hour = 0;
        }
        date = addToDate(gotDriverL, Calendar.MONTH, month);
        date = addToDate(date, Calendar.DAY_OF_MONTH, 1);
        date.setHours(hour);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    private static Date str2Date(String date) {
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    private static Date addToDate(Date date, int what, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(what, count);
        return calendar.getTime();
    }

    private static int diffranceBetween2Dates(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private static String getCounterText(long millisec) {
        if (millisec <= 0)
            return "הושלם";
        long days = TimeUnit.MILLISECONDS.toDays(millisec);
        if (days > 0) {
            if (days == 1)
                return "עוד יום אחד";
            return "עוד " + days + " ימים";
        }
        millisec -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millisec);
        if (hours == 1)
            return "עוד שעה אחת";
        else if (hours == 0)
            return "עוד פחות משעה";
        return "עוד " + hours + " שעות";
    }


}