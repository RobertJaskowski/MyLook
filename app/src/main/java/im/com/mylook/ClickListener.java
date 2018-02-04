package im.com.mylook;

import android.view.View;

/**
 * Created by Robert on 04.02.2018 - 12:18.
 */

public interface ClickListener {
    public void onClick(View view,int position);
    public void onLongClick(View view,int position);
}
