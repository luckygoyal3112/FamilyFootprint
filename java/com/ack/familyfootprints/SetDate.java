package com.ack.familyfootprints;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by Lucky Goyal on 12/21/2015.
 */
public class SetDate implements OnFocusChangeListener, OnDateSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context mContext;

    public SetDate(EditText editText, Context ctx) {
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        myCalendar = Calendar.getInstance();
        this.mContext= ctx;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // this.editText.setText();

        String myFormat = "MMM dd, yyyy"; //In which you need put here

        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String day = String.format("%02d", dayOfMonth);
        String month = String.format("%02d", monthOfYear + 1);

        //editText.setText(sdformat.format(myCalendar.getTime()));
        editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(day).append("/").append(month).append("/").append(year).append(" "));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        if (hasFocus) new DatePickerDialog(mContext, this,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }
}