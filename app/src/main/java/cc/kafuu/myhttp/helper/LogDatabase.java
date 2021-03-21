package cc.kafuu.myhttp.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LogDatabase extends SQLiteOpenHelper {

    public LogDatabase(@NonNull Context context) {
        super(context, context.getFilesDir() + "/logs.db", null, 5);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d("LogDatabase", "onOpen");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LogDatabase", "onCreate");
        initStruct(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Sqlite", "onUpgrade");
        db.execSQL("DROP TABLE post_get");
        initStruct(db);
    }

    private void initStruct(SQLiteDatabase db)
    {
        try {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS post_get(" +
                    "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "RegTime DATETIME DEFAULT(datetime('now', 'localtime')), " +
                    "Url VARCHAR, " +
                    "IsGet BOOL, " +
                    "PostParam VARCHAR, " +
                    "IsJson BOOL, " +
                    "RequestHead VARCHAR, " +
                    "RequestCookie VARCHAR, " +
                    "ResponseResult VARCHAR, " +
                    "ResponseHeaders VARCHAR)"
            );
        } catch (Exception e)
        {
            Log.d("LogDatabase", e.toString());
        }
    }

    public LogDatabase addNewPostGetLog(String Url, boolean IsGet, String PostParam, boolean IsJson, String RequestHead, String RequestCookie, String ResponseResult, String ResponseHeaders)
    {
        try
        {
            Object[] bindArgs = new Object[]{Url, (IsGet?"1":"0"), PostParam, (IsJson?"1":0), RequestHead, RequestCookie, ResponseResult, ResponseHeaders};
            getWritableDatabase().execSQL("INSERT INTO post_get(Url, IsGet, PostParam, IsJson, RequestHead, RequestCookie, ResponseResult, ResponseHeaders) VALUES(?, ?, ?, ?, ?, ?, ?, ?)", bindArgs);
        } catch (Exception e)
        {
            Log.d("LogDatabase", e.toString());
        }
        return this;
    }
}
