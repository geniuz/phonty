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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;

public class Login {
	private static String APIURL;
	String VALUE = "0";
 	static String login = "";
	static String password = "";
	HttpPost httppost;
	PhontyHttpClient client;
	public static Cookie SESSION_COOKIE = null;
	public static String SESSION_ID = null;
	public static Boolean isConnected = false;
	
	public Login(String _login, String _password, String url,Context context) {
		APIURL = url;
		login = _login;
		password = _password;
		BasicHttpParams params = new BasicHttpParams();
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("https", sslSocketFactory, 4711));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		client = new PhontyHttpClient(cm, params,context);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Phonty-Android-Client");
	}
	
	public boolean process() {
		StringBuilder builder = new StringBuilder();
		final String cookieName = "sessionid"; 
		httppost = new HttpPost(APIURL);

		try {
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("username", login));
			nvps.add(new BasicNameValuePair("password", password));
			 
			httppost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
			HttpResponse response = client.execute(httppost);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if (statusCode == 200) {
				List<Cookie> cookies = client.getCookieStore().getCookies();
				if (cookies.isEmpty()) {
			        } else {
			            for (int i = 0; i < cookies.size(); i++) {
			               if (cookieName.equals(cookies.get(i).getName())) {
			            	   // We get the session id. That what auth is.
				               SESSION_COOKIE = cookies.get(i);
				               SESSION_ID = cookies.get(i).getValue();
			            	 }
			            }
			        }
			        
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
					this.VALUE = line;
				}
			} else {
				this.VALUE = "0.0";				
 			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		if (this.VALUE.equals("AUTH_OK")) {
			return true;
		}
		else {
			logout();
			return false;
		}
	}
	
	public String value() {
		return this.VALUE;
	}

	public static void logout() {
		isConnected = false;
		SESSION_COOKIE = null;
		SESSION_ID = null;
	}
}