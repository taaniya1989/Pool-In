package edu.sdsu.tvidhate.pool_in.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.entity.User;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class SignUpActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener {

    private EditText contact,emailId,password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        contact = findViewById(R.id.signUpContactEditText);
        emailId = findViewById(R.id.signUpEmailAddressEditText);
        password = findViewById(R.id.signUpPasswordEditText);
        Button submit = findViewById(R.id.submitButton);
        Button reset = findViewById(R.id.resetButton);

        auth = FirebaseAuth.getInstance();

        submit.setOnClickListener(this);
        reset.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.submitButton:
                final String email = emailId.getText().toString().trim();
                String pass = password.getText().toString().trim();
                final String contactNumber = contact.getText().toString().trim();
                if(validInput()){
                    //Handle Name & Address
                    User currentUser = new User(emailId.getText().toString().split("@")[0],contact.getText().toString().trim(),
                            emailId.getText().toString().trim());
                    try{
                        DatabaseReference firebaseDatabaseInstanceReference = FirebaseDatabase.getInstance().getReference();
                        firebaseDatabaseInstanceReference.child(FIREBASE_PERSONAL_DATA).child(contact.getText().toString()).removeValue();
                        firebaseDatabaseInstanceReference.child(FIREBASE_PERSONAL_DATA).child(contact.getText().toString()).setValue(currentUser);
                        Log.d("TPV-NOTE","Data submitted successfully");

                    }catch(Exception e){
                        Log.d("TPV-NOTE","Exception: "+e);
                    }

                    Log.d("TPV-NOTE","user data: "+email+" "+pass+" "+contactNumber);
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d("TPV-NOTE","Exception: "+task.getException());
                                        Toast.makeText(SignUpActivity.this, SharedConstants.REGISTRATION_FAILED, Toast.LENGTH_SHORT).show();
                                        emailId.setError(SharedConstants.INVALID_EMAIL);
                                    } else {
                                        FirebaseUser user = auth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(contactNumber)
                                                .build();
                                        try {
                                            if(user != null){
                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("TPV-NOTE", "User profile updated.");
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }else{
                                                                    Log.d("TPV-NOTE","user profile update failed");
                                                                }
                                                            }
                                                        });
                                            }

                                        } catch (Exception e) {
                                            Log.d("TPV-NOTE", "failed: "+e);
                                        }
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), SharedConstants.ENTER_REQUIRED_FIELDS, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.resetButton:
                emailId.setText(SharedConstants.EMPTY_STRING);
                password.setText(SharedConstants.EMPTY_STRING);
                contact.setText(SharedConstants.EMPTY_STRING);
                break;
        }
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError(SharedConstants.ENTER_EMAIL);
            dataValid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(SharedConstants.ENTER_PASSWORD);
            dataValid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(SharedConstants.PASSWORD_LENGTH);
            Toast.makeText(getApplicationContext(), SharedConstants.PASSWORD_LENGTH,
                    Toast.LENGTH_SHORT).show();
            dataValid = false;
        }
        return dataValid;
    }

}
