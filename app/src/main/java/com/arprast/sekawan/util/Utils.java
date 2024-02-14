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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Utils {

    private final static SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
    private final static SimpleDateFormat yyyyMMddhhmmssWithoutSlash = new SimpleDateFormat("yyyyMMddhhmmssSSS");
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    public static String getPathImage(Context context, Uri uri ) {
        String[]  data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //    public String getFilePath(Context context, Uri uri) {
//        String[] projection = {MediaStore.Images.Media.DATA};
//
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(projection[0]);
//            String picturePath = cursor.getString(columnIndex); // returns null
//            cursor.close();
//            return picturePath;
//        }
//        return null;
//    }

    public static String getDateNow () {
        final Date today = Calendar.getInstance().getTime();
        return ddMMyyyy.format(today);
    }

   public static String getTrxNo(){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append( yyyyMMddhhmmssWithoutSlash.format(Calendar.getInstance().getTime()));
        stringBuilder.append("-");
        stringBuilder.append(getRandomString(10));
        return stringBuilder.toString();
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
