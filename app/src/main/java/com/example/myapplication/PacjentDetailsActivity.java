package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PacjentDetailsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView imieTextView, nazwiskoTextView, wiekTextView, chorobaTextView, pokojTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacjent_details);

        // Inicjalizacja Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Pacjenci");

        imieTextView = findViewById(R.id.imieTextView);
        nazwiskoTextView = findViewById(R.id.nazwiskoTextView);
        wiekTextView = findViewById(R.id.wiekTextView);
        chorobaTextView = findViewById(R.id.chorobaTextView);
        pokojTextView = findViewById(R.id.pokojTextView);

        // Pobierz identyfikator pacjenta z Intentu
        String pacjentId = getIntent().getStringExtra("PACJENT_ID");

        // Pobierz dane pacjenta z bazy danych
        if (pacjentId != null) {
            databaseReference.child(pacjentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imie = snapshot.child("imie").getValue(String.class);
                        String nazwisko = snapshot.child("nazwisko").getValue(String.class);
                        int wiek = snapshot.child("wiek").getValue(Integer.class);
                        String choroba = snapshot.child("choroba").getValue(String.class);
                        int pokoj = snapshot.child("pokoj").getValue(Integer.class);

                        // Wyświetl dane w TextView
                        imieTextView.setText("Imię: " + imie);
                        nazwiskoTextView.setText("Nazwisko: " + nazwisko);
                        wiekTextView.setText("Wiek: " + wiek);
                        chorobaTextView.setText("Choroba: " + choroba);
                        pokojTextView.setText("Pokój: " + pokoj);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
