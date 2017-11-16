package com.forsuntech.prisonmdm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	private static final int VERSION = 1;

	private static final String CREATE_MESSAGE="Create TABLE [message]([TIndex] integer PRIMARY KEY AUTOINCREMENT, [phonenumber] varchar(100))";
	private static final String DROP_MESSAGE="drop table if exists message";

	private static final String CREATE_PHONE="Create TABLE [phone]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[phonenumber] varchar(100))";
	private static final String DROP_PHONE="drop table if exists phone";

	private static final String CREATE_TAG="Create TABLE [tag]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[tagid] varchar(255))";
	private static final String DROP_TAG="drop table if exists tag";

	private static final String CREATE_SSID="Create TABLE [SSID]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[SSID] varchar(255))";
	private static final String DROP_SSID="drop table if exists ssid";

//	private static final String CREATE_CALLBLACK = "Create TABLE [callblack]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[telnum] varchar(50),[mode] integer,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_CALLBLACK = "drop table if exists callblack";
//
//	private static final String CREATE_DEVICESTATUS = "Create  TABLE [devicestatus]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[module] varchar(50) UNIQUE,[status] integer,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_DEVICESTATUS = "drop table if exists devicestatus";
//
//	private static final String CREATE_APPBLACK = "Create TABLE [appblack]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[appname] varchar(100) UNIQUE,[mode] integer,[pkgname] varchar(500) UNIQUE,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_APPBLACK = "drop table if exists appblack";
//
//	private static final String CREATE_GPSLOCATE = "Create TABLE [gpslocate]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[timeInterval] integer,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_GPSLOCATE = "drop table if exists gpslocate";
//
//	private static final String CREATE_APPSCOUNT = "Create TABLE [appscount]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[pkgname] varchar(250) UNIQUE,[appname] varchar(100),[time] integer,[date] date,[isreport] boolean,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_APPSCOUNT = "drop table if exists appscount";
//
//	private static final String CREATE_EDEVICESTATUS = "Create  TABLE [edevicestatus]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[module] varchar(50) UNIQUE,[status] integer,[lat] double,[lag] double,[radius] double,[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_EDEVICESTATUS = "drop table if exists edevicestatus";
//
//
//	private static final String CREATE_OFFLINEALARM = "Create TABLE [offlinealarm]([TIndex] integer PRIMARY KEY AUTOINCREMENT,[timeInterval] integer,[action] double,[fristoffline] varchar(20),[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_OFFLINEALARM = "drop table if exists offlinealarm";
//
//	private static final String CREATE_MSGCONTROL = "Create TABLE [msgControl] ([TIndex] integer PRIMARY KEY AUTOINCREMENT,[hourBucket] varchar(25),[weekBucket] varchar(8),[msgControlStatus] integer)";
//	private static final String DROP_MSGCONTROL = "drop table if exists msgControl";
//
//	private static final String CREATE_DEVICEINFO = "Create TABLE [deviceinfo] ([TIndex] integer PRIMARY KEY AUTOINCREMENT,[imei] varchar(35) UNIQUE)";
//	private static final String DROP_DEVICEINFO = "drop table if exists deviceinfo";
//
//	private static final String CREATE_APPCOUNTPOLICY = "Create TABLE [appcountpolicy] ([TIndex] integer PRIMARY KEY AUTOINCREMENT,[pkgname] varchar(250) UNIQUE,[appname] varchar(100),[hourBucket] varchar(25),[weekBucket] varchar(8))";
//	private static final String DROP_APPCOUNTPOLICY = "drop table if exists appcountpolicy";

	@Override
	public void onCreate(SQLiteDatabase database) {

		database.execSQL(CREATE_MESSAGE);
		database.execSQL(CREATE_TAG);
		database.execSQL(CREATE_PHONE);
		database.execSQL(CREATE_SSID);
		// database.execSQL(CREATE_CLIENTVERSION);
		Log.i("onCreate", "-------------覆盖安装数据库表:onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		try {
			Log.i("onUpgrade", "------------覆盖安装数据库表");

			switch (oldVersion) {
			case 1:

			case 2:

			default:
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
