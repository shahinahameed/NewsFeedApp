package com.example.newsfeed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	Download downloadDataAsync;
	 ListView listView ;
	 CustomListAdapter custadapter;
	 ArrayList<HashMap<String, String>> newsList=new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
             listView = (ListView) findViewById(R.id.list);
            
             listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            	  
                 @Override  
                 public void onItemClick(AdapterView<?> parent, View view,int position, long id) { 
                	 //calling next activity and sending data to that
                	Intent localIntent2 = new Intent(getApplicationContext(),DetailsActivity.class);
                	localIntent2.putExtra("details", newsList.get(position));
         			startActivity(localIntent2);
                 }
             }); 
        //async task to fetch data through API
        downloadDataAsync = new Download(this);
        downloadDataAsync.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public class Download extends AsyncTask<String, Integer, String> {
    	
    	 public MainActivity activity;
    	 
    	public Download(MainActivity mainActivity) {
    		this.activity = mainActivity;
		}

		@Override
    	protected void onPreExecute() {
			super.onPreExecute();
			//Show something like splash screen and meantime load data through API
		}
    	
		@Override
		protected String doInBackground(String... params) {
			
			try {
				//data download
				 URL url = new URL("http://newsapi.org/v2/top-headlines?country=us&apiKey=de029140eb4b45d7bee88cc905b41f68");
	                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	                try {
	                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	                    StringBuilder stringBuilder = new StringBuilder();
	                    String line;
	                    while ((line = bufferedReader.readLine()) != null) {
	                        stringBuilder.append(line).append("\n");
	                    }
	                    bufferedReader.close();
	                    return stringBuilder.toString();
	                }
	                finally{
	                    urlConnection.disconnect();
	                }
			} catch (Exception e) {
				System.out.println(e.toString());
				return e.toString();
			}
			
		}
		@Override
	    protected void onPostExecute(String result) {
			
			try {
				//process data
			final JSONObject jo = new JSONObject(result);
			String status=jo.getString("status");
			
			if(status.equalsIgnoreCase("ok")){
				
				HashMap<String, String> newsData;
				
				final JSONArray ja = new JSONArray(jo.getString("articles"));
				
				String[] maintitle=new String[ja.length()];
				
				for(int i=0;i<ja.length();i++){
					newsData=new HashMap<String, String>();
					
					JSONObject source=ja.getJSONObject(i).getJSONObject("source");
					
					newsData.put("source_id",source.getString("id"));
					newsData.put("source_name",source.getString("name"));
					newsData.put("author",ja.getJSONObject(i).getString("author"));
					newsData.put("title",ja.getJSONObject(i).getString("title"));
					newsData.put("description",ja.getJSONObject(i).getString("description"));
					newsData.put("url",ja.getJSONObject(i).getString("url"));
					newsData.put("urlToImage",ja.getJSONObject(i).getString("urlToImage"));
					newsData.put("publishedAt",ja.getJSONObject(i).getString("publishedAt"));
					newsData.put("content",ja.getJSONObject(i).getString("content"));
					newsList.add(newsData);
					maintitle[i]=ja.getJSONObject(i).getString("title");
					
				}
				custadapter=new CustomListAdapter(activity,maintitle,newsList);  
				listView.setAdapter(custadapter); 
				
			}else{
				//Inform user about error
				Toast.makeText(getApplicationContext(), "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
			}
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
	        }
		
    }
}
