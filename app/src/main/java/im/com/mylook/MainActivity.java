package im.com.mylook;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    Fragment mFragment;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    boolean FDataExist = false;
    boolean MDataExist = false;


    static String sex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mFragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
            switchFragment(mFragment);
        } else {
            switchFragment(new HomeFragment());
        }


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        dataExistCheck();
    }

    private void dataExistCheck() {
        DatabaseReference Fref = mDatabase.getReference().child("Users").child("Female").child(mAuth.getCurrentUser().getUid());
        DatabaseReference Mref = mDatabase.getReference().child("Users").child("Male").child(mAuth.getCurrentUser().getUid());



        Fref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("UserInfo")) {
                    Log.e("asd", "f not empty");
                    FDataExist = true;
                    sex = "Female";
//                    UserInfo.sex = "Female";

                } else {
                    Log.e("asd", "f empty");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("UserInfo")) {
                    Log.e("asd", "m not empty");
                    MDataExist = true;
                    sex= "Male";
//                    UserInfo.sex = "Male";
                } else {
                    Log.e("asd", "m empty");
                    runEditProfile();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void runEditProfile(){
        if (!MDataExist && !FDataExist){
            startActivity(new Intent(MainActivity.this, EditProfile.class));
        }
    }


    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mFragment = fragment;
        fragmentTransaction.replace(R.id.frame_container, mFragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        getFragmentManager().putFragment(outState, "fragment", mFragment);

//        outState.putBundle("fragment",bundle);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragment(new HomeFragment());
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(new PhotosFragment());
                    return true;
                case R.id.navigation_profile:
                    switchFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logOut:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;

            case R.id.menu_settings:
                break;
        }

        return true;
    }
}
