package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Pacjenci");

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
                            Pacjent pacjent = new Pacjent();
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
                            showToast(info);
                            sendPacjentDataToServer(pacjent);

                        }
                        private void showToast(String message) {
                            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void sendPacjentDataToServer(Pacjent pacjent) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<Void> call = apiService.dodajPacjenta(pacjent);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Dodano pacjenta poprawnie");
                } else {
                    showToast("Błąd podczas dodawania pacjenta");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Błąd komunikacji z serwerem");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}
