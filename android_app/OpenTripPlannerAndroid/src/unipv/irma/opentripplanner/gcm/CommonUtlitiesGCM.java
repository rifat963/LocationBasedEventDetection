/**
 * 
 */
package unipv.irma.opentripplanner.gcm;

/**
 * @author rifat
 *
 */

import android.content.Context;
import android.content.Intent;

public final class CommonUtlitiesGCM {
	
	// give your server registration url here
    static final String SERVER_URL = "http://eventmanager-irmatripplanner.rhcloud.com/gcmalert/register.php"; 

    // Google project id
    public static final String SENDER_ID = "378582953993"; 

    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    static final String DISPLAY_MESSAGE_ACTION = "unipv.irma.opentripplanner.gcm.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
