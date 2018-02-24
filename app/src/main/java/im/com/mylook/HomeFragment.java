package im.com.mylook;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private TextView ratingText;
    private ImageView ratingImage;
    private SeekBar ratingSeekBar;

    private List<User> potentialMatches;

    private String oppositeSex;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private DatabaseReference ratingsRef;


    int matchCounter;


    private String sex;

    boolean FDataExist = false;
    boolean MDataExist = false;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();


        ratingText = view.findViewById(R.id.home_ratingTextView);
        ratingImage = view.findViewById(R.id.home_matchImage);
        ratingSeekBar = view.findViewById(R.id.home_matchRating);

        ratingSeekBar.setMax(10);
        setProgressDefault();


//        dataExistCheck();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        Log.e("sexPhotyosnext", "go next " + SexChecking.FDataExist + " " + SexChecking.MDataExist);

//        if (!SexChecking.MDataExist && !SexChecking.FDataExist) {
//
//            startActivity(new Intent(getActivity().getApplicationContext(), EditProfile.class));
//
//            Log.e("start", "editprofile");
//        }


        setRatingListener();


        matchCounter = 0;


        }

    private void setRatingListener() {
        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ratingText.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //todo option to send after stop touching

                final int progress = seekBar.getProgress();


                ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ratingsRef = mDatabase.getReference().child("Users").child("Male").child("MatchRatings").child(potentialMatches.get(matchCounter).getPath());
                        UserRatings userRatings = (UserRatings) dataSnapshot.getValue();
                        ArrayList<Integer> newList = userRatings.getRatings();
                        newList.add(progress);
                        userRatings.setRatings(newList);
                        ratingsRef.setValue(userRatings);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }//todo change from male to opposite


        });
    }


    private void getArrayOfMatches() { //todo change from male to opposite

        Log.e("pote", "getarrayofmatcher");
        Query query = mDatabase.getReference().child("Users").child("Male").child("MatchPhotos").limitToLast(5);

        potentialMatches = new ArrayList<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Log.e("pote", "addin");
                    User user = dsp.getValue(User.class);
                    Log.e("pote", user.getImage());
                    potentialMatches.add(user);
                }

                displayImages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void displayImages() {
        Log.e("pote", " displayiamges");

//        ratingImage.setImageURI(Uri.parse(potentialMatches.get(matchCounter).getImage()));

        if (matchCounter >= 5)

            Picasso
                    .with(getActivity().getApplicationContext())
                    .load(Uri.parse(potentialMatches.get(matchCounter).getImage()))
                    .fit()//todo center crop if needed?
                    .transform(new RoundedCornersTransform())
                    .into(ratingImage);

        matchCounter++;
    }

    boolean cont;

    void runAfterCheck() {
        getArrayOfMatches();
    }

    private boolean dataExistCheck() {

        Log.e("pote", "dataexistcheck");

        DatabaseReference Fref = mDatabase.getReference().child("Users").child("Female").child(mAuth.getCurrentUser().getUid());
        DatabaseReference Mref = mDatabase.getReference().child("Users").child("Male").child(mAuth.getCurrentUser().getUid());


        Fref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("UserInfo")) {
                    Log.e("asd", "f not empty");
                    FDataExist = true;
                    sex = "Female";
                    cont = true;
//                    UserInfo.sex = "Female";

                    runAfterCheck();


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
                    sex = "Male";
                    cont = true;
//                    UserInfo.sex = "Male";
                } else {
                    Log.e("asd", "m empty");
                    runEditProfile();

                    runAfterCheck();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return cont;
    }

    private void runEditProfile() {
        if (!MDataExist && !FDataExist) {
            startActivity(new Intent(getActivity().getApplicationContext(), EditProfile.class));
        }
    }


    private void setProgressDefault() {
        ratingSeekBar.setProgress(5);
    }

}
