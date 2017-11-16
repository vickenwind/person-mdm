package com.forsuntech.prisonmdm.db;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import com.forsuntech.prisonmdm.utils.LogWriterUtils;

public class SqliteWrapper {
	private static final String SQLITE_EXCEPTION_DETAIL_MESSAGE = "unable to open database file";
	private static final String TAG = "SqliteWrapper";

	public static void checkSQLiteException(Context paramContext, SQLiteException paramSQLiteException) {
		if (isLowMemory(paramSQLiteException)) {
			return;
		}
		throw paramSQLiteException;
	}

	public static int delete(Context paramContext, ContentResolver paramContentResolver, Uri paramUri,
			String paramString, String[] paramArrayOfString) {
		try {
			int i = paramContentResolver.delete(paramUri, paramString, paramArrayOfString);
			return i;
		} catch (SQLiteException localSQLiteException) {
			Log.e("SqliteWrapper", "Catch a SQLiteException when delete: ", localSQLiteException);
			checkSQLiteException(paramContext, localSQLiteException);
		}
		return -1;
	}

	public static Uri insert(Context paramContext, ContentResolver paramContentResolver, Uri paramUri,
			ContentValues paramContentValues) {
		try {
			Log.i("msg", "insert");
			Uri localUri = paramContentResolver.insert(paramUri, paramContentValues);
			return localUri;
		} catch (SQLiteException localSQLiteException) {
			Log.e("SqliteWrapper", "Catch a SQLiteException when insert: ", localSQLiteException);
			checkSQLiteException(paramContext, localSQLiteException);
		}
		return null;
	}

	private static boolean isLowMemory(SQLiteException paramSQLiteException) {
		return paramSQLiteException.getMessage().equals("unable to open database file");
	}

	public static Cursor query(LogWriterUtils LogWriterUtils, Context paramContext,
							   ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString1, String paramString1,
							   String[] paramArrayOfString2, String paramString2) {
		try {
			Cursor localCursor = paramContentResolver.query(paramUri, paramArrayOfString1, paramString1,
					paramArrayOfString2, paramString2);
			return localCursor;
		} catch (SQLiteException localSQLiteException) {
			// Intent i=new
			// Intent(MyContext.getContextObject(),TestActivity.class);
			// i.putExtra("imeiStr", localSQLiteException.getMessage());
			// i.putExtra("key", MyConstant.getInstance().getIMEI());
			// i.putExtra("ip", MyConstant.getServiceIp());
			// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// MyContext.getContextObject().startActivity(i);
			Log.e("SqliteWrapper", "Catch a SQLiteException when query: ", localSQLiteException);
			checkSQLiteException(paramContext, localSQLiteException);
		}
		return null;
	}

	public static boolean requery(Context paramContext, Cursor paramCursor) {
		try {
			boolean bool = paramCursor.requery();
			return bool;
		} catch (SQLiteException localSQLiteException) {
			Log.e("SqliteWrapper", "Catch a SQLiteException when requery: ", localSQLiteException);
			checkSQLiteException(paramContext, localSQLiteException);
		}
		return false;
	}

	public static int update(Context paramContext, ContentResolver paramContentResolver, Uri paramUri,
			ContentValues paramContentValues, String paramString, String[] paramArrayOfString) {
		try {
			int i = paramContentResolver.update(paramUri, paramContentValues, paramString, paramArrayOfString);
			return i;
		} catch (SQLiteException localSQLiteException) {
			Log.e("SqliteWrapper", "Catch a SQLiteException when update: ", localSQLiteException);
			checkSQLiteException(paramContext, localSQLiteException);
		}
		return -1;
	}
}
