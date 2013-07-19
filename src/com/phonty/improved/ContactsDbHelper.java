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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactsDbHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "phonty";

	public static final String TABLE_NAME = "contacts";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String PHONE = "phone";
	
	public ContactsDbHelper(Context context) {
	    super(context, DB_NAME, null,DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + 
		" (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + ID + " INTEGER , " + NAME + " TEXT , " + PHONE + " TEXT);");
	}
	
	public void clean() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DELETE FROM "+TABLE_NAME+";");
		db.close();
	}

	public void delete(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+ID+"="+id+";");
		db.close();
	}
	
	public void add(String id,String name,String phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("INSERT INTO "+TABLE_NAME+" ("+ID+","+NAME+","+PHONE+") VALUES ("+id+",'"+name+"','"+phone+"');");
		db.close();
	}
	
	public void edit(String id,String name,String phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("UPDATE "+TABLE_NAME+" SET "+NAME+"='"+name+"', "+PHONE+"='"+phone+"' WHERE "+ID+"="+id);
		db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE " + TABLE_NAME+";");
		onCreate(db);
	}
}
