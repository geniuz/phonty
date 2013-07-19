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


import java.util.ArrayList;
import com.phonty.improved.Calls.CallItem;
import com.phonty.improved.PhontyService.Tasks;
import com.phonty.improved.PriceActivity.RatesTask;
import com.phonty.improved.R;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CallsActivity extends Activity {
	private Button showButton;
    private ProgressDialog progress;
    private Boolean taskIsRunning = false;
	private CallsTask task;
	private String country;
	private ListView callsViewList;
	private AutoCompleteTextView countryEdit; 
	Intent intent;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.calls);
 
 	     callsViewList = (ListView)findViewById(R.id.callsList);
	     callsViewList.setCacheColorHint(Color.TRANSPARENT);
	     final TextView tip = (TextView)findViewById(R.id.empty);
    	 callsViewList.setEmptyView(tip);
	}
	     	 
	 @Override
	 public void onResume() {
		 super.onResume();
   	  	 progress = ProgressDialog.show(CallsActivity.this, getString(R.string.processing), getString(R.string.newAccountTip), true,false);
         task = new CallsTask();
         task.execute();
	 }
	
	 @Override
	 public void onPause() {
		 super.onPause();
	 }
	 

	 class CallsTask extends AsyncTask<Tasks, String, Boolean>  {
		ArrayList<CallItem> callsList;
		boolean result,smsresult;
		
		protected Boolean doInBackground(Tasks... action) {
			if (taskIsRunning) { this.cancel(true); return null; }
			taskIsRunning = true;
 			Calls calls = new Calls(getResources().getText(R.string.calls_url).toString(),getApplicationContext());
			result = calls.get(country);
 			callsList = calls.VALUES; 			 			
 			return result;
		}
		protected void onProgressUpdate(Integer... progress) {}
		protected void onPostExecute(Boolean result) {
			taskIsRunning = false;
			progress.dismiss();
			Log.e("ARRAY",callsList.toString());
			if (result) {
			     ListAdapter directions = new SimpleAdapter(CallsActivity.this,
			    		 	callsList,
			    		 	R.layout.calls_view,
			    		 	new String[] {CallItem.NAME,CallItem.AMOUNT,CallItem.DATE,CallItem.DURATION},
			    		 	new int[]{R.id.callName, R.id.callAmount, R.id.callDate,R.id.callDuration});
			     callsViewList.setAdapter(directions);
			}
		}
	}
}
