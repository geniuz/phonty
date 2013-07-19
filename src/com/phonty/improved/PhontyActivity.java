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
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.RegisterService;

import com.phonty.improved.DialerFragment.OnFragmentInteractionListener;
import com.phonty.improved.R;
import com.phonty.improved.ServiceInterface;

import org.holoeverywhere.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PhontyActivity extends Activity implements OnFragmentInteractionListener{
	private static final int ABOUT_MENU_ITEM = 0;
	private static final int EXIT_MENU_ITEM = 1;
	private static final int CALLS_MENU_ITEM = 4;
	private static final int PRICE_MENU_ITEM = 2;
	private static final int NEW_CONTACT_ITEM = 3;
	private static final int PAY_MENU_ITEM = 5;
	//private static final int REFRESH_BALANCE_ITEM = 4;
	private Intent intent;
	private Bundle mThreadArgs;
	private Message mThreadMsg;
	private volatile Looper serviceLooper;
	private volatile ServiceHandler serviceHandler;
	public SharedPreferences prefs;
	ServiceInterface bgService;
	TextView balanceLabel;
	TextView directionCostLabel;
	ImageButton callButton;
    EditText phoneEdit; 
    ListView contactsList; 
    DownloadContactsTask task;
    View phoneLayout;
    ProgressBar directionProgress;
	private static AlertDialog alertDlg;
 	ListAdapter contactsListAdapter;

  
 	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event)  {
 	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
 	        // do something on back.
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);

 	        return true;
 	    }

 	    return super.onKeyDown(keyCode, event);
 	}
 	
	@Override
	public void onStart(){
		super.onStart();


		if ( !Login.isConnected ) {	   
				task = new DownloadContactsTask();
				task.execute("http://phonty.com/japi/contacts/");
				Login.isConnected = true;
	    	}
		else fill_сontacts();
	}
	
    /** Called when the main activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Login.SESSION_COOKIE == null ) {
            setVisible(false);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags( Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        
        else {
			Receiver.engine(this).registerMore();
    		Receiver.engine(getApplicationContext()).updateDNS();
	        setContentView(R.layout.main);
	        intent = new Intent(this, PhontyService.class);
			startService(intent);
			
	    	balanceLabel = (TextView) findViewById(R.id.balance);  	
	    	directionCostLabel = (TextView) findViewById(R.id.directionCostLabel);
	    	phoneEdit = (EditText)findViewById(R.id.phone);
	    	callButton = (ImageButton)findViewById(R.id.callButton);
	    	contactsList = (ListView)this.findViewById(R.id.contactsList);
	    	contactsList.setCacheColorHint(Color.TRANSPARENT);

	    	final TextView tip = (TextView)findViewById(R.id.empty);
	    	contactsList.setEmptyView(tip);

	    	
	    	phoneLayout = (View)findViewById(R.id.phoneLayout);
	    	getWindow().setFormat(PixelFormat.RGBA_8888);
	    	phoneLayout.getBackground().setDither(true);
	    	directionProgress = (ProgressBar)findViewById(R.id.directionProgress);
	    	directionProgress.setVisibility(View.GONE);
	    	
	    	phoneEdit.addTextChangedListener(new TextWatcher(){
	    			/* Fetching direction cost by clicking on contact item at ListView */ 
					@Override
					public void afterTextChanged(Editable phone) {
	    	            if (phone.length()==8) {
	    	            	directionProgress.setVisibility(View.VISIBLE);
	    	            	async_action("refreshDirectionCost",phone.toString());
	    	            }
	    	            if (phone.length()<3) {directionCostLabel.setText("");}
					}
	    	        public void beforeTextChanged(CharSequence phone, int start, int count, int after){}
	    	        public void onTextChanged(CharSequence phone, int start, int before, int count){
	    	            if (phone.length() == 8) {
	    	            }        	
	    	        }
	    	    }); 
	    	
	    	/* Inserting phone number to phone field by clicking on contact item at ContactsList */
	    	contactsList.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				 ContactItem item =(ContactItem) contactsList.getItemAtPosition(position);
				 phoneEdit.setText(item.get("phone"));
				 directionProgress.setVisibility(View.VISIBLE);
	             async_action("refreshDirectionCost",item.get("phone"));
				}    		
	    	});
	    	
	    	contactsList.setOnCreateContextMenuListener(this);
	    	
	    	/* Making the call */
	    	callButton.setOnClickListener(new OnClickListener(){
	    		@Override
				public void onClick(View arg0) {
	    			async_action("refreshBalance",null);
					call_menu();
				}
	    	});
	    	
	    	/* Sending the sms  */
	    	callButton.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View arg0) {
	            	intent = new Intent(PhontyActivity.this,SendSmsActivity.class);
	            	intent.putExtra("phone", phoneEdit.getText().toString());
	    	    	startActivity(intent);
	                return true;
 				}
	    	});
	    	
        } // end of else	
    } // end of onCreate
    
    /* Contact context menu actions */
	//@Override
	/*public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	        AdapterContextMenuInfo aMenuInfo = (AdapterContextMenuInfo) menuInfo;
	        final ContactItem data = (ContactItem) contactsListAdapter.getItem(aMenuInfo.position);
	        menu.setHeaderTitle(getString(R.string.contact_actions));
	        
	        menu.add(getString(R.string.call)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
	            @Override
	            public boolean onMenuItemClick(MenuItem item) {
					phoneEdit.setText(data.get(ContactItem.PHONE));
	            	call_menu();
	                return true;
	            }
	        });
	        
	        menu.add(getString(R.string.sendsms)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
	            @Override
	            public boolean onMenuItemClick(MenuItem item) {
	            	intent = new Intent(PhontyActivity.this,SendSmsActivity.class);
	            	intent.putExtra("phone", data.get(ContactItem.PHONE));
	    	    	startActivity(intent);
	                return true;
	            }
	        });
	        
	        menu.add(getString(R.string.delete)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
	            @Override
	            public boolean onMenuItemClick(MenuItem item) {
	            	AlertDialog.Builder builder = new AlertDialog.Builder(PhontyActivity.this);
	            	builder.setMessage(getString(R.string.sure));
	            	builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DeleteContactTask deleteTask = new DeleteContactTask();
			            	deleteTask.execute(data.get(ContactItem.ID));
			            	dialog.dismiss();
						}
					});	            	
	            	builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});	        
	            	AlertDialog alert  = builder.create();
	            	alert.show();
	            	return true;
	            }
	        });
	        
	        menu.add(getString(R.string.edit)).setOnMenuItemClickListener(new OnMenuItemClickListener() {
	            @Override
	            public boolean onMenuItemClick(MenuItem item) {
	            	intent = new Intent(PhontyActivity.this,EditContactActivity.class);
	            	intent.putExtra("id", data.get(ContactItem.ID));
	            	intent.putExtra("phone", data.get(ContactItem.PHONE));
	    	    	intent.putExtra("name", data.get(ContactItem.NAME));
	    	    	startActivity(intent);
	                return true;
	            }
	        });
	}*/
    
    @Override
	public void onResume() {
    	super.onResume();
		if (!PhontyActivity.this.isOnline()) {
			Login.logout();
			Receiver.pos(true);
			Receiver.engine(this).halt();
			Receiver.mSipdroidEngine = null;
			Receiver.reRegister(0);
			stopService(new Intent(this,RegisterService.class));
			stopService(new Intent(this,PhontyService.class));
			finish();
		}
    	
        bindService(new Intent(this, PhontyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
		registerReceiver(broadcastReceiver, new IntentFilter(PhontyService.BROADCAST_ACTION));
		PreferenceManager.getDefaultSharedPreferences(this);
		Handler handler = new Handler();
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	    			async_action("refreshBalance",null);
	         } 
	    }, 3000); 
		/*balancePref = prefs.getString(getString(R.string.balance),"0.0");
		if ( balancePref != null) {
			balanceLabel.setText(balancePref);
		}*/
	}
    
	@Override
	public void onPause() {
		super.onPause();
		unbindService(serviceConnection);
		unregisterReceiver(broadcastReceiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        intent = new Intent(this, PhontyService.class);
		stopService(intent); 
	}
	
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }
    };
    
    private ServiceConnection serviceConnection = new ServiceConnection()
    {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
 			bgService = ServiceInterface.Stub.asInterface(arg1);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
	        bgService = null;
		}
    };
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper){
			super(looper);
		}
	
		public void handleMessage(Message msg){

		Bundle args = (Bundle)msg.obj;
			if (args.containsKey("refreshBalance")) {
					RemoteRefreshBalance();
				}
			else if (args.containsKey("refreshDirectionCost")) {
				RemoteRefreshDirectionCost(args.getString("phone"));
			}
		}
	}
	
	private void RemoteRefreshBalance() {
		try {
			 bgService.refreshBalance();
			}
		catch (RemoteException e) {}
	}
	
	private void RemoteRefreshDirectionCost(String phone) {
		try {
			 bgService.refreshDirectionCost(phone);
			}
		catch (RemoteException e) {}
	}
	
    private void updateUI(Intent intent) {
    	String balance = intent.getStringExtra("balance"); 
    	String directionCost = intent.getStringExtra("directionCost");
    	balanceLabel.setText(balance);
    	directionCostLabel.setText(directionCost);
    	directionProgress.setVisibility(View.GONE);
    }
    

	/* Remote call for async refreshing balance or get dir.cost */
	private void async_action(String action, String option){
        HandlerThread hthr = new HandlerThread("StartedSumLooperService2Thread", Process.THREAD_PRIORITY_BACKGROUND);
		hthr.start();
		serviceLooper = hthr.getLooper();
		serviceHandler = new ServiceHandler(serviceLooper);
		mThreadArgs = new Bundle();
		mThreadMsg = serviceHandler.obtainMessage();
		mThreadArgs.clear();
		if (action == "refreshBalance") {
			mThreadArgs.putBoolean("refreshBalance",true);
		} else if (action == "refreshDirectionCost") {
			mThreadArgs.putBoolean("refreshDirectionCost",true);
        	mThreadArgs.putString("phone",option);
		}
		mThreadMsg.obj = mThreadArgs;
		serviceHandler.sendMessage(mThreadMsg);	
	}
    
    /* =========================================
     * ===== WORK WITH CONTACTS ================
     * ========================================= */
    
    /* Geting contacts */
	  public class ContactItem extends HashMap<String,String> {
		  private static final long serialVersionUID = 1L;
		  public static final String ID = "id";
		  public static final String NAME = "name";
		  public static final String PHONE = "phone";		  
		  public ContactItem (String id,String name,String phone) {
			  super();
			  super.put(ID, id);
			  super.put(NAME, name);
			  super.put(PHONE, phone);

		  }
	  }
    
	  private void fill_сontacts() {
		   /* Getting data from DB */
 	       ContactsDbHelper dbHelper = new ContactsDbHelper(getApplicationContext());
		   SQLiteDatabase db = dbHelper.getReadableDatabase();
		   ArrayList<ContactItem> contactsList = new ArrayList<ContactItem>();
		   
		   Cursor cursor=db.rawQuery("SELECT " + 
				   ContactsDbHelper.ID +", " +
				   ContactsDbHelper.NAME +", " +
				   ContactsDbHelper.PHONE + 
				   " FROM " + ContactsDbHelper.TABLE_NAME,null);
		   
		   while (cursor.isAfterLast() == false) {
			   	try {
				cursor.moveToNext();
 				String id=cursor.getString(0);
 				String name=cursor.getString(1);
				String phone=cursor.getString(2);
  				contactsList.add(new ContactItem(id,name,phone));
			   	}
			   	catch (android.database.CursorIndexOutOfBoundsException e) {
			   		break;}
		   }
		   cursor.close();
		   db.close();
		   
		   /* Filling list view */
		   contactsListAdapter = new SimpleAdapter(PhontyActivity.this,contactsList, R.layout.contact_view,
					new String[] {ContactItem.ID,ContactItem.NAME,ContactItem.PHONE },
					new int[] { R.id.contactId, R.id.contactName, R.id.contactPhone });
			
		   ListView contactListView = (ListView)findViewById(R.id.contactsList);
		   contactListView.setAdapter(contactsListAdapter);
	  } 
	  
	  
	  
    /*  Getting contacts as file from main server,
     *  and store it in local DB 
     *  while application is running. */
	  
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
	  
	  /* GETTING CONTACTS FROM SERVER AS JSON TEXT FILE with progress bar */
	  try {

	     conn = (HttpURLConnection) new URL(getResources().getText(R.string.contacts_url).toString()).openConnection();
	     conn.setRequestProperty("Cookie", "sessionid="+ Login.SESSION_ID);
	     conn.setRequestProperty("User-agent", "Phonty-Android-Client"); 
	     
	     fileSize = conn.getContentLength();

	     out = new ByteArrayOutputStream((int) fileSize);
	     conn.connect();
	 
	     stream = conn.getInputStream();

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

	      out.write(buffer, 0, read);
	      downloaded += read;

	      publishProgress((int) ((downloaded / fileSize) * 100));
	     }
	 
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
	   }
	  }
	    
	  @Override
	  protected void onProgressUpdate(Integer... changed) {
	   progressDialog.setProgress(changed[0]);
	     }
	   
	  @Override
	  protected void onPreExecute() {
	   progressDialog = new ProgressDialog(PhontyActivity.this);
	   progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	   progressDialog.setMessage(getString(R.string.gettingContacts));
	   progressDialog.setCancelable(false);
	   progressDialog.show();
	  }
	  
	  @Override
	  protected void onPostExecute(String result) {
	   if(result != null){
	   progressDialog.dismiss();
       ContactsDbHelper dbHelper = new ContactsDbHelper(getApplicationContext());
	   SQLiteDatabase db = dbHelper.getWritableDatabase();
	   
		try {
			JSONArray jsonArray = new JSONArray(result.toString());
		    for (int i = 0; i < jsonArray.length(); i++) {
			   JSONObject jsonObject = jsonArray.getJSONObject(i);
			   /* Saving to db */
			   ContentValues dbV = new ContentValues();
			   dbV.put(ContactsDbHelper.ID,jsonObject.getString("id"));
			   dbV.put(ContactsDbHelper.NAME,jsonObject.getString("name"));
			   dbV.put(ContactsDbHelper.PHONE,jsonObject.getString("phone"));
			   db.insert(ContactsDbHelper.TABLE_NAME,null,dbV);
			 }

	    db.close();
	    fill_сontacts();
		
		} catch (JSONException e) {
		}
				
	   }
	   else {
	    progressDialog.dismiss();
	    AlertDialog alertDialog;
	    alertDialog = new AlertDialog.Builder(PhontyActivity.this).create();
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

	 
	 /* Delete contact */
	 class DeleteContactTask extends AsyncTask<String, Integer, Boolean> {
		 	ProgressDialog progDialog;
			   
			  @Override
			  protected void onPreExecute() {
				  progDialog = new ProgressDialog(PhontyActivity.this);
	              progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	              progDialog.setMessage(getString(R.string.processing));
	              progDialog.show();
	          }
		 	
			protected Boolean doInBackground(String... contact_id) {
            	
            	
				Contacts contacts = new Contacts(getString(R.string.contact_delete_url),getApplicationContext());
				if (contacts.delete(contact_id[0])) {
					ContactsDbHelper contacts_db = new ContactsDbHelper(getApplicationContext());
					contacts_db.delete(contact_id[0]);
					return true;
				}
				else
					return false;
			}
		protected void onProgressUpdate(Integer... progress) {}
 	    protected void onPostExecute(Boolean result) {
 	    	if (result) PhontyActivity.this.fill_сontacts();
 	    	progDialog.dismiss();
 	    }
 	 }	            		
	 
	 
	 
	 	/* This void used by callButton click listener */
		void call_menu()
		{
			String target = phoneEdit.getText().toString().replace("-", "").replace("(","").replace(")", "").replace("+","");
			if (alertDlg != null) 
			{
				alertDlg.cancel();
			}
			if (target.length() == 0)
				alertDlg = new AlertDialog.Builder(this)
					.setMessage(R.string.empty)
					.setTitle(R.string.app_name)
					.setIcon(R.drawable.icon22)
					.setCancelable(true)
					.show();
			else if (!Receiver.engine(this).call(target,true))
				alertDlg = new AlertDialog.Builder(this)
					.setMessage(R.string.notfast)
					.setTitle(R.string.app_name)
					.setIcon(R.drawable.icon22)
					.setCancelable(true)
					.show();
		}

		/* MENU */
		/*@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			boolean result = super.onCreateOptionsMenu(menu);

			MenuItem m = menu.add(0, NEW_CONTACT_ITEM, 0, R.string.new_contact);
			m.setIcon(android.R.drawable.ic_menu_add);
			
			/*m = menu.add(0, REFRESH_BALANCE_ITEM, 0, R.string.refresh_balance);
			m.setIcon(android.R.drawable.ic_menu_rotate);*/

			/*m = menu.add(0, CALLS_MENU_ITEM, 0, R.string.calllog);
			m.setIcon(android.R.drawable.ic_menu_today);

			m = menu.add(0, ABOUT_MENU_ITEM, 0, R.string.menu_about);
			m.setIcon(android.R.drawable.ic_menu_info_details);

			m = menu.add(0, PRICE_MENU_ITEM, 0, R.string.menu_price);
			m.setIcon(android.R.drawable.ic_menu_mapmode);

			m = menu.add(0, PAY_MENU_ITEM, 0, R.string.menu_pay);
			m.setIcon(android.R.drawable.ic_menu_agenda);
			
			m = menu.add(0, EXIT_MENU_ITEM, 0, R.string.menu_exit);
			m.setIcon(android.R.drawable.ic_menu_close_clear_cancel);			
			return result;
		}*/
		
		//@Override
		/*public boolean onOptionsItemSelected(MenuItem item) {
			boolean result = super.onOptionsItemSelected(item);
			Intent intent = null;

			switch (item.getItemId()) {
			case ABOUT_MENU_ITEM:
				if (alertDlg != null) 
				{
					alertDlg.cancel();
				}
				alertDlg = new AlertDialog.Builder(this)
				.setMessage(getString(R.string.about).replace("\\n","\n").replace("${VERSION}","10"))
				.setTitle(getString(R.string.menu_about))
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();
				break;
				
			case NEW_CONTACT_ITEM:
				intent = new Intent(PhontyActivity.this, AddContactActivity.class);
				startActivity(intent);
				break;
				
			case PAY_MENU_ITEM:
				intent = new Intent(PhontyActivity.this, PaymentActivity.class);
				startActivity(intent);
				break;
				
			case EXIT_MENU_ITEM: 
				//on(this,false);
				Login.logout();

				Receiver.pos(true);
				Receiver.engine(this).halt();
				Receiver.mSipdroidEngine = null;
				Receiver.reRegister(0);
				stopService(new Intent(this,RegisterService.class));
				stopService(new Intent(this,PhontyService.class));
				finish();
				break;
				
			/*case REFRESH_BALANCE_ITEM: 
    			async_action("refreshBalance",null);
				break;*/
				
			/*case CALLS_MENU_ITEM:
				intent = new Intent(PhontyActivity.this, CallsActivity.class);
				startActivity(intent);
				break;
				
			case PRICE_MENU_ITEM: {
				try {
					intent = new Intent(this, PriceActivity.class);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
				}
			}
				break;
			}

			return result;
		}*/
		
	public boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo nInfo = cm.getActiveNetworkInfo();
	    if (nInfo != null && nInfo.isConnected()) {
	        return true;
	    }
	    else {
	        return false;
	    }
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		
	}
}