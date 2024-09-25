package com.sierratechnologies.safeshield;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterNumberActivity extends AppCompatActivity {

    ImageView logout;
    TextInputEditText number;

    Dialog dialog;
    Dialog dialog1,dialog2;
    Button btnDialogCancel,btnDialogLogout;
    Button btnDialogBack,btnDialogProceed,btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_number);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        number = findViewById(R.id.numberEdit);

        number = findViewById(R.id.numberEdit);

       logout=findViewById(R.id.logout);


       dialog=new Dialog(RegisterNumberActivity.this);
       dialog.setContentView(R.layout.custom_dialog_box);
       dialog. getWindow(). setLayout(ViewGroup. LayoutParams. WRAP_CONTENT, ViewGroup. LayoutParams. WRAP_CONTENT);

        dialog.getWindow() .setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog. setCancelable(false);

        btnDialogLogout = dialog.findViewById(R.id.btnDialogLogout);
        btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDialogLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Reset the login status
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Redirect to LoginActivity
                Intent intent = new Intent(RegisterNumberActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Close the current activity
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        dialog1=new Dialog(RegisterNumberActivity.this);
        dialog1.setContentView(R.layout.procced_box);
        dialog1. getWindow(). setLayout(ViewGroup. LayoutParams. WRAP_CONTENT, ViewGroup. LayoutParams. WRAP_CONTENT);

        dialog1.getWindow() .setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog1. setCancelable(false);

        btnDialogProceed = dialog1.findViewById(R.id.btnDialogProceed);
        btnDialogBack = dialog1.findViewById(R.id.btnDialogBack);

        btnDialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });

        btnDialogProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberString = number.getText().toString();
                if (numberString.length() == 10) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("ENUM", numberString);
                    myEdit.apply();

                    // Start the MainActivity after saving the number
                    Intent intent = new Intent(RegisterNumberActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Close the current activity
                    finish();
                } else {
                    //another layout is mandatory lateron
                    dialog2.show();
                }
            }
        });
        ///////////

        dialog2=new Dialog(RegisterNumberActivity.this);
        dialog2.setContentView(R.layout.alert);
        dialog2. getWindow(). setLayout(ViewGroup. LayoutParams. WRAP_CONTENT, ViewGroup. LayoutParams. WRAP_CONTENT);

        dialog2.getWindow() .setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog2. setCancelable(false);

        btnOk = dialog2.findViewById(R.id.btnDialogOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
                dialog2.dismiss();
            }
        });


    }



    public void saveNumber(View view) {
        dialog1.show();
    }


    public void saveNumber1(View view) {
        // Show the dialog with indications first
        showIndicationDialog();
    }

    public void saveNumber2(View view){
        showShield();
    }


    private void showShield() {
        new AlertDialog.Builder(this)
                .setTitle("Indications")
                .setMessage("1. Ensure your phone has an active internet connection." +
                        "\n2. Keep the app running in the background." +
                        "\n3. Only use the SOS feature in real emergencies. Misuse could cause unnecessary panic among your contacts."+
                        "\n4. Enable location services for accurate tracking."+
                        "\n5. In addition to using the app, practice basic safety measures such as avoiding dark or isolated areas, staying in groups when possible,"+
                        "\n6. While SafeShield is designed to help in emergencies, it is not a substitute for immediate help from authorities. Always contact local emergency services if you are in immediate danger.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // After the user clicks "OK", check if the number is valid
                    String numberString = number.getText().toString();
                    if (numberString.length() == 10) {
                        // Save the number and switch to MainActivity
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("ENUM", numberString);
                        myEdit.apply();
                        Toast.makeText(this, "Number Saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Enter Number to proceed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showIndicationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Emergency Services")
                .setMessage("1. 100: Police" +
                        "\n2. 101: Fire Brigade" +
                        "\n3. 102: Ambulance"+
                        "\n4. 1091: Women’s Helpline (for harassment or violence against women)"+
                        "\n5. 108: Emergency Response Services (for medical, police, and fire emergencies)"+
                        "\n6. 181: Women in Distress (National Helpline for women facing violence or abuse)"+
                        "\n7. 098: Childline (For reporting child abuse, neglect, and trafficking)" +
                        "\n8. 14567: Senior Citizen Helpline (for elderly abuse or emergency support)" +
                        "\n9. 1930: Cyber Fraud Helpline (for reporting online financial frauds)"+
                        "\n10. 155260: National Cyber Crime Helpline (for cyber-related crimes)"+
                        "\n11. 1800-2000-113: National Human Trafficking Helpline"+
                        "\n12. 1070: Disaster Management Helpline (National and State level)"+
                        "\n13. 9152987821: Kiran Mental Health Helpline (24/7 support for mental health issues)")
                .setPositiveButton("OK", (dialog, which) -> {
                    // After the user clicks "OK", check if the number is valid
                    String numberString = number.getText().toString();
                    if (numberString.length() == 10) {
                        // Save the number and switch to MainActivity
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("ENUM", numberString);
                        myEdit.apply();
                        Toast.makeText(this, "Number Saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Enter Number to proceed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(false)
                .show();
    }


}