package in.oriange.iblebook.utilities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.oriange.iblebook.R;

public class Utilities {

    public static NotificationManager mManager;
    public static SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dfDate2 = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat dfDate3 = new SimpleDateFormat("MM/dd/yyyy");
    public static SimpleDateFormat dfDate4 = new SimpleDateFormat("yyyy/MM/dd");
    static AlertDialog.Builder alertDialog;

    public static boolean isEmailValid(EditText edt) {
        edt.setError(null);
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = edt.getText().toString().trim();
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWebsiteValid(EditText edt) {
        edt.setError(null);
        String expression = "w{3}\\.[a-z]+\\.?[a-z]{2,3}(|\\.[a-z]{2,3})";
        CharSequence inputStr = edt.getText().toString().trim();
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLandlineValid(EditText edt) {
        edt.setError(null);
        String expression = "((\\+*)((0[ -]+)*|(91 )*)(\\d{12}+|\\d{10}+))|\\d{5}([- ]*)\\d{6}";
        CharSequence inputStr = edt.getText().toString().trim();
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isIfscValid(EditText edt) {
        edt.setError(null);
        String expression = "^[A-Za-z]{4}0[A-Z0-9a-z]{6}$";
        CharSequence inputStr = edt.getText().toString().trim();
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isGSTValid(EditText edt) {
        edt.setError(null);
        String expression = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}";
        CharSequence inputStr = edt.getText().toString().trim();
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(EditText... edt) {
        int cnt = 0;
        for (int i = 0; i < edt.length; i++)
            edt[i].setError(null);

        for (int i = 0; i < edt.length; i++)
            if (edt[i].getText().toString().trim().length() == 0
                    || edt[i].getText().toString().trim().equalsIgnoreCase("")
                    || edt[i].getText().toString().trim().equalsIgnoreCase(" ")) {
                edt[i].setError("Please enter mandatory fields");
                edt[i].requestFocus();
                cnt++;
            }
        return (cnt == 0) ? false : true;
    }

    public static boolean isPanNum(EditText edt) {
        edt.setError(null);
        if ((edt.getText().toString().trim().length() == 10) && (isValidPanNum(edt.getText().toString().trim())))
            return true;
        else {
            return false;
        }
    }

    public static boolean isMobileNo(EditText edt) {
        edt.setError(null);
        if ((edt.getText().toString().trim().length() == 10)
                && (isValidMobileno(edt.getText().toString().trim())))
            return true;
        else {
            return false;
        }
    }

    private static boolean isValidMobileno(String mobileno) {
        String Mobile_PATTERN = "^[6-9]{1}[0-9]{9}$";                                               //^[+]?[0-9]{10,13}$
        Pattern pattern = Pattern.compile(Mobile_PATTERN);
        Matcher matcher = pattern.matcher(mobileno);
        return matcher.matches();
    }

    public static boolean isPinCode(EditText edt) {
        edt.setError(null);
        if ((edt.getText().toString().trim().length() == 6)
                && (isValidPinCode(edt.getText().toString().trim())))
            return true;
        else {
            return false;
        }
    }

    private static boolean isValidPinCode(String mobileno) {
        String Mobile_PATTERN = "^[1-9][0-9]{5}$";                                               //^[+]?[0-9]{10,13}$
        Pattern pattern = Pattern.compile(Mobile_PATTERN);
        Matcher matcher = pattern.matcher(mobileno);
        return matcher.matches();
    }

    private static boolean isValidPanNum(String pannum) {
        String Pannum_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern pattern = Pattern.compile(Pannum_PATTERN);
        Matcher matcher = pattern.matcher(pannum);
        return matcher.matches();
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }


    //******************************* Massages Methods *********************************************

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static void showMessageString(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    public static void showAlertDialog(Context context, String title,
                                       String message, Boolean status) {
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        if (status != null)
            alertDialog.setIcon((status) ? R.drawable.ic_success_24dp : R.drawable.ic_alert_red_24dp);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD = alertDialog.create();
        alertD.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alertD.show();
    }


    public static void showSnackBar(ViewGroup viewGroup, String message) {
        Snackbar snackbar = Snackbar
                .make(viewGroup, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null)
            return false;
        else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true; // <-- -- -- Connected
        }
        return false; // <-- -- -- NOT Connected
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
