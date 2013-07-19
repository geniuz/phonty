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
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
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
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class DirectionCost {
	private static String APIURL;
	String VALUE = "0";
	static HttpPost httppost;
	static DefaultHttpClient client;
	static Context context;
	
	public DirectionCost(String url,Context _context) {
		context = _context;
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 4711));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new PhontyHttpClient(cm, params,context);
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(Login.SESSION_COOKIE); 
		client.setCookieStore(cookieStore);
		APIURL = url;
		httppost = new HttpPost(APIURL);
	}

	private static String Parse(String response) {
		String value = "";
		try {
			 JSONObject jsonObject = new JSONObject(response.toString());
			 value = jsonObject.getString("amount")+" ";
			 value += jsonObject.getString("provider");
			 value += "("+jsonObject.getString("country")+")";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public String get(String phone) {
		StringBuilder builder = new StringBuilder();
		String value = "0";
	    
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Phonty-Android-Client");
		httppost = new HttpPost(APIURL);
        httppost.addHeader("Content-Type", "application/json; charset=\"utf-8\"");

		try {
			String locale = context.getResources().getConfiguration().locale.getCountry();
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("phone", phone));
			nvps.add(new BasicNameValuePair("locale", locale));
			 
			httppost.setEntity(new UrlEncodedFormEntity(nvps));
			
			HttpResponse response = client.execute(httppost);		
			
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					value = Parse(line);
					this.VALUE = value;
				}
			} else {
				this.VALUE = "0.0";				
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			this.VALUE = "Protocol exception";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.VALUE = "I.O. Exception";				
			e.printStackTrace();
		}
		
		return value;
	}
	
	public String value() {
		return this.VALUE;
	}
}