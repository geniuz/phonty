/*
 * Copyright (C) 2012 PhontyCom - Belize
 * 
 * This file is part of Phonty(http://www.phonty.com/android)
 * 
 * Phonty is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.phonty.improved;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.phonty.improved.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

public class DownloadContactsActivity extends Activity {
	private DownloadContactsTask task;
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.download_contacts);
	     task = new DownloadContactsTask();
	     task.execute("http://phonty.com/");
	 }
	 
	 @Override
	 public void onResume() {
		 super.onResume();
	 }
	
	 @Override
	 public void onPause() {
		 super.onPause();
	 }
	 
	 public class DownloadContactsTask extends AsyncTask<String, Integer, String> {
		 
		 private HttpURLConnection conn;
	     private InputStream stream; //to read
	     private ByteArrayOutputStream out; //to write
	      
	     private double fileSize;
	     private double downloaded; // number of bytes downloaded
	     private int status = DOWNLOADING; //status of current process
	      
	     private ProgressDialog progressDialog;    
	 
	     private static final int MAX_BUFFER_SIZE = 1024; //1kb
	     public static final int DOWNLOADING = 0;
	     public static final int COMPLETE = 1;
	      
	     public DownloadContactsTask(){
	      conn = null;
	      fileSize = 0;
	      downloaded = 0;
	      status = DOWNLOADING;
	     }

	   @Override
	  protected String doInBackground(String... url) {
	 
	   try {
	     conn = (HttpURLConnection) new URL("http://phone.scat.su/japi/contacts/").openConnection();
	     fileSize = conn.getContentLength();
	     out = new ByteArrayOutputStream((int) fileSize);
	     conn.connect();
	 
	     stream = conn.getInputStream();
	     // loop with step 1kb
	     while (status == DOWNLOADING) {
	      byte buffer[];
	 
	      if (fileSize - downloaded > MAX_BUFFER_SIZE) {
	       buffer = new byte[MAX_BUFFER_SIZE];
	      } else {
	       buffer = new byte[(int) (fileSize - downloaded)];
	      }
	      int read = stream.read(buffer);
	 
	      if (read == -1) {
	       publishProgress(100);
	       break;
	      }
	      // writing to buffer
	      out.write(buffer, 0, read);
	      downloaded += read;
	      // update progress bar
	      publishProgress((int) ((downloaded / fileSize) * 100));
	     }// end of while
	 
	     if (status == DOWNLOADING) {
	      status = COMPLETE;
	     }
	     BufferedReader rd = new BufferedReader(new InputStreamReader((InputStream) new ByteArrayInputStream(out.toByteArray())), 4096);
	     String line;
	     StringBuilder sb =  new StringBuilder();
	     while ((line = rd.readLine()) != null) {
	     		sb.append(line);
	     }
	     rd.close();
	     return sb.toString();
	   }
	 
	   catch (Exception e) {
	    System.out.println("Exception: " + e);
	    return null;
	   }// end of catch
	  }// end of class DownloadImageTask()
	    
	  @Override
	  protected void onProgressUpdate(Integer... changed) {
	   progressDialog.setProgress(changed[0]);
	     }
	   
	  @Override
	  protected void onPreExecute() {
	   progressDialog = new ProgressDialog(DownloadContactsActivity.this);
	   progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	   progressDialog.setMessage("Loading...");
	   progressDialog.setCancelable(false);
	   progressDialog.show();
	  }
	   
	  @Override
	  protected void onPostExecute(String result) {
	   if(result != null){
	   progressDialog.dismiss();
	   
	   JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result.toString());
		    HashMap<String,String> contactsList = new HashMap<String,String>();
		    for (int i = 0; i < jsonArray.length(); i++) {
			   JSONObject jsonObject = jsonArray.getJSONObject(i);
			   contactsList.put(jsonObject.getString("name"),jsonObject.getString("phone"));
			 }
		} catch (JSONException e) {
		}
				
	   }
	   else {
	    progressDialog.dismiss();
	    AlertDialog alertDialog;
	    alertDialog = new AlertDialog.Builder(DownloadContactsActivity.this).create();
	    alertDialog.setTitle(R.string.infoLabel);
	    alertDialog.setMessage(getString(R.string.loadErrorLabel));
	    alertDialog.setButton(getString(R.string.close),
	      new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dlg, int sum) {
	        // do nothing, close
	       }
	      });
	    alertDialog.show();
	   }
	  }
	 }
	 
}
