package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AccompanyFragment extends Fragment {
    private Button gotDLdateBtn;
    private LinearLayout accBoard, dayAccLL, nightAccLL;
    private Button shareDayAcc, shareNightAcc;
    private ProgressBar dayPB, nightPB;
    private TextView dayTV, nightTv, finishDateDN, finishDateN, nightCountdown, dayCountdown;
    private long timeEndDayA , timeEndNightA;
    private SharedPreferences sharedPreferences;
    private Date gotDL, endDayAccompany, endNightAccompany, currentDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat hhmmss = new SimpleDateFormat("HH:mm:ss");
    private boolean isRunning = false;
    private String name;

    public AccompanyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acoompany, container, false);
        gotDLdateBtn = view.findViewById(R.id.dateDLgot);
        accBoard = view.findViewById(R.id.accBoard);
        dayPB = view.findViewById(R.id.dayPB);
        nightPB = view.findViewById(R.id.nightPB);
        dayTV = view.findViewById(R.id.dayTV);
        nightTv = view.findViewById(R.id.nightTV);
        finishDateDN = view.findViewById(R.id.finishDateDN);
        finishDateN = view.findViewById(R.id.finishDateN);
        dayCountdown = view.findViewById(R.id.dayCountdownTV);
        nightCountdown = view.findViewById(R.id.nightCountdownTV);
        shareDayAcc = view.findViewById(R.id.share_day_acc);
        shareNightAcc = view.findViewById(R.id.share_night_acc);
        dayAccLL = view.findViewById(R.id.day_acc_ll);
        nightAccLL = view.findViewById(R.id.night_acc_ll);
        sharedPreferences = getActivity().getSharedPreferences("file1", 0);
        String gotDLdate = sharedPreferences.getString("gotDLdate", null);
        name = sharedPreferences.getString("name",null);


          //setNotification(Calendar.getInstance().getTimeInMillis()+1000*10,"מזל טוב "+name +"! סיימת את המלווה יום");

        if (gotDLdate != null) {
            accBoard.setVisibility(View.VISIBLE);
            gotDLdateBtn.setText(gotDLdate);
            setCounters();

        }

        gotDLdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);

                        String formatedDate = dateFormat.format(calendar.getTime());
                        gotDLdateBtn.setText(formatedDate);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("gotDLdate", formatedDate);
                        editor.commit();
                        cancelAllAlarms();
                        setAlarms(calendar.getTime());
                        accBoard.setVisibility(View.VISIBLE);

                        setCounters();
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        shareDayAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureAndShareLinearLayout(Accompany.day);
            }
        });
        shareNightAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureAndShareLinearLayout(Accompany.night);
            }
        });
        return view;
    }

    private void setAlarms(Date gotDriverL) {
        Date endDayA = getEndAccompany(gotDriverL, Accompany.day);
        Date endNightA = getEndAccompany(gotDriverL, Accompany.night);

        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Log.d(TAG, s.format(endDayA) + " night: " + s.format(endNightA));

        setNotification(endDayA.getTime(), "מזל טוב "+name +"! סיימת את המלווה יום");
        setNotification(endNightA.getTime(), "מזל טוב "+name +"! סיימת את המלווה לילה");
        setNotification(addToDate(endDayA,Calendar.DAY_OF_MONTH,-7).getTime(),"הי "+name+", נשאר לך שבוע אחרון למלווה יום.");
        setNotification(addToDate(endNightA,Calendar.DAY_OF_MONTH,-7).getTime(),"הי "+name+", נשאר לך שבוע אחרון למלווה לילה.");

    }

    private void setNotification(long triggerTime, String notificationText) {
        if (triggerTime > Calendar.getInstance().getTimeInMillis()){
            Intent notificationIntent = new Intent(getContext(), NotificationReceiver.class);
            notificationIntent.setAction("com.mz.SmartApps.youngDriver.SHOW_NOTIFICATION");
            notificationIntent.putExtra("text", notificationText);

            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                pendingIntent = PendingIntent.getBroadcast(getContext(), 2005, notificationIntent, PendingIntent.FLAG_MUTABLE);
            else
                pendingIntent = PendingIntent.getBroadcast(getContext(), 2005, notificationIntent, 0);

            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
        else
            Log.d(TAG, "setNotification: is happen");

    }

    public void cancelAllAlarms() {
        Intent alarmIntent = new Intent(getContext(), NotificationReceiver.class);
        alarmIntent.setAction("com.mz.SmartApps.youngDriver.SHOW_NOTIFICATION");
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            pendingIntent = PendingIntent.getBroadcast(getContext(), 2005, alarmIntent, PendingIntent.FLAG_MUTABLE);
        else
            pendingIntent = PendingIntent.getBroadcast(getContext(), 2005, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        pendingIntent.cancel();
    }


    enum Accompany {day, night}

    ;

    public Date getEndAccompany(Date gotDriverL, Accompany which) {
        Date date;
        int month = 3, hour = 6; //default for day accompany
        if (which == Accompany.night) {
            month = 6;
            hour = 0;
        }
        date = addToDate(gotDriverL, Calendar.MONTH, month); //add 3/6 month
        date = addToDate(date, Calendar.DAY_OF_MONTH, 1); //add 1 day
        date.setHours(hour); //set hour (hour = 6/0)
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    private void setCounters() {
        gotDL = str2Date(gotDLdateBtn.getText().toString());
        nightCountdown.setVisibility(View.VISIBLE);
        dayCountdown.setVisibility(View.VISIBLE);
        endDayAccompany = getEndAccompany(gotDL, Accompany.day);
        endNightAccompany = getEndAccompany(gotDL, Accompany.night);
        currentDate = Calendar.getInstance().getTime();

        finishDateDN.setText("תאריך סיום: " + dateFormat.format(endDayAccompany));
        finishDateN.setText("תאריך סיום: " + dateFormat.format(endNightAccompany));

        timeEndDayA = endDayAccompany.getTime() - currentDate.getTime();
        long timeBetweenGot2EndDay = endDayAccompany.getTime() - gotDL.getTime();
        timeEndNightA = endNightAccompany.getTime() - currentDate.getTime();
        long timeBetweenGot2EndNight = endNightAccompany.getTime() - gotDL.getTime();

        if (timeEndDayA <= 0){
            dayTV.setText("הושלם");
            dayCountdown.setVisibility(View.GONE);
        }
        if (timeEndNightA <=0){
            nightTv.setText("הושלם");
            nightCountdown.setVisibility(View.GONE);
        }

        dayPB.setMax((int) (timeBetweenGot2EndDay / (1000 * 60 * 60 * 24)));//in hours
        dayPB.setProgress((int) ((timeBetweenGot2EndDay - timeEndDayA) / (1000 * 60 * 60 * 24)));

        nightPB.setMax((int) (timeBetweenGot2EndNight / (1000 * 60 * 60 * 24)));
        nightPB.setProgress((int) ((timeBetweenGot2EndNight - timeEndNightA) / (1000 * 60 * 60 * 24)));

       // Log.d(TAG, "max: "+((int) (timeBetweenGot2EndNight) / (1000 * 60 * 60 * 24))+
         //       "progress: "+((int) ((timeBetweenGot2EndNight - timeEndNightA) / (1000 * 60 * 60 * 24))));
        updateWidget();

        if (timeEndNightA > 0 && !isRunning){
            CountDownTimer cdt = new CountDownTimer(timeEndNightA, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    isRunning = true;
                    if (timeEndDayA > 0){
                        timeEndDayA -=1000;
                        dayCountdown.setText(timeUntilFinish(timeEndDayA,Accompany.day));
                    }
                    else{
                        dayTV.setText("הושלם");
                        dayCountdown.setVisibility(View.GONE);
                    }
                    if (timeEndNightA >0) {
                        timeEndNightA -= 1000;
                        nightCountdown.setText(timeUntilFinish(timeEndNightA, Accompany.night));
                    }
                    else{
                        nightTv.setText("הושלם");
                        nightCountdown.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFinish() {
                    isRunning = false;
                    setCounters();
                }
            }.start();
        }

    }

    public String timeUntilFinish(long millisec, Accompany which) {
        Date timeUm = new Date();
        long days = TimeUnit.MILLISECONDS.toDays(millisec); //how much days
        if (which == Accompany.day)
            dayTV.setText(getCounterText((int) days));
        else
            nightTv.setText(getCounterText((int) days));
        millisec -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(millisec); //how much hours
        millisec -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisec); //how much minutes
        millisec -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisec); //how much seconds

        timeUm.setHours((int) (hours));
        timeUm.setMinutes((int) (minutes));
        timeUm.setSeconds((int) (seconds));
        return hhmmss.format(timeUm);
    }

    private void updateWidget() {
        Intent updateWidget = new Intent(getContext(), AccompanyWidget.class); // Widget.class is your widget class
        updateWidget.setAction("update_widget");
        PendingIntent pending = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
            pending = PendingIntent.getBroadcast(getContext(), 0, updateWidget, PendingIntent.FLAG_MUTABLE);
        else
            pending = PendingIntent.getBroadcast(getContext(), 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }



    private Date addToDate(Date date, int what, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(what, count);
        return calendar.getTime();
    }

    private String getCounterText(int days) {
        String def = "עוד " + days + " ימים";
        if (days == 1)
            return "עוד יום אחד";
        return def;
    }

    private Date str2Date(String date) {
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    private void captureAndShareLinearLayout( Accompany which) {
        LinearLayout linearLayout = null;
        if (which == Accompany.day){
            linearLayout = dayAccLL;
            shareDayAcc.setVisibility(View.INVISIBLE);
        }
        else {
            linearLayout = nightAccLL;
            shareNightAcc.setVisibility(View.INVISIBLE);
        }

        linearLayout.measure(
                View.MeasureSpec.makeMeasureSpec(linearLayout.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(linearLayout.getHeight(), View.MeasureSpec.EXACTLY));
        linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());

        Bitmap accompanyBit = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(accompanyBit);

        linearLayout.draw(canvas);

        Drawable drawable = getContext().getResources().getDrawable( R.drawable.bottom_share);
        int heightBottom = (int) (accompanyBit.getWidth()/3.37913);
        Bitmap belowBitmap = Bitmap.createBitmap(accompanyBit.getWidth(), heightBottom, Bitmap.Config.ARGB_8888);
        Canvas drawableCanvas = new Canvas(belowBitmap);
        drawable.setBounds(0, 0, accompanyBit.getWidth(), heightBottom);
        drawable.draw(drawableCanvas);

        Bitmap title = createTextBitmapWithBackground(accompanyBit.getWidth());
        int combinedWidth = accompanyBit.getWidth();
        int combinedHeight = accompanyBit.getHeight() + belowBitmap.getHeight()+title.getHeight()+50;
        Bitmap combinedBitmap = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);
        Canvas combinedCanvas = new Canvas(combinedBitmap);
        combinedCanvas.drawColor(Color.WHITE);
        combinedCanvas.drawBitmap(title,0,0,null);
        combinedCanvas.drawBitmap(accompanyBit, 0, title.getHeight(), null);
        combinedCanvas.drawBitmap(belowBitmap, 0, accompanyBit.getHeight()+title.getHeight()+50, null);

        shareDayAcc.setVisibility(View.VISIBLE);
        shareNightAcc.setVisibility(View.VISIBLE);
        shareImage(getContext(),combinedBitmap);

    }
    public void shareImage(Context context, Bitmap combinedBitmap) {
        // Create a file to store the combinedBitmap temporarily
        File tempFile = new File(context.getCacheDir(), "combined_image.jpg");

        try {
            // Create an output stream to write the combinedBitmap to the tempFile
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            String shareText = getContext().getString(R.string.shareText);
            // Create a FileProvider URI for the tempFile
            Uri tempFileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);

            // Create an intent to share the image using the tempFileUri
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, tempFileUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the share activity with the shareIntent
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap createTextBitmapWithBackground(int width) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80f);
        String text = "מד המלווה של "+name;

        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        int bgHeight = textRect.height() + 70;
        // Create a Bitmap for the text background with calculated height and the combinedBitmap's width
        Bitmap textBgBitmap = Bitmap.createBitmap(width, bgHeight, Bitmap.Config.ARGB_8888);
        // Create a Canvas object with the textBgBitmap as its target
        Canvas textBgCanvas = new Canvas(textBgBitmap);
        // Draw a background rectangle with specified color onto the textBgCanvas
        textBgCanvas.drawColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        // Draw the text onto the textBgCanvas at the center of the background
        float x = (textBgBitmap.getWidth() - textRect.width()) / 2f;
        float y = (textBgBitmap.getHeight() + textRect.height()) / 2f;
        textBgCanvas.drawText(text, x, y, textPaint);
        return textBgBitmap;
    }



}
