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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSmsActivity extends Activity {
	private Button processButton,cancelButton;
	private EditText phoneEdit,messageEdit;
	String phone;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 Bundle extras = getIntent().getExtras();
		 if(extras !=null)
		 {
			phone = extras.getString("phone");
		 }
		 
	     setContentView(R.layout.sms_form);
	     
	     processButton = (Button)findViewById(R.id.process_sms);
	     phoneEdit = (EditText)findViewById(R.id.smsPhoneEdit);
	     messageEdit = (EditText)findViewById(R.id.smsMessageEdit);
	     cancelButton = (Button)findViewById(R.id.cancel_sms_form);
	     
	     phoneEdit.setText(phone);
	     
	     cancelButton.setOnClickListener(new OnClickListener(){
	      @Override
	      public void onClick(View arg0) {
	    	  finish();
	      	}
	      });
	     
	     processButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SendSmsTask send_task	= new SendSmsTask();
				send_task.execute();
			}
	     });
	 }
	 
	 /* Add contact task */
	 class SendSmsTask extends AsyncTask<String, Integer, Boolean> {
		    Sms sms; 
		 	ProgressDialog progDialog;
			   
			  @Override
			  protected void onPreExecute() {
				  progDialog = new ProgressDialog(SendSmsActivity.this);
	              progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	              progDialog.setMessage(getString(R.string.processing));
	              progDialog.show();
	          }
		 	
			protected Boolean doInBackground(String... stub) {
           	
				sms = new Sms(getString(R.string.sms_send_url),getApplicationContext());
				String phone = phoneEdit.getText().toString();
				String message = messageEdit.getText().toString();
				
				if (sms.send(phone,message)) return true;
				else return false;
			}
		protected void onProgressUpdate(Integer... progress) {}
	    protected void onPostExecute(Boolean result) {
	    	progDialog.dismiss();
	    	if (result) finish();
			else if (sms.STATUS.equals("SE")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedSE).toString(), 15).show();
			}
			else if (sms.STATUS.equals("NM")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedNM).toString(), 15).show();
			}
			else if (sms.STATUS.equals("WN")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedWN).toString(), 15).show();
			}
			else if (sms.STATUS.equals("ND")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedND).toString(), 15).show();
			}
	    	else Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedSE).toString(), 15).show();
	    }
	 }	            		
	
	 @Override
	 public void onResume() {
		 super.onResume();
	 }
	
	 @Override
	 public void onPause() {
		 super.onPause();
	 }
}
