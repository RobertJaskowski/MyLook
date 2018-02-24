package im.com.mylook;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Robert on 24.02.2018 - 21:01.
 */

public interface OnGetDataListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
    void onEmpty();
}
