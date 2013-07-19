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


import com.phonty.improved.PhontyService.Tasks;
import com.phonty.improved.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private Button cancelButton;
	private Button processRegistrationButton;
    private ProgressDialog registrationProgress;
    private EditText registrationEdit;
	private Boolean taskIsRunning = false;
	private Registration task;
	private String phone;
	Intent intent;
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.register);
	     
	     processRegistrationButton = (Button)findViewById(R.id.processRegistration);
	     registrationEdit = (EditText)findViewById(R.id.registrationEdit);
	     cancelButton = (Button)findViewById(R.id.cancel);
	     
	     cancelButton.setOnClickListener(new OnClickListener(){
	      @Override
	      public void onClick(View arg0) {
	    	  finish();
	      	}
	      });
    
         processRegistrationButton.setEnabled(false);
	     processRegistrationButton.setOnClickListener(new OnClickListener(){
	      @Override
	      public void onClick(View arg0) {
	    	  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
	    	  SharedPreferences.Editor editor = prefs.edit();

	    	  phone = registrationEdit.getText().toString();
	    	  editor.putString(getString(R.string.login), phone);
	    	  editor.commit();
	    	  
	    	  registrationProgress = ProgressDialog.show(RegisterActivity.this, getString(R.string.newAccount), getString(R.string.newAccountTip), true,false);
              task = new Registration();
              task.execute();
	      	}
	      });
	     

	     registrationEdit.addTextChangedListener(new TextWatcher(){
		  @Override
		  public void afterTextChanged(Editable arg0) {
	        if (arg0.length()>3) {
	        	processRegistrationButton.setEnabled(true);
	        }
	        if (arg0.length()<3) {
	        	processRegistrationButton.setEnabled(false);
	        }
	        phone = arg0.toString();
	      }
    	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	        public void onTextChanged(CharSequence s, int start, int before, int count){}
    	  }); 
	 }
	 
	 @Override
	 public void onResume() {
		 super.onResume();
	 }
	
	 @Override
	 public void onPause() {
		 super.onPause();
	 }

	 class Registration extends AsyncTask<Tasks, String, Boolean>  {
		protected Boolean doInBackground(Tasks... action) {
			if (taskIsRunning) { this.cancel(true); return null; }
			taskIsRunning = true;
 			Register registration = new Register(getResources().getText(R.string.register_url).toString(),getApplicationContext());
 			  return registration.process(phone);
		}
		protected void onProgressUpdate(Integer... progress) {}
		protected void onPostExecute(Boolean result) {
			taskIsRunning = false;
			registrationProgress.dismiss();
			if (result) {
				  finish();
		    	  intent = new Intent();
			      intent.setClass(RegisterActivity.this,SuccessRegistrationActivity.class);
		    	  startActivity(intent);
			}
			else if (Register.STATUS.equals("AE")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedAE).toString(), 15).show();
			}
			else if (Register.STATUS.equals("TU")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedTU).toString(), 15).show();
			}
			else if (Register.STATUS.equals("NR")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedNR).toString(), 15).show();
			}
			else if (Register.STATUS.equals("UE")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedUE).toString(), 15).show();
			}
			else if (Register.STATUS.equals("SE")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedSE).toString(), 15).show();
			}
			else if (Register.STATUS.equals("ND")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedND).toString(), 15).show();
			}
			else if (Register.STATUS.equals("TF")) {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.failedTF).toString(), 15).show();
			}
			else {
				Toast.makeText(getApplicationContext(),getResources().getText(R.string.regfailed).toString(), 15).show();
			};
		}
	}
}
