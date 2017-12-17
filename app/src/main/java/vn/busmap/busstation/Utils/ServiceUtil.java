package vn.busmap.busstation.Utils;

/**
 * Created by hkhoi on 3/28/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Provides functions to ensure the pre-conditions (Location, Internet Connection...) before
 * using Bus-Station
 */
public class ServiceUtil {

    public static final int FAILURE_INTERNET = 1;
    public static final int FAILURE_LOCATION = 2;
    public static final int SUCCESS = 0;


    /**
     * Check if Location and Internet is enable.
     * @param context
     * @return
     *          Success: SUCCESS = 0
     *          Location failure: FAILURE_LOCATION = 2
     *          Internet Failure: FAILURE_INTERNET = 1
     *          Both Internet and Location Failure: FAILURE_BOTH = 3
     *          Success: SUCCESS = 0
     */
    public static int checkServices(Context context) {
        // TODO: Implement checkServices
        if (!isInternetConnected()) {
            return FAILURE_INTERNET;
        } else if (!isLocationEnabled(context)) {
            return FAILURE_LOCATION;
        }
        return SUCCESS;
    }

    /**
     * Guides users to go to setting in order to enable Internet connection.
     * @param context
     */
    public static void guideInternet(final Context context) {
        // TODO: Refactor strings
        popAlertDialog(context,
                "Alert",
                "Please enable Internet connection",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new
                                Intent(Settings.ACTION_SETTINGS));
                    }
                });
    }

    /**
     * Guides users to go to setting in order to enable Location.
     * @param context
     */
    public static void guideLocation(final Context context) {
        // TODO: Implement guideLocation
        popAlertDialog(context,
                "Alert",
                "Please enable Location",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
    }

    /**
     * Pops up an alert dialog.
     * @param context
     * @param title
     * @param message
     * @param action what to do after click the OK button.
     */
    public static void popAlertDialog(Context context,
                                      String title,
                                      String message,
                                      DialogInterface.OnClickListener action) {
        // TODO: Refactor positive button
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", action)
                .show();
    }

    /**
     * Checks if device is connected to the Internet.
     * @return result
     */
    public static boolean isInternetConnected() {
        boolean ret = false;
        AsyncTask<Void, Void, Boolean> asyncTask =
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            InetAddress ipAddr = InetAddress.getByName("google.com");
                            return !ipAddr.equals("");
                        } catch (Exception e) {
                            return false;
                        }
                    }
                };
        try {
            asyncTask.execute();
            ret = asyncTask.get(3500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }

        return ret;
    }

    /**
     * Checks if device's GPS is enabled.
     * @param context
     * @return result
     */
    public static boolean isLocationEnabled(Context context) {
        LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }
}
