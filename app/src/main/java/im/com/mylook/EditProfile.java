package im.com.mylook;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfile extends Activity {

    @BindView(R.id.editProfile_etName)
    EditText etName;

    @BindView(R.id.editProfile_etAge)
    EditText etAge;

    @BindView(R.id.editProfile_sexSpinner)
    Spinner sexSpinner;


    @BindView(R.id.editProfile_save)
    Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);


    }

    @OnClick(R.id.editProfile_save)
    void saveInfo() {
        Log.e("etprofile", "saving");

        etName.setError(null);
        etAge.setError(null);

        // Store values at the time of the login attempt.
        String name = etName.getText().toString();
        String age = etAge.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.invalid));
            focusView = etName;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(age) || Integer.parseInt(age) < 18 || Integer.parseInt(age) > 80) {
            etAge.setError(getString(R.string.invalid));
            focusView = etAge;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }else {

            String sex = (String) sexSpinner.getSelectedItem();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(sex).child(mAuth.getCurrentUser().getUid());

//            mRef.setValue() //todo


        }

    }

}
