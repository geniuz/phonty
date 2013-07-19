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


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import com.phonty.improved.Balance;
import com.phonty.improved.DirectionCost;
import com.phonty.improved.R;
import com.phonty.improved.ServiceInterface;

public class PhontyService extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.phonty.test.displayevent";
	private final Handler handler = new Handler();
	private static String directionPhone = null;

	Intent intent;
	int counter = 0;
	private Boolean taskIsRunning = false;
	public SharedPreferences prefs;

	enum Tasks { BALANCE, DIRECTIONCOST }
	    
	@Override
	public IBinder onBind(Intent arg0) {
		return serviceBinder;
	}

    @Override
    public void onCreate() {
        super.onCreate();
    	intent = new Intent(BROADCAST_ACTION);
    }
    
    @Override
    public void onStart(Intent intent, int startId) {

       // ниже - как изменять активити из сервиса
       prefs = PreferenceManager.getDefaultSharedPreferences(this);
       handler.removeCallbacks(sendUpdatesToUI);
       handler.postDelayed(sendUpdatesToUI, 0); // 1 second

    }
    
	private final ServiceInterface.Stub serviceBinder = new ServiceInterface.Stub() {
		@Override
		public void refreshBalance() throws RemoteException {
			PhontyService.this.refreshBalance();
		}

		@Override
		public void refreshDirectionCost(String phone) throws RemoteException {
			PhontyService.this.refreshDirectionCost(phone);
		}
	};
    
	private void refreshBalance() {
		Tasks action = Tasks.BALANCE;
		PhontyParams task = new PhontyParams();
		task.execute(action);
	}
	
	private void refreshDirectionCost(String phone) {
		Tasks action = Tasks.DIRECTIONCOST;
		PhontyParams task = new PhontyParams();
		directionPhone = phone;
		task.execute(action);
	}
	
    private Runnable sendUpdatesToUI = new Runnable() {
    	private PhontyParams task;
    	public void run() {
    		Tasks action = Tasks.BALANCE;
    	    task = new PhontyParams();
    	    task.execute(action);
   	    //handler.postDelayed(this, 5000); // 5 seconds
    	}
    };
    
    public static class Params {
    	public static String balance = "0";
    	public static String directionCost = "";    
    	public static String bookProgress = "";        	
    }
    
	class PhontyParams extends AsyncTask<Tasks, String, String> {
		private Balance balance;
		private DirectionCost directionCost;
		protected String doInBackground(Tasks... action) {
			if (taskIsRunning) { this.cancel(true); return null; }
			taskIsRunning = true;
			switch(action[0]) {
			case BALANCE:
				balance = new Balance( getResources().getText(R.string.balance_url).toString(),getApplicationContext());
				balance.get();
				Params.balance = balance.VALUE;
				//return null;
				break;
			case DIRECTIONCOST:
				directionCost = new DirectionCost( getResources().getText(R.string.direction_cost_url).toString(),getApplicationContext());
				directionCost.get(directionPhone);
				Params.directionCost = directionCost.VALUE;
				break;
				//return null;
			}
			return null;
			//throw new IllegalArgumentException();
		}

		protected void onProgressUpdate(Integer... progress) {

		}

		protected void onPostExecute(String result) {
			taskIsRunning = false;

	    	SharedPreferences.Editor editor = PhontyService.this.prefs.edit();
	    	editor.putString(getString(R.string.balance), Params.balance);
	    	editor.commit();
	    	
	    	intent.putExtra("balance", Params.balance);
	    	intent.putExtra("directionCost", Params.directionCost);
	    	sendBroadcast(intent);
	    	
		}
	}
	
}
