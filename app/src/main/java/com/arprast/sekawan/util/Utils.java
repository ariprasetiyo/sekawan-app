package com.arprast.sekawan.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class Utils {
    public static String getPathImage(Uri uri, Context context) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void showTost( Context context, String text) {
        Toast invalidateInputMesssage = Toast.makeText(context, text, Toast.LENGTH_LONG);
        invalidateInputMesssage.setGravity(Gravity.CENTER, 0, 0);
        invalidateInputMesssage.show();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone.length() < 10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        EditText editText;
        public DatePickerFragment(EditText editText){
            this.editText = editText;
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            editText.setText(String.valueOf(day) + "/"
                    + String.valueOf(month + 1) + "/" + String.valueOf(year));
        }
    }

}
