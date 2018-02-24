package im.com.mylook;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Robert on 06.02.2018 - 12:49.
 */

public class SexChecking {

    private static FirebaseDatabase mDatabase;
    private static FirebaseAuth mAuth;
    public static int getLicz() {
        return licz;
    }

    static String sex;

    static boolean Fchecked =false;
    static boolean Mchecked =false;
    static boolean sexChecked = false;
    static boolean FDataExist = false;
    static boolean MDataExist = false;

    private static int licz = 0;

    void checkSex(OnGetDataListener listener) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        DatabaseReference Fref = mDatabase.getReference().child("Users").child("Female").child(mAuth.getCurrentUser().getUid());
        DatabaseReference Mref = mDatabase.getReference().child("Users").child("Male").child(mAuth.getCurrentUser().getUid());


        Fref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Fchecked = true;
                if (dataSnapshot.hasChild("UserInfo")) {
                    Log.e("asd", "f not empty");
                    sex = "Female";
                    FDataExist = true;
                    sexChecked = true;
                    listener.onSuccess(dataSnapshot);


                } else {
                    Log.e("asd", "f empty");
                    sexChecked = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mchecked =true;
                if (dataSnapshot.hasChild("UserInfo")) {
                    Log.e("asd", "m not empty");
                    sex = "Male";
                    MDataExist = true;
                    sexChecked = true;
                    listener.onSuccess(dataSnapshot);
                } else {
                    Log.e("asd", "m empty");
                    sexChecked = true;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkExistance(){

    }


    static boolean checkState() {
        if (sexChecked) {
            return true;
        }
        if (licz <= 5) {
            licz++;
            return false;
        } else {
            return true;
        }
    }


}
