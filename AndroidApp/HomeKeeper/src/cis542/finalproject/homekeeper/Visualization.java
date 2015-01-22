package cis542.finalproject.homekeeper;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class Visualization extends ActionBarActivity {
	private LinkedHashMap<Integer, Float> datas = new LinkedHashMap<Integer, Float>();;
	private int global_count =1402112800;
	private DrawView drawView;
	private Canvas current =null;
	private int value;
	private String unit;
	private String variable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_visualization);
		drawView = new DrawView(this);
	    setContentView(drawView);
	    datas.put(1401312800, 0f);
        datas.put(1401412800, 0f);
        datas.put(1401412800, 0f);
        datas.put(1401512800, 0f);
        datas.put(1401612800, 0f);
        datas.put(1401712800, 0f);
        datas.put(1401812800, 0f);
        datas.put(1401912800, 0f);
        datas.put(1402012800, 0f);
     //   Button back = (Button)findViewById(R.id.Back);
	/*    back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
	    	
	    });*/
        Bundle extras = getIntent().getExtras();
        value = extras.getInt("type");
        if(value==63055){
        	unit="C";
        	variable="Temperature";
        }else if(value==63057){
        	unit="cm";
        	variable="Distance";
        }else{
        	unit ="lux";
        	variable="Lightness";
        }
	    Grabdata updates = new Grabdata();
	    updates.start();
	   
	   
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visualization, menu);
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
	
	
	public class Grabdata extends Thread{
		
		
		public void run(){
			while(true){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			 String result ="";
			try {
	            result = new HTTP().execute("http://emoncms.org/feed/value.json?apikey=fe7c4476efe13895c361adc56440005d&id="+value).get();
	            Log.i("EmonLog", "Result: "+result);
	            if(datas.size()>=10){
					int i = getFirstKey(datas);
					datas.remove(i);
				}
	            result = result.replace("\"", "");
	            System.out.println(Float.valueOf(result)+" datas size is "+datas.size());
				datas.put(global_count,Float.valueOf(result));
			
				//drawView.onDraw(canvas);
				global_count+=100000;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						drawView.invalidate();
						drawView.draw_myElectric(current,1f,datas);
					}
				});
	        } catch (Exception e) {
	            Log.i("EmonLog", "Error: "+e);
	        }
			
			}
		}
	}
	
	public int getFirstKey(LinkedHashMap<Integer, Float> myMap) {
		 
		  int keys = 0;
		  for (int key : myMap.keySet()) {
			  keys=key;
			  break;
		  }
		  return keys;
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
	
	
	class DrawView extends View {
	    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    Handler uiThread = new Handler();
	    public DrawView(Context context) {
	        super(context);
	    }
	    
	    @Override
	    public void onDraw(Canvas canvas ) {

	        float power = 250.4f;

	        Typeface robotoNormal = Typeface.create("Roboto",Typeface.NORMAL);
	        Typeface robotoBold = Typeface.create("Roboto",Typeface.BOLD);
	        int screenWidth = getWidth();
	        int screenHeight = getHeight();
	        float scale = screenWidth / 720.0f;
	        float left = 30*scale;
	        float  graphHeight = 0.6f * screenHeight;
            float top = screenHeight - graphHeight-(30*scale);
            
	     

	      /*  // Example data
	        LinkedHashMap<Integer, Float> data = new LinkedHashMap<Integer, Float>();
	        data.put(1401321600, 20.1f);
	        data.put(1401408000, 6.0f);
	        data.put(1401494400, 8.0f);
	        data.put(1401580800, 2.5f);
	        data.put(1401667200, 7.3f);
	        data.put(1401753600, 3.0f);
	        data.put(1401840000, 0.8f);
	        data.put(1401926400, 0.5f);
	        data.put(1402012800, 0.4f);
	        data.put(1402012800, 0.2f);*/
	        current = canvas;
	        draw_myElectric(canvas,power,datas);
	     
	    }

	    public void draw_myElectric(Canvas canvas, Float power, LinkedHashMap<Integer, Float> data)
	    {
	        // Typeface definitions
	        Typeface robotoNormal = Typeface.create("Roboto",Typeface.NORMAL);
	        Typeface robotoBold = Typeface.create("Roboto",Typeface.BOLD);

	        // Clear background
	        paint.setColor(Color.parseColor("#222222"));
	        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

	        // View needs to be responsive to different screen width's and heights
	        // and whether the display is in portrait or landscape mode
	        //-------------------------------------------------------------------------

	        int screenWidth = getWidth();
	        int screenHeight = getHeight();

	        float scale = 1.0f;

	        float left = 0;
	        float top = 0;
	        float graphWidth = 0;
	        float graphHeight = 0;

	        if (screenWidth<screenHeight) {
	            // Portrait mode:
	            scale = screenWidth / 720.0f;

	            left = 30*scale;
	            graphWidth = screenWidth - 60*scale;

	            graphHeight = 0.6f * screenHeight;
	            top = screenHeight - graphHeight-(30*scale);

	        } else {
	            // Landscape mode:
	            scale = screenHeight / 720.0f;

	            left = screenWidth * 0.35f;
	            graphWidth = screenWidth * 0.65f - 30*scale;

	            top = (90*scale);
	            graphHeight = screenHeight - (120*scale);
	        }

	        // Power now text and value's
	        //-------------------------------------------------------------------------

	        // Grey horizontal line at the top
	        paint.setColor(Color.parseColor("#333333"));
	        canvas.drawLine(30*scale, 60*scale, screenWidth-30*scale, 60*scale, paint);

	        // My Electric text
	     

	        // Power value text
	      /*  paint.setColor(Color.parseColor("#0699fa"));
	        paint.setTextSize(160*scale);        
	        canvas.drawText(String.format("%.0f", power)+"W", 30*scale, 260*scale, paint);*/

	        // kwh text
	        paint.setTypeface(robotoNormal);
	        paint.setTextSize((int)35*scale);
	        //canvas.drawText("USE TODAY: 0.5 kWh", 30*scale, (int)320*scale, paint);

	        // Start of graph drawing:
	        //-------------------------------------------------------------------------

	        // Margin and inner dimensions
	        float margin = 12 * scale;
	        float innerWidth = graphWidth - 2*margin;
	        float innerHeight = graphHeight - 2*margin;

	        // Draw Axes
	        paint.setColor(Color.rgb(6,153,250));
	        canvas.drawLine(left, top, left, top+graphHeight, paint);
	        canvas.drawLine(left, top+graphHeight, left+graphWidth, top+graphHeight, paint);

	        // Draw kWh label top-left
	        paint.setTypeface(robotoBold);
	        paint.setColor(Color.parseColor("#aaaaaa"));
	        paint.setTextSize((int)35*scale);
	        canvas.drawText("Variable: "+variable, (int)30*1200/720+300, (int)100*1646/720, paint);
	        paint.setTextSize(35*scale);
	        canvas.drawText(unit, left+60, top-30*scale, paint);
	        // Auto detect xmin, xmax, ymin, ymax
	        float xmin = 0;
	        float xmax = 0;
	        float ymin = 0;
	        float ymax = 0;
	        boolean s = false;

	        Iterator<Integer> keySetIterator = data.keySet().iterator();
	        while(keySetIterator.hasNext()){
	            Integer time = keySetIterator.next();
	            float value = (Float) data.get(time);

	            if (!s) {
	                xmin = time;
	                xmax = time;
	                ymin = value;
	                ymax = value;
	                s = true;
	            }

	            if (value>ymax) ymax = value;
	            if (value<ymin) ymin = value;
	            if (time>xmax) xmax = time;
	            if (time<xmin) xmin = time;               
	        }

	        float r = (ymax - ymin);
	        ymax = (ymax - (r / 2f)) + (r/1.5f);
	        // Fixed min y
	        ymin = 0;

	        float barWidth = 3600*20;
	        xmin -= barWidth /2;
	        xmax += barWidth /2;

	        float barWidthpx = (barWidth / (xmax - xmin)) * innerWidth;

	        // kWh labels on each bar
	        paint.setTextAlign(Align.CENTER);
	        paint.setTextSize(35*scale);

	        keySetIterator = data.keySet().iterator();

	        while(keySetIterator.hasNext()){

	            Integer time = keySetIterator.next();
	            float value = (Float) data.get(time);

	            float px = ((time - xmin) / (xmax - xmin)) * innerWidth;
	            float py = ((value - ymin) / (ymax - ymin)) * innerHeight;

	            float barLeft = left + margin + px - barWidthpx/2;
	            float barBottom = top + margin + innerHeight;

	            float barTop = barBottom - py;
	            float barRight = barLeft + barWidthpx;

	            paint.setColor(Color.rgb(6,153,250));
	            canvas.drawRect(barLeft,barTop,barRight,barBottom,paint);

	            // Draw kwh label text
	            if (py>38*scale) {
	              paint.setColor(Color.parseColor("#ccccff"));
	              int offset = (int)(45*scale);
	              canvas.drawText(String.format("%.0f", value), left+margin+px, barTop + offset, paint);
	            }
	        }
	    }
	}
}
