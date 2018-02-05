package im.com.mylook;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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


    int matchCounter ;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        mDatabase = FirebaseDatabase.getInstance();


        ratingText = view.findViewById(R.id.home_ratingTextView);
        ratingImage = view.findViewById(R.id.home_matchImage);
        ratingSeekBar = view.findViewById(R.id.home_matchRating);

        ratingSeekBar.setMax(10);
        setProgressDefault();
//        getOppositeSex();

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
            }
        });


        matchCounter = 0;
        getArrayOfMatches();




        return view;
    }

    private void getOppositeSex() {
        if (MainActivity.sex.equals("Male")){
            oppositeSex = "Female";
        }else{
            oppositeSex = "Male";
        }
    }

    private void getArrayOfMatches(){
        Query query = mDatabase.getReference().child("Users").child("Male").child("MatchPhotos").limitToLast(5);

        potentialMatches = new ArrayList<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    Log.e("pote","addin");
                    User user = dsp.getValue(User.class);
                    Log.e("pote",user.getImage());
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
        Log.e("pote"," displayiamges");

//        ratingImage.setImageURI(Uri.parse(potentialMatches.get(matchCounter).getImage()));

        if (matchCounter>=5)

        Picasso
                .with(getActivity().getApplicationContext())
                .load(Uri.parse(potentialMatches.get(matchCounter).getImage()))
                .fit()//todo center crop if needed?
                .transform(new RoundedCornersTransform())
                .into(ratingImage);

        matchCounter++;
    }


    private void setProgressDefault(){
        ratingSeekBar.setProgress(5);
    }

}
