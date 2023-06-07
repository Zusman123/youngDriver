package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;

  //AddLessonDialog is an abstract class that extends BottomSheetDialog and provides a dialog for adding lessons.
public abstract class AddLessonDialog extends BottomSheetDialog {
    private TextView dateD, startTimeD, endTimeD;
    private EditText priceD, countGroupET;
   private CheckBox cbPaidD;
   private RadioGroup rgType;
    private Button okD;
    private LinearLayout regLL, groupLL;
    private boolean isGroup = false;
    private Animation textAnim = null;
    private int price, time;
    private TextView regLsnI, groupLsnI, priceETtag;
    private String startTime, endTime;
    private LessonsDB lessonsDB;
    private SharedPreferences sharedPreferences;
    private double[] types = new double[]{1,1.5,2,0};
    
  //Constructs an instance of AddLessonDialog
    public AddLessonDialog(@NonNull final Context context) {
        super(context);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(R.layout.add_lesson_dialog);

        // Initialize views
        dateD = findViewById(R.id.dateD);
        startTimeD = findViewById(R.id.startTimeD);
        endTimeD = findViewById(R.id.endTimeD);
        priceD =findViewById(R.id.priceD);
        cbPaidD = findViewById(R.id.cbPaidD);
        rgType = findViewById(R.id.rgType);
        okD = findViewById(R.id.okD);
        regLsnI = findViewById(R.id.regLsnI);
        groupLsnI = findViewById(R.id.groupLsnI);
        regLL = findViewById(R.id.regLL);
        groupLL = findViewById(R.id.groupLL);
        priceETtag = findViewById(R.id.priceETtag);
        countGroupET = findViewById(R.id.countGroupET);

         // Initialize shared preferences and lessons database
        sharedPreferences = context.getSharedPreferences("file1",0);
        lessonsDB = new LessonsDB(context);

         // Load price and time values from shared preferences
        price = sharedPreferences.getInt("price",0);
        time = sharedPreferences.getInt("time",0);

        ProperInput properInput = new ProperInput();
        TextWatcher proper2 = properInput.cheakProperInput(new EditText[]{countGroupET,priceD},okD);
        TextWatcher proper1 = properInput.cheakProperInput(priceD,okD);
        
        // Initialize click listener for navigation buttons
        View.OnClickListener navClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reg clicked
                if (isGroup){
                    regLsnI.setTypeface(null, Typeface.BOLD);
                    regLsnI.setBackgroundResource(R.color.colorAccent);
                    groupLsnI.setTypeface(null,Typeface.NORMAL);
                    groupLsnI.setBackgroundResource(R.color.colorWeakAccent);
                    regLL.setVisibility(View.VISIBLE);
                    groupLL.setVisibility(View.GONE);
                    priceETtag.setText("מחיר השיעור");
                    priceD.removeTextChangedListener(proper2);
                    priceD.addTextChangedListener(proper1);
                    priceD.setText(price+"");
                    isGroup = false;
                }
                //group clicked
                else {
                    regLsnI.setTypeface(null, Typeface.NORMAL);
                    regLsnI.setBackgroundResource(R.color.colorWeakAccent);
                    groupLsnI.setTypeface(null,Typeface.BOLD);
                    groupLsnI.setBackgroundResource(R.color.colorAccent);
                    regLL.setVisibility(View.GONE);
                    groupLL.setVisibility(View.VISIBLE);
                    priceETtag.setText("סה\"כ (₪)");
                    priceD.setText("");
                    countGroupET.setText("");
                    countGroupET.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (count > 0){
                                int totalP = (int)(Double.parseDouble(countGroupET.getText().toString())*price);
                                priceD.setText(totalP+"");
                            }
                        }@Override public void afterTextChanged(Editable s) { }
                    });
                    countGroupET.addTextChangedListener(proper2);
                    priceD.removeTextChangedListener(proper1);
                    priceD.addTextChangedListener(proper2);

                    isGroup = true;
                }
            }
        };
        regLsnI.setOnClickListener(navClick);
        groupLsnI.setOnClickListener(navClick);

         // Set initial values and listeners
        rgType.check(R.id.lType1);
        setStartAndEndTime();
        dateD.setText(getCurrentDate());
        startTimeD.setText(startTime);
        endTimeD.setText(endTime);
        priceD.setText(String.valueOf(price));

        textAnim = AnimationUtils.loadAnimation(context, R.anim.text_animation);

        priceD.addTextChangedListener(proper1);
        dateD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c =Calendar.getInstance();
                c.setTime(str2date(dateD.getText().toString()));
                DatePickerDialog datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formatedDate = sdf.format(calendar.getTime());
                        dateD.setText(formatedDate);
                    }
                },c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        startTimeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeD.setText(String.format("%02d:%02d", hourOfDay, minute));
                        endTimeD.setText(getEndTimeByStartTime());
                    }
                },getHourFromString(startTimeD.getText().toString()),getMinuteFromString(startTimeD.getText().toString()),true);
                timePickerDialog.show();
            }
        });
        endTimeD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String endT = String.format("%02d:%02d", hourOfDay, minute);
                        if (isTrueStartEndTime(endT))
                            endTimeD.setText(endT);
                        else
                            Toast.makeText(context,"שגיאה! שעת הסיום קטנה או שווה לשעת ההתחלה",Toast.LENGTH_LONG).show();
                    }
                },getHourFromString(endTimeD.getText().toString()),getMinuteFromString(endTimeD.getText().toString()),true);
                timePickerDialog.show();
            }
        });


        okD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lesson lesson = null;
                if (!isGroup){
                    lesson = new Lesson(0,str2date(dateD.getText().toString()),
                            str2time(startTimeD.getText().toString()),
                            str2time(endTimeD.getText().toString()),
                            Integer.parseInt(priceD.getText().toString()),
                            cbPaidD.isChecked(), getType(),false);
                    lessonsDB.addLesson(lesson);
                }
                else {
                    lesson = new Lesson(0,str2date(getCurrentDate()),Integer.parseInt(priceD.getText().toString()),
                            cbPaidD.isChecked(), Double.parseDouble(countGroupET.getText().toString()),true);
                    lessonsDB.addLessonsGroup(lesson);
                }

                dismiss();
                onLessonAdded();
            }
        });
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setStartAndEndTime();
                startTimeD.setText(startTime);
                endTimeD.setText(endTime);
                if (getType()!=0)
                    priceD.setText(String.valueOf((int)(price*getType())));
                else
                    priceD.setText(String.valueOf(price));
            }
        });
      endTimeD.addTextChangedListener(textChangedWithAnimation(endTimeD));
      startTimeD.addTextChangedListener(textChangedWithAnimation(startTimeD));
      dateD.addTextChangedListener(textChangedWithAnimation(dateD));
    }
    public abstract void onLessonAdded();

    //Creates a TextWatcher with animation to apply on a TextView after text has been changed.
    private TextWatcher textChangedWithAnimation(final TextView tv){
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (textAnim!=null)
                    tv.startAnimation(textAnim);

            }
        };
        return textWatcher;
    }

//Retrieves the selected type value from the RadioGroup.
    private double getType(){
        View radioButton = rgType.findViewById(rgType.getCheckedRadioButtonId());
        int type = rgType.indexOfChild(radioButton);
        return types[type];
    }
    
    //Checks if the provided end time is after the start time.
    private boolean isTrueStartEndTime(String endTime){
        Calendar startTcal = getCalendarByTime(startTimeD.getText().toString());
        Calendar endTcal = getCalendarByTime(endTime);
        Log.d(TAG, "strat compare to end: "+startTcal.compareTo(endTcal));
        if (startTcal.compareTo(endTcal) == -1)
          return true;
        return false;
    }
    //Retrieves the current date in the format "dd/MM/yyyy".
    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(calendar.getTime());
    }
    
//Retrieves the hour from a time string in the format "HH:mm".
    public int getHourFromString(String time){
       return getCalendarByTime(time).get(HOUR_OF_DAY);
    }
    
    //Retrieves the minute from a time string in the format "HH:mm".
    public int getMinuteFromString(String time){
       return getCalendarByTime(time).get(MINUTE);
    }
    
    //Retrieves a Calendar instance based on a time string in the format "HH:mm".
    private Calendar getCalendarByTime(String time){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(time);
        } catch (ParseException e) { e.printStackTrace(); }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }
    
    //Calculates the end time based on the start time and the lesson type.
    public String getEndTimeByStartTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
       Calendar cal = getCalendarByTime(startTimeD.getText().toString()); //calendar of start lesson
       if (getType()!= 0)
         cal.add(Calendar.MINUTE, (int) (time*getType())); //הוסף לזמן ההתחלה
       else
           cal.add(MINUTE,15); //טסט פנימי 15 דקות
        return timeFormat.format(cal.getTime());
    }
    
    //Sets the start time and end time based on the current time and the lesson type.
    public void setStartAndEndTime(){
        Calendar cTime = Calendar.getInstance(TimeZone.getDefault());
        int defTime = (int) (time*getType());
        int hours = (int) defTime/60;
        if (defTime%60 != 0){
            hours = hours+1;
        }
        int hourOfDay = cTime.get(HOUR_OF_DAY);
        endTime = String.format("%02d:%02d", hourOfDay, 0);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(endTime);
        } catch (ParseException e) { e.printStackTrace(); }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        if (getType() != 0)
            cal.add(Calendar.MINUTE, -defTime);
        else
            cal.add(MINUTE,-time);
        startTime = df.format(cal.getTime());

    }
//Converts a date string in the format "dd/MM/yyyy" to a Date object
    public Date str2date(String date){
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d=dateFormat.parse(date);
        } catch (ParseException e) { e.printStackTrace(); }
        return d;
    }
    
    //Converts a time string in the format "HH:mm" to a Date object.
    public Date str2time(String date){
        SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d=dateFormat.parse(date);
        } catch (ParseException e) { e.printStackTrace(); }
        return d;
    }

}
