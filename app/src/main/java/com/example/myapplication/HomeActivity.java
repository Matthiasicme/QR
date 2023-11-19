package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {
    private TextView tv;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Pacjenci");

        tv = findViewById(R.id.scanResultTextView);

        findViewById(R.id.add_pacjent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dodaj ValueEventListener do pobrania danych
                databaseReference.child("pacjent1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Pobierz dane pacjenta
                            String imie = snapshot.child("imie").getValue(String.class);
                            String nazwisko = snapshot.child("nazwisko").getValue(String.class);
                            int wiek = snapshot.child("wiek").getValue(Integer.class);
                            String choroba = snapshot.child("choroba").getValue(String.class);
                            int pokoj = snapshot.child("pokoj").getValue(Integer.class);

                            // Ustaw dane w TextView
                            String pacjent1Data = "Imię: " + imie + "\n"
                                    + "Nazwisko: " + nazwisko + "\n"
                                    + "Wiek: " + wiek + "\n"
                                    + "Choroba: " + choroba + "\n"
                                    + "Pokój: " + pokoj;

                            tv.setText(pacjent1Data);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeActivity", "Error: " + error.getMessage());
                    }
                });
            }
        });

        findViewById(R.id.add_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNotesDialog();
            }
        });

        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private void showAddNotesDialog() {
        // Utwórz okno dialogowe
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj notatkę");
        builder.setMessage("Wprowadź identyfikator pacjenta i notatkę:");

        // Dodaj pola do wprowadzenia identyfikatora pacjenta i notatki
        final EditText pacjentIdInput = new EditText(this);
        pacjentIdInput.setHint("Identyfikator pacjenta");
        final EditText notatkiInput = new EditText(this);
        notatkiInput.setHint("Notatki");

        // Ustaw layout dla pól
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(pacjentIdInput);
        layout.addView(notatkiInput);
        builder.setView(layout);

        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pacjentId = pacjentIdInput.getText().toString().trim();
                String newNote = notatkiInput.getText().toString().trim();

                if (!pacjentId.isEmpty() && !newNote.isEmpty()) {
                    databaseReference.child(pacjentId).child("notatki").setValue(newNote);
                }
            }
        });

        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
