package com.airthcompany.gradecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Airth on 1/7/2015.
 */
public class DataBaseAdapter {
    private static final String TAG = "DatabaseAdapter";

    // Database Fields
    public static final String KEY_ROWID = "id";
    public static final int COL_ROWID = 0;

    //My fields in here
    public static final String KEY_NAME = "name";
    public static final String KEY_SCORE = "score";
    public static final String KEY_MAX = "max";
    public static final String KEY_WEIGHT = "weight";

    public static final String TABLE_NAME_TABLE = "table"; // table name for table list

    public static final int COL_NAME = 1;
    public static final int COL_SCORE = 2;
    public static final int COL_MAX = 3;
    public static final int COL_WEIGHT = 4;

    public static final String[] ALL_KEYS = new String [] {KEY_ROWID, KEY_NAME, KEY_SCORE, KEY_MAX, KEY_WEIGHT};

    //Database info
    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_TABLE = "mainTable"; // one table for now

    //Database version (might change)

    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE + " (" + KEY_ROWID +
             " integer primary key autoincrement, "

            // MY FIELDS
            + KEY_NAME + " text not null, "
            + KEY_SCORE + " integer not null, "
            + KEY_MAX + " integer not null, "
            + KEY_WEIGHT + " integer not null"

            // To close
            + ");";


    private final Context context;

    private DatabaseHelper myDatabaseHelper;
    private SQLiteDatabase db;




    // PUBLIC METHODS

    public DataBaseAdapter(Context ctx){
        this.context = ctx;
        myDatabaseHelper = new DatabaseHelper(context);
    }

    public DataBaseAdapter open(){
        db = myDatabaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myDatabaseHelper.close();
    }

    public void createTable(String tableName){
        String create =
                "create table " + tableName + " (" + KEY_ROWID +
                        " integer primary key autoincrement, "

                        // MY FIELDS
                        + KEY_NAME + " text not null, "
                        + KEY_SCORE + " integer not null, "
                        + KEY_MAX + " integer not null, "
                        + KEY_WEIGHT + " integer not null"

                        // To close
                        + ");";

        db.execSQL(create);
    }



    public Cursor getAllTables(){
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        return c;
    }




    // insert item to main table (changing this later to account for multiple tables (courses) )
    public long insertItem(String name, int score, int max, int weight, String tableName){

        //create the data
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SCORE, score);
        initialValues.put(KEY_MAX, max);
        initialValues.put(KEY_WEIGHT, weight);

        //insert the data created to database
        return db.insert(tableName, null, initialValues);
    }

    // delete item from main table
    public boolean deleteRow(long rowID, String tableName){
        // id = rowID
        String where = KEY_ROWID + "=" + rowID;
        return db.delete(tableName, where, null) != 0;
    }

    public void deleteTable(String tableName){
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
    }

    public void deleteAll(String tableName){
        Cursor c = getAllRows(tableName);
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if(c.moveToFirst()){
            do{
                deleteRow(c.getLong( (int) rowId), tableName);
            } while(c.moveToNext());
        }
        c.close();
    }

    public Cursor getAllRows(String tableName){
        String where = null;
        Cursor c = db.query(true, tableName, ALL_KEYS,
                    where, null, null, null, null, null);
        if (c != null){
            c.moveToFirst();
        }

        return c;
    }


    public Cursor getRow(long rowId, String tableName){
        String where = KEY_ROWID +"="+ rowId;
        Cursor c = db.query(true, tableName, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null){
            c.moveToFirst();
        }

        return c;
    }


    public boolean updateRow(long rowId, String name, int score, int max, int weight, String tableName) {
        String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_SCORE, score);
        newValues.put(KEY_MAX, max);
        newValues.put(KEY_WEIGHT, weight);

        // Insert it into the database.
        return db.update(tableName, newValues, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(db);
        }
    }
}
