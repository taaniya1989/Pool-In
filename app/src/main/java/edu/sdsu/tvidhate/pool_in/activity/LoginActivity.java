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

import edu.sdsu.tvidhate.pool_in.R;
import edu.sdsu.tvidhate.pool_in.helper.SharedConstants;

public class LoginActivity extends AppCompatActivity implements SharedConstants,View.OnClickListener {

    private EditText emailId;
    private EditText password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailId =  findViewById(R.id.emailAddressEditText);
        password = findViewById(R.id.passwordEditText);
        Button signIn = findViewById(R.id.signInButton);
        Button signUp = findViewById(R.id.signUpButton);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signInButton:
                if(validInput())
                    authenticateUser(emailId.getText().toString().trim(),
                            password.getText().toString().trim());
                break;
            case R.id.signUpButton:
                Intent signUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpActivity);
                finish();
                break;
        }
    }

    private void authenticateUser(String email, String pass) {
        Log.d("TPV-NOTE","Authenticating user");
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, SharedConstants.INVALID_EMAIL_PW_TOAST, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private boolean validInput() {
        boolean valid = true;
        if(TextUtils.isEmpty(emailId.getText().toString())){
            emailId.setError(SharedConstants.ENTER_EMAIL);
            valid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(SharedConstants.ENTER_PASSWORD);
            valid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(SharedConstants.PASSWORD_LENGTH);
            valid = false;
        }
        return valid;
    }
}
