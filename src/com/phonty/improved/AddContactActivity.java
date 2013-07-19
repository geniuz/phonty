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

public class AddContactActivity extends Activity {
	private Button processButton,cancelButton;
	private EditText phoneEdit,nameEdit;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.contact_form);
	     
	     processButton = (Button)findViewById(R.id.process_contact);
	     phoneEdit = (EditText)findViewById(R.id.contactPhoneEdit);
	     nameEdit = (EditText)findViewById(R.id.contactNameEdit);
	     cancelButton = (Button)findViewById(R.id.cancel_contact_form);
	     
	     cancelButton.setOnClickListener(new OnClickListener(){
	      @Override
	      public void onClick(View arg0) {
	    	  finish();
	      	}
	      });
	     
	     processButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AddContactTask add_task	= new AddContactTask();
				add_task.execute(phoneEdit.getText().toString(),nameEdit.getText().toString());
			}
	     });
	 }
	 
	 /* Add contact task */
	 class AddContactTask extends AsyncTask<String, Integer, Boolean> {
		 	ProgressDialog progDialog;
			   
			  @Override
			  protected void onPreExecute() {
				  progDialog = new ProgressDialog(AddContactActivity.this);
	              progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	              progDialog.setMessage(getString(R.string.processing));
	              progDialog.show();
	          }
		 	
			protected Boolean doInBackground(String... number_name) {           	
				Contacts contacts = new Contacts(getString(R.string.contact_add_url),getApplicationContext());
				if (contacts.add(number_name[0],number_name[1])) {
					ContactsDbHelper contacts_db = new ContactsDbHelper(getApplicationContext());
					contacts_db.add(contacts.value(),number_name[1],number_name[0]);
					return true;
				}
				else
					return false;
			}
		protected void onProgressUpdate(Integer... progress) {}
	    protected void onPostExecute(Boolean result) {
	    	progDialog.dismiss();
	    	if (result) finish();
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
