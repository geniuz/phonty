package com.phonty.improved;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.phonty.improved.R;

import org.holoeverywhere.LayoutInflater;

import android.support.v4.app.Fragment;
import org.holoeverywhere.app.TabSwipeFragment;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class TabsFragment extends TabSwipeFragment {
 		  @Override
		public void onHandleTabs() {
		    addTab("Dialer", HIstoryFragment.class, new Bundle());		
		    addTab("Contacts", ContactsFragment.class, new Bundle());		   
		  }
 }
