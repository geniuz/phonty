package com.phonty.improved;

import java.util.ArrayList;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import com.phonty.improved.Calls.CallItem;
import com.phonty.improved.CallsActivity.CallsTask;
import com.phonty.improved.PhontyService.Tasks;
  
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class HIstoryFragment extends Fragment {
	private View mView;
	private ListView callsViewList;
    private Boolean taskIsRunning = false;
	private CallsTask task;
	private String country = "ru";
	private TextView tip;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {    	    	
    	Log.e("1111111111111","111111111111111");
    	mView = inflater.inflate(R.layout.fragment_history); 
        return mView;
     }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	    /*tip = (TextView) mView.findViewById(R.id.empty);
   	    callsViewList.setEmptyView(tip);*/
	    callsViewList = (ListView) mView.findViewById(R.id.callsList);
        task = new CallsTask();
        task.execute();
         
    	Log.e("2122222222222","222222222222222");
        super.onViewCreated(view, savedInstanceState);
    }
    
    class CallsTask extends AsyncTask<Tasks, String, Boolean>  {
		ArrayList<CallItem> callsList;
		boolean result,smsresult;
		
		protected Boolean doInBackground(Tasks... action) {
			if (taskIsRunning) { this.cancel(true); return null; }
			taskIsRunning = true;
 			Calls calls = new Calls(getResources().getText(R.string.calls_url).toString(),getActivity());
			result = calls.get(country);
 			callsList = calls.VALUES; 			 			
 			return result;
		}
		protected void onProgressUpdate(Integer... progress) {}
		protected void onPostExecute(Boolean result) {
			taskIsRunning = false;
			Log.e("ARRAY",callsList.toString());
			if (result) {
			     ListAdapter directions = new SimpleAdapter(getActivity(),
			    		 	callsList,
			    		 	R.layout.calls_view,
			    		 	new String[] {CallItem.NAME,CallItem.AMOUNT,CallItem.DATE,CallItem.DURATION},
			    		 	new int[]{R.id.callName, R.id.callAmount, R.id.callDate,R.id.callDuration});
			     callsViewList.setAdapter(directions);
			}
		}
    }
}
