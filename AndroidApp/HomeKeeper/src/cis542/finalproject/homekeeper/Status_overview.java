package cis542.finalproject.homekeeper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class Status_overview extends ActionBarActivity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private int value;
	private RadioGroup myradiogroup;
	String SENDER_ID = "573198047032";
	
	//Used for GCM registration.
	GoogleCloudMessaging gcm;
	String regid;
	Context context;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status_overview);
		
		context =getApplicationContext();
	
		// Check device for Play Services APK.
		if(checkPlayServices()){
			System.out.println("check succeed!");
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

	        if (regid.isEmpty()) {
	            registerInBackground();
	            
	        }else{
	        	System.out.println("regid not empty!");
	        }
		}
		value=63055;
		
		/*GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
	    .addApi(Drive.API)
	    .addScope(Drive.SCOPE_FILE)
	    .build();*/
		
		//setup httpurlconnection
	
		//Set button onclicklisteners
		Button Setttings = (Button) findViewById(R.id.Setting);
		Button Visual = (Button) findViewById(R.id.Visuals);
		myradiogroup = (RadioGroup) findViewById(R.id.myradiogroup);
		
		myradiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == R.id.Temper_select) {
					value=63055;
				}else if(checkedId == R.id.Distance_select){
					value=63057;
				}else{
					value=63056;
				}
			}
			
		});

		
		Visual.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("type", value);	
				intent.setClass(Status_overview.this, Visualization.class);
				startActivity(intent);
			}
		});
		
		Setttings .setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Status_overview.this, Settings.class);
				startActivity(intent);
			}
		});
		
		//Navigate to Status Visual
		
		
	

	}

	
	class HTTP extends AsyncTask<String, Void, String>
	{
	    @Override
	    protected String doInBackground(String... params) {
	        String result = "";
	        try {
	            String urlstring = params[0];
	            Log.i("EmonLog", "HTTP Connecting: "+urlstring);
	            URL url = new URL(urlstring);
	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	            try {
	                InputStream reader = new BufferedInputStream(urlConnection.getInputStream());
	                
	                String text = "";
	                int i = 0;
	                while((i=reader.read())!=-1)
	                {
	                    text += (char)i;
	                }
	                Log.i("EmonLog", "HTTP Response: "+text);
	                result = text;

	            } catch (Exception e) {
	            	int status = urlConnection.getResponseCode();
	                Log.i("EmonLog", "HTTP Exception: "+e+" Code is "+status);
	            }
	            finally {
	                Log.i("EmonLog", "HTTP Disconnecting");
	                urlConnection.disconnect();
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.i("EmonLog", "HTTP Exception: "+e);
	        }

	        return result;
	    }
	}
	
	
	
	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        System.out.println("Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        System.out.println("App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(Status_overview.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	           // Log.i(, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>(){
	    	
	    	@Override
	        protected String doInBackground(Void...params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;
	                System.out.println(msg);

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	    }.execute(null, null, null);
	}
	
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	}
	
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    System.out.println("Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	
	public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        // Explicitly specify that GcmIntentService will handle the intent.
	        ComponentName comp = new ComponentName(context.getPackageName(),
	        		GcmIntentService.class.getName());
	        // Start the service, keeping the device awake while it is launching.
	        startWakefulService(context, (intent.setComponent(comp)));
	        setResultCode(Activity.RESULT_OK);
	    }

		
	}
	
	public class GcmIntentService extends IntentService {
	    public static final int NOTIFICATION_ID = 1;
	    private NotificationManager mNotificationManager;
	    NotificationCompat.Builder builder;

	    public GcmIntentService() {
	        super("GcmIntentService");
	    }

	    @Override
	    protected void onHandleIntent(Intent intent) {
	        Bundle extras = intent.getExtras();
	        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        // The getMessageType() intent parameter must be the intent you received
	        // in your BroadcastReceiver.
	        String messageType = gcm.getMessageType(intent);

	        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
	            /*
	             * Filter messages based on message type. Since it is likely that GCM
	             * will be extended in the future with new message types, just ignore
	             * any message types you're not interested in, or that you don't
	             * recognize.
	             */
	            if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
	                sendNotification("Deleted messages on server: " +
	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // This loop represents the service doing some work.
	                for (int i=0; i<5; i++) {
	                   System.out.println("Working... " + (i+1)
	                            + "/5 @ " + SystemClock.elapsedRealtime());
	                    try {
	                        Thread.sleep(5000);
	                    } catch (InterruptedException e) {
	                    }
	                }
	                System.out.println("Completed work @ " + SystemClock.elapsedRealtime());
	                // Post notification of received message.
	                sendNotification("Received: " + extras.toString());
	                System.out.println("Received: " + extras.toString());
	            }
	        }
	        // Release the wake lock provided by the WakefulBroadcastReceiver.
	        GcmBroadcastReceiver.completeWakefulIntent(intent);
	    }

	    // Put the message into a notification and post it.
	    // This is just one simple example of what you might choose to do with
	    // a GCM message.
	    private void sendNotification(String msg) {
	        mNotificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);

	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, Status_overview.class), 0);

	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_android)
	        .setContentTitle("GCM Notification")
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(msg))
	        .setContentText(msg);

	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	    }
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status_overview, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	   // setContentView(R.layout.activity_status_overview);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
