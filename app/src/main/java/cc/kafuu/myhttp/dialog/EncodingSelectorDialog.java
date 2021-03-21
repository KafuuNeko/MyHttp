package cc.kafuu.myhttp.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import java.nio.charset.Charset;
import java.util.SortedMap;

public class EncodingSelectorDialog {

    public interface OnClickListener {
        void onClick(DialogInterface dialog, Charset charset);
    }

    private Context mContext;
    private OnClickListener mListener = null;

    public EncodingSelectorDialog(@NonNull Context context) {
        mContext = context;
    }

    public EncodingSelectorDialog setListener(OnClickListener listener)
    {
        mListener = listener;
        return this;
    }

    public void show()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        int index = 0;
        final SortedMap<String,Charset> charsets = Charset.availableCharsets();
        final CharSequence[] list = new CharSequence[charsets.size()];
        for (String name : charsets.keySet()) list[index++] = name;

        builder.setItems(list, (dialog, which) -> mListener.onClick(dialog, charsets.get(list[which].toString())));

        builder.show();
    }

}
