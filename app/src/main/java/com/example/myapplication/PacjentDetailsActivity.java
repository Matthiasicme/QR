package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PacjentDetailsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView imieTextView, nazwiskoTextView, wiekTextView, chorobaTextView, pokojTextView, notatkiTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacjent_details);

        // Ustaw Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Dodaj przycisk cofania
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inicjalizacja Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Pacjenci");

        imieTextView = findViewById(R.id.imieTextView);
        nazwiskoTextView = findViewById(R.id.nazwiskoTextView);
        wiekTextView = findViewById(R.id.wiekTextView);
        chorobaTextView = findViewById(R.id.chorobaTextView);
        pokojTextView = findViewById(R.id.pokojTextView);
        notatkiTextView = findViewById(R.id.notatkiTextView);

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
                        String notatki = snapshot.child("notatki").getValue(String.class);

                        // Wyświetl dane w TextView
                        imieTextView.setText("Imię: " + imie);
                        nazwiskoTextView.setText("Nazwisko: " + nazwisko);
                        wiekTextView.setText("Wiek: " + wiek);
                        chorobaTextView.setText("Choroba: " + choroba);
                        pokojTextView.setText("Pokój: " + pokoj);
                        notatkiTextView.setText("Notatki: " + notatki);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        findViewById(R.id.add_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNotesDialog(pacjentId);
            }
        });

    }
    private void showAddNotesDialog(String pacjentId) {
        // Utwórz okno dialogowe
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj notatkę");
        builder.setMessage("Wprowadź notatkę dla " + pacjentId);

        // Dodaj pole do wprowadzenia notatki
        final EditText notatkiInput = new EditText(this);
        notatkiInput.setHint("Notatki");

        // Ustaw layout dla pola
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(notatkiInput);
        builder.setView(layout);

        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNote = notatkiInput.getText().toString().trim();

                if (!newNote.isEmpty()) {
                    // Pobierz aktualną wartość notatek z bazy danych
                    databaseReference.child(pacjentId).child("notatki").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String currentNotes = snapshot.getValue(String.class);

                            // Dodaj nową notatkę wraz z datą
                            String timestamp = getFormattedTimestamp();
                            String updatedNotes = currentNotes + "\n" + timestamp + ": " + newNote;

                            // Zaktualizuj notatki w bazie danych
                            databaseReference.child(pacjentId).child("notatki").setValue(updatedNotes);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Obsługa błędu odczytu z bazy danych
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

    private String getFormattedTimestamp() {
        // Ustaw format daty
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        // Pobierz bieżący znacznik czasu
        long currentTimeMillis = System.currentTimeMillis();
        // Sformatuj datę
        return dateFormat.format(new Date(currentTimeMillis));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
