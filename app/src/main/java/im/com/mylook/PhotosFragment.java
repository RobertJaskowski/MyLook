package im.com.mylook;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {


    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_INTENT = 3;

    private StorageReference mStorage;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;


    private FloatingActionButton floatingActionButtonFile, floatingActionButtonCamera;


    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter;

    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();


        floatingActionButtonFile = view.findViewById(R.id.photos_fab_file);
        floatingActionButtonCamera = view.findViewById(R.id.photos_fab_camera);
        recyclerView = view.findViewById(R.id.photos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setFloatingButtonsListeners();


        setRecyclerAdapter();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tag result", String.valueOf(resultCode));

        if (resultCode != 0) {

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());


            Bitmap bmp = ImagePicker.getImageFromResult(getActivity().getApplicationContext(),resultCode,data);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] dane = byteArrayOutputStream.toByteArray();

            UploadTask uploadTask = filepath.putBytes(dane);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    saveUriToDatabase(taskSnapshot);
                }
            });

        }
    }

    private void saveUriToDatabase(UploadTask.TaskSnapshot taskSnapshot) {
        Uri downloadedUri = taskSnapshot.getDownloadUrl();

        Calendar calendar = Calendar.getInstance();


        String userUid = mAuth.getCurrentUser().getUid();
        Log.e("user", userUid);
        User user = new User(String.valueOf(downloadedUri), String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH)+1), 5, 0);
        mRef.child("Users").child(userUid).push().setValue(user);


    }

    private void setRecyclerAdapter() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        Log.e("user", "adapter");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.setPhoto(model.getImage(), getActivity().getApplicationContext());
                holder.setDate(String.valueOf(model.getDate()));
                holder.setAverage(String.valueOf(model.getAverage()));
                holder.setRatings(String.valueOf(model.getRatings()));
            }

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.photos_cards_item, parent, false);

                return new UserViewHolder(view);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                Log.e("data error", error.getDetails() + " " + error.getMessage());
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class User {
        private String image;
        private String date;
        private int average;
        private int ratings; //todo no rat and ave at start

        public User() {
        }

        public User(String image, String date, int average, int ratings) {
            this.image = image;
            this.date = date;
            this.average = average;
            this.ratings = ratings;

        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getAverage() {
            return average;
        }

        public void setAverage(int average) {
            this.average = average;
        }

        public int getRatings() {
            return ratings;
        }

        public void setRatings(int ratings) {
            this.ratings = ratings;
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView date, average, ratings;

        public UserViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.card_photo_image);
            date = itemView.findViewById(R.id.card_photo_date);
            average = itemView.findViewById(R.id.card_photo_average);
            ratings = itemView.findViewById(R.id.card_photo_ratings);
        }

        public void setPhoto(String i, Context context) {
            Picasso
                    .with(context)
                    .load(i)
                    .fit()
                    .centerCrop()
                    .transform(new RoundedCornersTransform())
                    .into(photo);
        }

        public void setDate(String i) {
            String text = i;


            date.setText(i);
        }

        public void setAverage(String i) {
            average.setText(i);
        }

        public void setRatings(String i) {
            ratings.setText(i);
        }

    }


    private void setFloatingButtonsListeners() {


        floatingActionButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

//                                Toast.makeText(getActivity().getApplicationContext(), "Camera permission granted.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                startActivityForResult(intent, CAMERA_INTENT);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getActivity().getApplicationContext(), "Required camera permission", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();


            }
        });


        floatingActionButtonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("asd ", "asd");

                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {


                                Intent intent = new Intent(Intent.ACTION_PICK);

                                intent.setType("image/*");


                                Intent chooserr = Intent.createChooser(intent, "Choose application");
                                startActivityForResult(chooserr, GALLERY_INTENT);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getActivity().getApplicationContext(), "Required storage permission", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                Log.e("asd ", "asd");
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && floatingActionButtonCamera.isShown()){
                    floatingActionButtonCamera.hide();
                    floatingActionButtonFile.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    floatingActionButtonCamera.show();
                    floatingActionButtonFile.show();
                }
                super.onScrollStateChanged(recyclerView,newState);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        firebaseRecyclerAdapter.stopListening();
    }
}
