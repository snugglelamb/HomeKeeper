package cis542.finalproject.homekeeper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cis542.finalproject.homekeeper.Visualization.HTTP;

import com.google.android.gms.gcm.GoogleCloudMessaging;


import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Settings extends ActionBarActivity {
	AtomicInteger msgId = new AtomicInteger();
	String SENDER_ID = "573198047032";
	GoogleCloudMessaging gcm;
	
	//5 temp
	//6 light
	//7 distance
	//private String sb =null;
	private TextView temp = null;
	private TextView dist = null;
	private TextView light = null;
	private SeekBar temp_seek =null;
	private SeekBar distance_seek =null;
	private SeekBar light_seek =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		gcm = GoogleCloudMessaging.getInstance(this);
		
		Button confirm = (Button) findViewById(R.id.confirm);
		confirm.setOnClickListener(new sendonclick());
		
		temp = (TextView)findViewById(R.id.temp_value);
		dist = (TextView)findViewById(R.id.distance_value);
		light = (TextView)findViewById(R.id.light_value);
		
		temp_seek=(SeekBar)findViewById(R.id.seekBar_temper);
		distance_seek=(SeekBar)findViewById(R.id.seekBar_distance);
		light_seek=(SeekBar)findViewById(R.id.seekBar_light);
		
		
		light_seek.setMax(1200);
		temp_seek.setMax(30);
		distance_seek.setMax(200);
		
		String result ="";
		try {
            result = new HTTP().execute("http://emoncms.org/feed/value.json?apikey=fe7c4476efe13895c361adc56440005d&id=63052").get();
            Log.i("EmonLog", "Result: "+result);
            
            result = result.replace("\"", "");
     
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
		temp.setText(result);
		temp_seek.setProgress(Integer.parseInt(result));
		String newresult="";
		try {
			newresult = new HTTP().execute("http://emoncms.org/feed/value.json?apikey=fe7c4476efe13895c361adc56440005d&id=63053").get();
            Log.i("EmonLog", "Result: "+newresult);
            
            newresult = newresult.replace("\"", "");
     
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
		light.setText(newresult);
		light_seek.setProgress(Integer.parseInt(newresult));
		
		String resultnew="";
		try {
			resultnew= new HTTP().execute("http://emoncms.org/feed/value.json?apikey=fe7c4476efe13895c361adc56440005d&id=63054").get();
            Log.i("EmonLog", "Result: "+result);
            
            resultnew= resultnew.replace("\"", "");
     
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
		dist.setText(resultnew);
		distance_seek.setProgress(Integer.parseInt(resultnew));
		
		
		temp_seek.setOnSeekBarChangeListener(new temp_set());
		distance_seek.setOnSeekBarChangeListener(new distance_set());
		light_seek.setOnSeekBarChangeListener(new light_set());
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
	
	
	
	
	private void Set_temp_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://emoncms.org/input/post.json?apikey=d9bdad5e15fe86a55dc4be9d82b90b8d&node=13&csv="+value).get();
            Log.i("EmonLog", "Result: "+result);
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
	}
	
	private void Set_light_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://emoncms.org/input/post.json?apikey=d9bdad5e15fe86a55dc4be9d82b90b8d&node=15&csv="+value).get();
            Log.i("EmonLog", "Result: "+result);
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
	}
	
	private void Set_distance_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://emoncms.org/input/post.json?apikey=d9bdad5e15fe86a55dc4be9d82b90b8d&node=14&csv="+value).get();
            Log.i("EmonLog", "Result: "+result);
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
	}
	
	class HTTPwrite extends AsyncTask<String, Void, String>
	{
	    @Override
	    protected String doInBackground(String... params) {
	        String result = "";
	        try {
	            String urlstring = params[0];
	            Log.i("EmonLog", "HTTP Connecting: "+urlstring);
	            URL url = new URL(urlstring);
	            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	           
	           	urlConnection.setRequestMethod("GET");
	            try {
	            	urlConnection.setDoOutput(true);
	                //OutputStream writer = new BufferedOutputStream(urlConnection.getOutputStream());
	            	DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
	        		wr.flush();
	        		wr.close();
	        		int status = urlConnection.getResponseCode();
	        		Log.i("EmonLog", "HTTP Response: "+status);
	                result = "success";

	                BufferedReader in = new BufferedReader(
	        		        new InputStreamReader(urlConnection.getInputStream()));
	        		String inputLine;
	        		StringBuffer response = new StringBuffer();
	         
	        		while ((inputLine = in.readLine()) != null) {
	        			response.append(inputLine);
	        		}
	        		in.close();
	         
	        		//print result
	        		System.out.println(response.toString());
	                
	            } catch (Exception e) {
	                Log.i("EmonLog", "HTTP Exception: "+e);
	                result = "fail";
	            }
	            finally {
	                Log.i("EmonLog", "HTTP Disconnecting");
	                urlConnection.disconnect();
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.i("EmonLog", "HTTP Exception: "+e);
	            result = "fail";
	        }

	        return result;
	    }
	}
	
	
	class sendonclick implements OnClickListener{
		String temp_value = "";
		String distance_value = "";
		String light_value = "";
		
		@Override
		public void onClick(final View view) {
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					temp = (TextView)findViewById(R.id.temp_value);
					temp_value = (String) temp.getText();
					dist = (TextView)findViewById(R.id.distance_value);
					distance_value = (String) dist.getText();
					light = (TextView)findViewById(R.id.light_value);
					light_value = (String) light.getText();
				}
			});
			
			Set_temp_threshold(temp_value);
			Set_light_threshold(distance_value);
			Set_distance_threshold(light_value);
		}
		
	}


	
	
	 private class temp_set implements OnSeekBarChangeListener{
	    
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub		
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final int a = progress;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						temp = (TextView)findViewById(R.id.temp_value);
						temp.setText(Integer.toString(a));
					}
				});
			}
	    }
	 private class distance_set implements OnSeekBarChangeListener{
		    
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub		
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final int a = progress;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dist = (TextView)findViewById(R.id.distance_value);
						dist.setText(Integer.toString(a));
					}
				});
			}
	    }
	 private class light_set implements OnSeekBarChangeListener{
		    
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub		
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				final int a = progress;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						light = (TextView)findViewById(R.id.light_value);
						light.setText(Integer.toString(a));
					}
				});
			}
	    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
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
