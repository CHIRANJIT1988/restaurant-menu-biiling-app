package restaurent.billing.demo;

import static restaurent.billing.demo.CommonUtilities.SENDER_ID;
import static restaurent.billing.demo.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

import restaurent.billing.demo.activity.GCMActivity;
import restaurent.billing.demo.activity.MainActivity;
import restaurent.billing.demo.helper.Helper;
import restaurent.billing.demo.helper.Order;
import restaurent.billing.demo.sqlitedb.SQLiteDatabaseHelper;


public class GCMIntentService extends GCMBaseIntentService
{

	private static final String TAG = "GCMIntentService";

	
    public GCMIntentService() 
    {
        super(SENDER_ID);
    }

    
        
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) 
    {
    
    	Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");

        Toast.makeText(context, "Device registered: regId = " + registrationId, Toast.LENGTH_LONG).show();

        Log.d("NAME", GCMActivity.emp_name);

        ServerUtilities.register(context, GCMActivity.emp_name, GCMActivity.phone_no, registrationId);
    }

    
    
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) 
    {
    
    	Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    
    
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) 
    {
    
    	Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        
        displayMessage(context, message);

        // notifies user
        generateNotification(context, message);
    }

   
    
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total)
    {
    
    	Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        
        displayMessage(context, message);

        // notifies user
        generateNotification(context, message);
    }

    
    
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) 
    {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    
    
    @Override
    protected boolean onRecoverableError(Context context, String errorId) 
    {
    
    	// log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        
        return super.onRecoverableError(context, errorId);
    }

    
    
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message)
    {

        if(message == null)
        {
            return;
        }


        //if(message.split("<BR>").length == 6)
        {

            Order order = Helper.orderObject(message);
            new SQLiteDatabaseHelper(context).insertOrder(order);

            int icon = R.drawable.ic_email_outline_white_24dp;
            long when = System.currentTimeMillis();


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(icon, "Order Received", when);


            String title = "Order Received"; //context.getString(R.string.app_name);


            Intent notificationIntent = new Intent(context, MainActivity.class);


            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notification.setLatestEventInfo(context, title, "Table No " + order.getTableNo(), intent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;


            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;


            //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");


            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(0, notification);
        }
    }
}