package com.mz.SmartApps.youngDriver;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;

/**
 * A fragment for displaying and editing user profile information.
 */
public class ProfileFragment extends Fragment {

    private TextView nameSubT, nameProfile, dateProfile, timeProfile, priceProfile;
    private LinearLayout nameLl, birthdayLl, timeLl, priceLl;
    private SharedPreferences sharedPreferences;
    private Dialog changeDialog;
    private TextView title;

    private EditText editText;
    private Button save;
    private String[] titles = new String[]{"שנה שם", "שנה תאריך לידה", " שנה אורך שיעור", "שנה עלות שיעור"};
    private String[] keys = new String[]{"name", "date", "time", "price"};
    private int[] maxLength = new int[]{50, 0, 4, 6};
    private String[] data;
    private TextView[] profileTexts;
    private int[] inputType = new int[]{TYPE_CLASS_TEXT, TYPE_CLASS_TEXT, TYPE_CLASS_NUMBER, TYPE_CLASS_NUMBER};

    public ProfileFragment() {
        // Required empty public constructor
    }

   
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nameSubT = view.findViewById(R.id.nameSubT);
        nameProfile = view.findViewById(R.id.nameProfile);
        dateProfile = view.findViewById(R.id.dateProfile);
        timeProfile = view.findViewById(R.id.timeProfile);
        priceProfile = view.findViewById(R.id.priceProfile);

        nameLl = view.findViewById(R.id.nameLl);
        birthdayLl = view.findViewById(R.id.birthdayLl);
        timeLl = view.findViewById(R.id.timeLl);
        priceLl = view.findViewById(R.id.priceLl);

        changeDialog = new Dialog(getActivity());
        changeDialog.setContentView(R.layout.change_profile_data);
        title = changeDialog.findViewById(R.id.titleCP);
        editText = changeDialog.findViewById(R.id.editPD);
        save = changeDialog.findViewById(R.id.savePD);

        nameLl.setOnClickListener(clickLl(0));
        birthdayLl.setOnClickListener(clickLl(1));
        timeLl.setOnClickListener(clickLl(2));
        priceLl.setOnClickListener(clickLl(3));

        sharedPreferences = getActivity().getSharedPreferences("file1", 0);

        int time = sharedPreferences.getInt("time", 0);
        int price = sharedPreferences.getInt("price", 0);
        data = new String[]{sharedPreferences.getString("name", null), sharedPreferences.getString("date", null), String.valueOf(time), String.valueOf(price)};

        profileTexts = new TextView[]{nameProfile, dateProfile, timeProfile, priceProfile};

        nameSubT.setText(data[0]);
        nameProfile.setText(data[0]);
        dateProfile.setText(data[1]);
        timeProfile.setText(data[2]);
        priceProfile.setText(data[3]);

        return view;
    }

    /**
     * Handles the click event for saving profile data.
     *
     * @param index The index of the profile data field being edited
     * @param text  The new value entered for the profile data field
     */
    public void saveBtnClick(int index, String text) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (index < 2)
            editor.putString(keys[index], text);
        else
            editor.putInt(keys[index], Integer.parseInt(text));
        editor.apply();
        profileTexts[index].setText(text);
        data[index] = text;
        if (index == 0)
            nameSubT.setText(text);
    }

    /**
     * Creates a click listener for the profile data fields.
     *
     * @param index The index of the profile data field being edited
     * @return The OnClickListener for the profile data field
     */
    public View.OnClickListener clickLl(final int index) {
        View.OnClickListener v = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setInputType(inputType[index]);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength[index])});
                title.setText(titles[index]);
                editText.setText(data[index]);
                editText.addTextChangedListener(new ProperInput().cheakProperInput(editText, save));
                if (index == 1) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(str2date(data[index]));
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String formattedDate = sdf.format(calendar.getTime());
                            saveBtnClick(index, formattedDate);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                } else
                    changeDialog.show();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveBtnClick(index, editText.getText().toString());
                        changeDialog.dismiss();
                    }
                });
            }
        };
        return v;
    }

    /**
     * Converts a string representation of a date to a Date object.
     *
     * @param date The date string to convert
     * @return The corresponding Date object
     */
    public Date str2date(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
