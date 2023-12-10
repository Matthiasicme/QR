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

public class HomeActivity extends AppCompatActivity {
    private TextView tv;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Pacjenci");

//        tv = findViewById(R.id.scanResultTextView);
//
//        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Dodaj ValueEventListener do pobrania danych
//                databaseReference.child("pacjent1").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            // Pobierz dane pacjenta
//                            String imie = snapshot.child("imie").getValue(String.class);
//                            String nazwisko = snapshot.child("nazwisko").getValue(String.class);
//                            int wiek = snapshot.child("wiek").getValue(Integer.class);
//                            String choroba = snapshot.child("choroba").getValue(String.class);
//                            int pokoj = snapshot.child("pokoj").getValue(Integer.class);
//
//                            // Ustaw dane w TextView
//                            String pacjent1Data = "Imię: " + imie + "\n"
//                                    + "Nazwisko: " + nazwisko + "\n"
//                                    + "Wiek: " + wiek + "\n"
//                                    + "Choroba: " + choroba + "\n"
//                                    + "Pokój: " + pokoj;
//
//                            tv.setText(pacjent1Data);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("HomeActivity", "Error: " + error.getMessage());
//                    }
//                });
//            }
//        });

//        findViewById(R.id.add_notes).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddNotesDialog();
//            }
//        });

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

        findViewById(R.id.add_pacjent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPacjentDialog();
            }
        });
    }
//    private void showAddNotesDialog() {
//        // Utwórz okno dialogowe
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Dodaj notatkę");
//        builder.setMessage("Wprowadź identyfikator pacjenta i notatkę:");
//
//        // Dodaj pola do wprowadzenia identyfikatora pacjenta i notatki
//        final EditText pacjentIdInput = new EditText(this);
//        pacjentIdInput.setHint("Identyfikator pacjenta");
//        final EditText notatkiInput = new EditText(this);
//        notatkiInput.setHint("Notatki");
//
//        // Ustaw layout dla pól
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.addView(pacjentIdInput);
//        layout.addView(notatkiInput);
//        builder.setView(layout);
//
//        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String pacjentId = pacjentIdInput.getText().toString().trim();
//                String newNote = notatkiInput.getText().toString().trim();
//
//                if (!pacjentId.isEmpty() && !newNote.isEmpty()) {
//                    databaseReference.child(pacjentId).child("notatki").setValue(newNote);
//                }
//            }
//        });
//
//        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    private void showAddPacjentDialog() {
        // Utwórz okno dialogowe
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj pacjenta");
        builder.setMessage("Wprowadź dane nowego pacjenta:");

        // Dodaj pola do wprowadzenia danych pacjenta
        final EditText imieInput = new EditText(this);
        imieInput.setHint("Imię");
        final EditText nazwiskoInput = new EditText(this);
        nazwiskoInput.setHint("Nazwisko");
        final EditText wiekInput = new EditText(this);
        wiekInput.setHint("Wiek");
        final EditText chorobaInput = new EditText(this);
        chorobaInput.setHint("Choroba");
        final EditText pokojInput = new EditText(this);
        pokojInput.setHint("Pokój");

        // Ustaw layout dla pól
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(imieInput);
        layout.addView(nazwiskoInput);
        layout.addView(wiekInput);
        layout.addView(chorobaInput);
        layout.addView(pokojInput);
        builder.setView(layout);

        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imie = imieInput.getText().toString().trim();
                String nazwisko = nazwiskoInput.getText().toString().trim();
                String wiekStr = wiekInput.getText().toString().trim();
                String choroba = chorobaInput.getText().toString().trim();
                String pokojStr = pokojInput.getText().toString().trim();

                if (!imie.isEmpty() && !nazwisko.isEmpty() && !wiekStr.isEmpty() && !choroba.isEmpty() && !pokojStr.isEmpty()) {
                    // Odczytaj ostatnią wartość identyfikatora z bazy danych
                    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long currentTime = System.currentTimeMillis();
                            String pacjentId;

                            // Sprawdź, czy istnieją pacjenci w bazie danych
                            if (snapshot.getChildrenCount() > 0) {
                                // Odczytaj ostatnią wartość identyfikatora i zastosuj inkrementację
                                String lastId = snapshot.getChildren().iterator().next().getKey();
                                int lastNumber = Integer.parseInt(lastId.substring(7)); // Ignoruj "pacjent" i parsuj liczbę
                                int newNumber = lastNumber + 1;
                                pacjentId = "pacjent" + newNumber;
                            } else {
                                // Brak pacjentów w bazie danych, użyj czasu bieżącego
                                pacjentId = "pacjent" + currentTime;
                            }

                            // Zamień dane na odpowiednie typy
                            int wiek = Integer.parseInt(wiekStr);
                            int pokoj = Integer.parseInt(pokojStr);

                            // Dodaj nowego pacjenta do bazy danych
                            databaseReference.child(pacjentId).child("imie").setValue(imie);
                            databaseReference.child(pacjentId).child("nazwisko").setValue(nazwisko);
                            databaseReference.child(pacjentId).child("wiek").setValue(wiek);
                            databaseReference.child(pacjentId).child("choroba").setValue(choroba);
                            databaseReference.child(pacjentId).child("pokoj").setValue(pokoj);

                            // Informacja zwrotna o dodaniu pacjenta
                            String info = "Dodano nowego pacjenta:\n" +
                                    "ID: " + pacjentId + "\n" +
                                    "Imię: " + imie + "\n" +
                                    "Nazwisko: " + nazwisko + "\n" +
                                    "Wiek: " + wiek + "\n" +
                                    "Choroba: " + choroba + "\n" +
                                    "Pokój: " + pokoj;
                            tv.setText(info);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("HomeActivity", "Error: " + error.getMessage());
                        }
                    });
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
