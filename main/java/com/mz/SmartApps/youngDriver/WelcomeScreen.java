package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.mz.SmartApps.youngDriver.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WelcomeScreen extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Button continueBtn, dateChooser;
    private EditText nameET;

   private NumberPicker lessonPrice, lessonTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        sharedPreferences = getSharedPreferences("file1",0);

        continueBtn = findViewById(R.id.continueBtn);
        dateChooser = findViewById(R.id.dateChooser);
        nameET = findViewById(R.id.nameET);
        continueBtn.setEnabled(false);
        //continueBtn.setClickable(false);

        lessonPrice= findViewById(R.id.lessonPrice);
        lessonTime =findViewById(R.id.lessonTime);

        lessonPrice.setMinValue(0);
        lessonPrice.setMaxValue(500);
        lessonPrice.setValue(150);
        lessonTime.setMinValue(0);
        lessonTime.setMaxValue(100);
        lessonTime.setValue(40);

        dateChooser.addTextChangedListener(cheakTrueData());
        nameET.addTextChangedListener(cheakTrueData() );

        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c =Calendar.getInstance();
                DatePickerDialog datePickerDialog=new DatePickerDialog(WelcomeScreen.this,  AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String formatedDate = sdf.format(calendar.getTime());
                        dateChooser.setText(formatedDate);
                    }
                },c.get(Calendar.YEAR)-16, 0, 1);
                datePickerDialog.show();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("name",nameET.getText().toString());

                editor.putString("date",dateChooser.getText().toString());
                editor.putInt("price", lessonPrice.getValue());
                editor.putInt("time",lessonTime.getValue());
                editor.putInt("new",1);
                editor.commit();
                Intent intent = new Intent(WelcomeScreen.this, MainActivity.class);
                intent.putExtra("type",3);
                startActivity(intent);

            }
        });

    }

    //Creates a TextWatcher to check for true data input in the name and date fields and enable/disable a button accordingly.
    private TextWatcher cheakTrueData(){
        TextWatcher textWatcher  = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: "+nameET.getText().toString());
                if (!nameET.getText().toString().equals("") && !dateChooser.getText().toString().equals("בחר תאריך")) {
                    continueBtn.setEnabled(true);
                    continueBtn.setBackgroundResource(R.color.colorPrimary);
                } else {
                    continueBtn.setEnabled(false);
                    continueBtn.setBackgroundResource(R.color.gray);
                }
            }
            @Override public void afterTextChanged(Editable editable) { }
        };
        return textWatcher;
    }

    //intent to the phone's home screen
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
