package com.sierratechnologies.safeshield;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sierratechnologies.safeshield.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater()) ;
        setContentView(binding.getRoot());

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseHelper=new DatabaseHelper(this);

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= binding.signupEmail.getText().toString();
                String password= binding.signupPassword.getText().toString();
                String confirmPassword= binding.signupConfirm.getText().toString();

                if (email.equals("") || password.equals("") || confirmPassword.equals("")){
                    Toast.makeText(SignupActivity.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (password.equals(confirmPassword)){
                        Boolean checkUserEmail = databaseHelper.checkEmail (email);

                        if (checkUserEmail==false){
                            Boolean insert = databaseHelper.insertData(email, password);

                            if (insert==true){
                                Toast.makeText(SignupActivity.this,"Successfully signed in",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignupActivity.this,"Error in signing in",Toast.LENGTH_SHORT).show();
                            }
                            }
                        else{
                            Toast.makeText(SignupActivity.this,"User already exists,please login",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                }
            }

        }
    });
        binding.LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignupActivity.this,"Successfully signed in",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            }
        });
}
}