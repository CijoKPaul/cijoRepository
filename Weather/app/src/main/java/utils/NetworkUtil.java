package utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * The Class NetworkUtil to perform network related checkings.
 */
public class NetworkUtil {

    /**
     * Helper method to check network availability.
     *
     * @param context - calling context
     * @return - Returns true if network is available otherwise false
     */
    public static boolean isNetworkAvailable(final Context context, boolean showDialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isAvailable && showDialog) {
            final Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
                }
            });
        }
        return isAvailable;
    }

}
