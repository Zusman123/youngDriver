package com.mz.SmartApps.youngDriver;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ProperInput {
    public ProperInput() {

    }

    public TextWatcher cheakProperInput(EditText[] editTexts, Button button) {
        TextWatcher textWatcher = new TextWatcher() {
            boolean proper = true;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                proper = true;
                for (EditText editText : editTexts) {
                    if (editText.length() == 0) {
                        proper = false;
                        break;
                    }
                }
                if (proper) {
                    button.setEnabled(true);
                    button.setBackgroundResource(R.color.colorPrimary);
                } else {
                    button.setEnabled(false);
                    button.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        return textWatcher;
    }

    public TextWatcher cheakProperInput(EditText editText, Button button) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                if (editText.length() == 0) {
                    button.setEnabled(false);
                    button.setBackgroundResource(R.color.gray);
                } else {
                    button.setEnabled(true);
                    button.setBackgroundResource(R.color.colorPrimary);
                }
            }
            @Override public void afterTextChanged(Editable editable) { }
        };
        return textWatcher;
    }
}
