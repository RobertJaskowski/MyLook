package im.com.mylook;



import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {


    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_INTENT = 3;

    private StorageReference mStorage;
    private FloatingActionButton floatingActionButtonFile, floatingActionButtonCamera;



    private ImageView imageView;





    public PhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        mStorage = FirebaseStorage.getInstance().getReference();

        floatingActionButtonFile = view.findViewById(R.id.photos_fab_file);
        floatingActionButtonCamera = view.findViewById(R.id.photos_fab_camera);

        imageView = view.findViewById(R.id.testContect);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setFloatingButtonsListeners();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tag result",String.valueOf(resultCode));

        if (resultCode != 0) {

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());


            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {


                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadedUri = taskSnapshot.getDownloadUrl();


                        Log.e("test",String.valueOf(downloadedUri));

                        Toast.makeText(getActivity().getApplicationContext(), "Added photo", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (requestCode == CAMERA_INTENT && resultCode == RESULT_OK) {

                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity().getApplicationContext(), "Added photo", Toast.LENGTH_SHORT).show();

                    }
                });


            }
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

                                Toast.makeText(getActivity().getApplicationContext(),"Camera permission granted.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                startActivityForResult(intent, CAMERA_INTENT);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getActivity().getApplicationContext(),"Required camera permission",Toast.LENGTH_LONG).show();
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

                Log.e("asd ","asd");

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
                                Toast.makeText(getActivity().getApplicationContext(),"Required storage permission",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                Log.e("asd ","asd");
                                token.continuePermissionRequest();
                            }
                        }).check();




            }
        });
    }


}
