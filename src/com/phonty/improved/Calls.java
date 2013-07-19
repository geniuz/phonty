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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.phonty.improved.PhontyActivity.ContactItem;

import android.content.Context;
import android.util.Log;

public class Calls {
	private static String APIURL;
	static ArrayList<CallItem> VALUES;
	HttpPost httppost;
	DefaultHttpClient client;
	static Context context; 
	String type;
	
	public Calls(String url, Context _context) {
		context = _context;
		APIURL = url;
		httppost = new HttpPost(APIURL);
        httppost.addHeader("Content-Type", "application/json; charset=\"utf-8\"");
        BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 4711));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new PhontyHttpClient(cm, params,context);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Phonty-Android-Client");
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(Login.SESSION_COOKIE); 
		client.setCookieStore(cookieStore);
		VALUES = new ArrayList<CallItem>();
		type = "Calls:";

	}
	

	  public class CallItem extends HashMap<String,String> {
		  private static final long serialVersionUID = 1L;
		  public static final String NAME = "name";
		  public static final String AMOUNT = "amount";	
		  public static final String DATE = "date";
		  public static final String DURATION = "duration";	
		  
		  public CallItem (String name,String amount,String date, String duration) {
			  super();
			  super.put(NAME, name);
			  super.put(AMOUNT, amount);
			  super.put(DATE, date);
			  super.put(DURATION, duration);

		  }
	  }
	
	private ArrayList<CallItem> parse(String response) {
		try {
 			 JSONArray jsonArray = new JSONArray(response.toString());
			 for (int i = 0; i < jsonArray.length(); ++i) {
				    JSONObject rec = jsonArray.getJSONObject(i);
				    VALUES.add(new CallItem(rec.getString("name"),rec.getString("amount"),rec.getString("date"),rec.getString("duration")));			    
				}
			 Log.e("RESPONSE",jsonArray.length()+"");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return VALUES;
	}
	
	public void getsms(String country,String url) {
		APIURL = url;
		httppost = new HttpPost(APIURL);
        httppost.addHeader("Content-Type", "application/json; charset=\"utf-8\"");
        BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 4711));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new PhontyHttpClient(cm, params,context);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Phonty-Android-Client");
		type="SMS:";
		get(country);
	}
	
	public boolean get(String country) {
		Log.e("RESPONSE","Start getting");
		String line = null;
		StringBuilder builder = new StringBuilder();
		try {
			String locale = context.getResources().getConfiguration().locale.getCountry();
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
 			nvps.add(new BasicNameValuePair("locale", locale));
			 
			httppost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
			HttpResponse response = client.execute(httppost);		
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					Log.e("RESPONSE",line);
					if (parse(line)!=null)
						return true;
					else return false;
				}
			} else {
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}