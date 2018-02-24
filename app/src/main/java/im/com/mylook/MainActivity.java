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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {


    private Fragment mFragment;


    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;

    private ProgressBar progressBar;

    Observable<Boolean> observable;

    Observer<Boolean> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
        }

        progressBar = findViewById(R.id.progress_switchFragment);

        progressBar.setVisibility(View.VISIBLE);

        setRxFragment();


//        if (!SexChecking.sexChecked) {
//            AccessSex();
//        }else {
//            subscribeRx();
//            Log.e("subRx","subRx");
//        }


//        while (!SexChecking.checkState()) {
//            Log.e("mainactivity", "checking " + SexChecking.getLicz());
//
//        }

        if (!SexChecking.sexChecked) {
            AccessSex();
        } else {
            if (savedInstanceState != null) {
                mFragment = getFragmentManager().getFragment(savedInstanceState, "fragment");
                switchFragment(mFragment);
            } else {
                switchFragment(new HomeFragment());
            }
        }

        setNavigation();

    }

    void subscribeRx() {
        observable.subscribe(observer);
    }

    private void setRxFragment() {
        observer = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
//                SexChecking.checkSex();
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.e("asssd", "asdssNEXT");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                switchFragment(new HomeFragment());
                progressBar.setVisibility(View.GONE);

                Log.e("onComplete", "observer");

            }
        };

        observable = Observable.empty();
    }

    private void AccessSex() {
        SexChecking sexChecking = new SexChecking();

        sexChecking.checkSex(new OnGetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                subscribeRx();
                Log.e("accessSex", "success");
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onEmpty() {
                if (SexChecking.Fchecked && SexChecking.Mchecked) {
                    if (!SexChecking.MDataExist && !SexChecking.FDataExist) {

                        startActivity(new Intent(getApplicationContext(), EditProfile.class));

                        Log.e("start", "editprofile");
                    }
                }
            }
        });
    }

    private void setNavigation() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void switchFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mFragment = fragment;
        fragmentTransaction.replace(R.id.frame_container, mFragment, "tag");
        fragmentTransaction.commit();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getFragmentManager().findFragmentByTag("tag") != null) {
            getFragmentManager().putFragment(outState, "fragment", mFragment);
        }

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
