package com.example.pemipiljagung;

import static android.icu.text.ListFormatter.Type.OR;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {

    private Button btnWeightTarget, btnOff, btnOn, btnAutoMode;
    private EditText txtWeightTarget;
    private TextView viewWeightRead;
    protected static Float valWeightRead;
    protected static Integer weightTarget, valWeightTarget, valMode, valServo, valRelay;
    protected static FirebaseDatabase database = FirebaseDatabase.getInstance();

    protected static LinearLayout layoutWeightTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewWeightRead = findViewById(R.id.txtWeightRead);
        btnWeightTarget = findViewById(R.id.btnWeightTarget);
        txtWeightTarget = findViewById(R.id.txtWeightTarget);
        btnOff = findViewById(R.id.btnOff);
        btnOn = findViewById(R.id.btnOn);
        btnAutoMode = findViewById(R.id.btnAutoMode);
        layoutWeightTarget = findViewById(R.id.lytWeightTarget);

        readWrite(); //Mengambil function read write
        btnMode(); //Mengambil function button mode


    }

    private void readWrite() {

        //variabel myRef untuk koneksi firebase
        DatabaseReference myRef = database.getReference();

        //myRef Lister. tempat program myRef
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //snapshot untuk baca field realtime database firebase
                valMode = snapshot.child("mode").getValue(Integer.class);
                valServo = snapshot.child("servo").getValue(Integer.class);
                valRelay = snapshot.child("relay").getValue(Integer.class);
                valWeightRead = snapshot.child("weight_read").getValue(Float.class);
                valWeightTarget = snapshot.child("weight_target").getValue(Integer.class);

                //fungsi perulangan mengacu pada mode
                if (valMode == 0) {
                    btnOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_pressed));
                    btnOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));
                    btnAutoMode.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));
                    myRef.child("relay").setValue(0);
                    if (valMode == 0 && valRelay == 0) {
                        myRef.child("servo").setValue(0);
                    }
                } else if (valMode == 1) {
                    btnOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_pressed));
                    btnOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));
                    btnAutoMode.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));
                    myRef.child("relay").setValue(1);
                    if (valMode == 1 && valRelay == 1) {
                        myRef.child("servo").setValue(1);
                    }
                } else if (valMode == 2) {
                    btnAutoMode.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_pressed));
                    btnOn.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));
                    btnOff.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.button_default));

                }

                //perulangan dipisah agar tidak bingung
                //perulangan untuk Automode Sistem
                if (valMode == 1) {
                    viewWeightRead.setText(String.valueOf(valWeightRead));
                }
                if (valMode == 2 && valWeightTarget != null) {
                    viewWeightRead.setText(String.valueOf(valWeightRead));
                    if (valWeightRead >= Float.parseFloat(String.valueOf(valWeightTarget))) {
                        myRef.child("servo").setValue(0);
                        myRef.child("weight_target").setValue(10);
                        txtWeightTarget.setText("");
                        btnOn.setEnabled(true);
                        btnOff.setEnabled(true);
                        Toast.makeText(MainActivity.this,
                                "Auto Mode Telah Selesai", Toast.LENGTH_LONG).show();
                        //notificationAutoModeComplete();
                    } else {
                        myRef.child("servo").setValue(1);
                    }
                }

                btnWeightTarget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        weightTarget = Integer.parseInt(txtWeightTarget.getText().toString());
                        if (weightTarget <= 10) {
                            Toast.makeText(MainActivity.this,
                                    "Harus lebih dari 10 gram", Toast.LENGTH_LONG).show();
                        } else {
                            myRef.child("weight_target").setValue(weightTarget);
                            Toast.makeText(MainActivity.this,
                                    "Nilai berat sudah terinput", Toast.LENGTH_LONG).show();
                            btnOn.setEnabled(false);
                            btnOff.setEnabled(false);
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void btnMode() {

        DatabaseReference myRef = database.getReference("mode");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                btnOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtWeightTarget.setEnabled(false);
                        btnWeightTarget.setEnabled(false);
                        layoutWeightTarget.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Mode OFF", Toast.LENGTH_LONG).show();
                        viewWeightRead.setText("----");
                        myRef.setValue(0);
                    }
                });

                btnOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtWeightTarget.setEnabled(false);
                        btnWeightTarget.setEnabled(false);
                        layoutWeightTarget.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Mode ON", Toast.LENGTH_LONG).show();

                        myRef.setValue(1);
                    }
                });

                btnAutoMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtWeightTarget.setEnabled(true);
                        btnWeightTarget.setEnabled(true);
                        layoutWeightTarget.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,
                                "Auto Mode On, masukkan input", Toast.LENGTH_LONG).show();

                        myRef.setValue(2);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
