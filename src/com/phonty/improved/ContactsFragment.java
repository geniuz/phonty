package com.phonty.improved;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
 
 
public class ContactsFragment extends Fragment {
	private View mView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {    	    	
    	
    	Log.e("1111111111111","111111111111111");
    	mView = inflater.inflate(R.layout.fragment_contacts); 
        return mView;
     }
    
}
