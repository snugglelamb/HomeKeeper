package cis542.finalproject.homekeeper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
		
		temp = (TextView)findViewById(R.id.distance_value);
		dist = (TextView)findViewById(R.id.distance_value);
		light = (TextView)findViewById(R.id.light_value);
		
		
		temp_seek=(SeekBar)findViewById(R.id.seekBar_temp);
		distance_seek=(SeekBar)findViewById(R.id.seekBar_distance);
		light_seek=(SeekBar)findViewById(R.id.seekBar_light);
		temp_seek.setMax(40);
		distance_seek.setMax(300);
		light_seek.setMax(200);
		temp_seek.setOnSeekBarChangeListener(new temp_set());
		distance_seek.setOnSeekBarChangeListener(new distance_set());
		light_seek.setOnSeekBarChangeListener(new light_set());
	}
	
	private void httppost(){
		try 
		{
		    URL url;
		    DataOutputStream printout;
		    DataInputStream  input;
		    url = new URL ("http://192.168.1.111");
		    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		    urlConnection.setRequestMethod("POST");  
		    urlConnection.setDoInput (true);
		    urlConnection.setDoOutput (true);
		    urlConnection.setUseCaches (false);

		    urlConnection.setConnectTimeout(10000);  
		    urlConnection.setReadTimeout(10000);

		    urlConnection.setRequestProperty("Content-Type","application/json");   
		    urlConnection.connect();  

		    JSONObject jsonParam = new JSONObject();

		      JSONArray arr = new JSONArray();
		      arr.put("LNCf206KYa5b");
		      arr.put("oWdC0hnm1jjJ");
		      jsonParam.put("places", arr);
		      jsonParam.put("action", "Do");

		            printout = new DataOutputStream(urlConnection.getOutputStream ());
		            printout.writeUTF(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
		            printout.flush ();
		            printout.close ();

		            int HttpResult =urlConnection.getResponseCode();  

		        if(HttpResult ==HttpURLConnection.HTTP_OK){  
		        BufferedReader br = new BufferedReader(new InputStreamReader(  
		            urlConnection.getInputStream(),"utf-8"));  
		        String line = null;  

		           while ((line = br.readLine()) != null) {  
		         //   sb.append(line + "\n");  
		        }  
		        br.close();  

		           //System.out.println(""+sb.toString());  

		        }else{  
		             System.out.println(urlConnection.getResponseMessage());  
		        }  
		    } catch (MalformedURLException e) {  

		        e.printStackTrace();  
		    }  
		    catch (IOException e) {  

		        e.printStackTrace();  
		    } catch (JSONException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }finally{  
		       /* if(urlConnection!=null)  
		           urlConnection.disconnect(); */ 
		   // }  
		}
	}
	
	private void Set_temp_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://192.168.1.111/emoncms/feed/value.json?","apikey=b53ec1abe610c66009b207d6207f2c9e&node=13&key=1&csv="+value).get();
            Log.i("EmonLog", "Result: "+result);
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
	}
	
	private void Set_light_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://192.168.1.111/emoncms/feed/post.json?apikey=b53ec1abe610c66009b207d6207f2c9e","&node=13&key=2&csv="+value).get();
            Log.i("EmonLog", "Result: "+result);
        } catch (Exception e) {
            Log.i("EmonLog", "Error: "+e);
        }
	}
	
	private void Set_distance_threshold (String value){
		try {
            String result = new HTTPwrite().execute("http://192.168.1.111/emoncms/post/list.json?","apikey=b53ec1abe610c66009b207d6207f2c9e&node=13&key=3&csv="+value).get();
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
	           
	           	urlConnection.setRequestMethod("POST");
	            try {
	            	urlConnection.setDoOutput(true);
	                //OutputStream writer = new BufferedOutputStream(urlConnection.getOutputStream());
	            	DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
	            	
	            	String urlParameters = params[1];
	            	System.out.println(urlParameters);
	            	wr.writeBytes(urlParameters);
	        		wr.flush();
	        		wr.close();
	              //  String text = params[1];
	               
	                //writer.write(Integer.valueOf(text));
	                //writer.flush();
	              //  Log.i("EmonLog", "HTTP Response: "+text);
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
