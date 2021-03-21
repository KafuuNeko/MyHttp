package cc.kafuu.myhttp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cc.kafuu.myhttp.R;

public class LogListAdapter extends BaseAdapter {

    private static class LogItemStruct {
        public long id;
        public String time;
        public String url;
        public boolean isGet;
        public String param;

        public LogItemStruct(long id, String time, String url, boolean isGet, String postParam)
        {
            this.id = id;
            this.time = time;
            this.url = url;
            this.isGet = isGet;

            if(!isGet)
            {
                this.param = postParam;
            }
            else if(this.url != null)
            {
                String[] sp = this.url.split("\\?");
                if(sp.length == 2) param = sp[1];
                this.url = sp[0];
            }

            if(url == null || url.length() == 0) url = "null";
            if(param == null || param.length() == 0) param = "No parameters";
        }
    }

    private SQLiteOpenHelper mDatabase = null;
    private LayoutInflater mInflater = null;
    private List<LogItemStruct> mItems;


    public LogListAdapter(Context context, SQLiteOpenHelper database)
    {
        mDatabase = database;
        mInflater = LayoutInflater.from(context);
        loadItems();
    }

    public void loadItems()
    {
        mItems = new ArrayList<>();
        Cursor cursor = mDatabase.getReadableDatabase().rawQuery("SELECT Id,RegTime,Url,IsGet,PostParam FROM post_get ORDER BY RegTime DESC LIMIT 1000", null);
        while (cursor.moveToNext())
            mItems.add(new LogItemStruct(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3) == 1, cursor.getString(4)));
        cursor.close();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public LogItemStruct getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        loadItems();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null)
             view = mInflater.inflate(R.layout.listview_post_get_log_item, parent, false);

        TextView info = view.findViewById(R.id.info);
        TextView requestUrl = view.findViewById(R.id.requestUrl);
        TextView requestParam = view.findViewById(R.id.requestParam);

        LogItemStruct item = getItem(position);

        String infoText = item.time+"["+ (item.isGet?"GET":"POST") +"]";
        info.setText(infoText);
        requestUrl.setText(item.url);
        requestParam.setText(item.param);

        return view;
    }


}
