package com.forsuntech.prisonmdm.db;

import android.content.Context;

import com.forsuntech.prisonmdm.utils.LogWriterUtils;

/**
 * Created by vicken on 2017/11/9.
 */

public class DbPolicy {
    private Context context = null;
    private DatabaseHelper db = null;
    private com.forsuntech.prisonmdm.utils.LogWriterUtils LogWriterUtils;
    public DbPolicy(Context _context) {
        context = _context;
        db = new DatabaseHelper(context, "mdm.db");
        LogWriterUtils = new LogWriterUtils("DB.log");
    }

    public synchronized void addmessage(String phonenumber){

    }

    public synchronized  void updatemessage(String phonenumber){

    }

//    public

}
