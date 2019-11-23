package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvTicket;
    ArrayList<Ticket> tickets_array = new ArrayList<Ticket>();
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTicket = (RecyclerView)findViewById(R.id.rvTicket);
        rvTicket.setHasFixedSize(true);
        rvTicket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        cargarTicketFirebase();


    }


    private void cargarTicketFirebase(){
        final Ticket ticket;
        mDatabase = FirebaseDatabase.getInstance().getReference("ticket");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot tickets: dataSnapshot.getChildren()){
                   final Ticket ticket = tickets.getValue(Ticket.class);

                    //Al objeto sede le obtengo el HasMap de poblacion
                    for(Map.Entry<String,Boolean> entry: ticket.getSede().entrySet()){

                        String id_sede = entry.getKey();

                        FirebaseDatabase.getInstance().getReference("sede").child(id_sede).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Sede sede  = dataSnapshot.getValue(Sede.class);
                                ticket.setSedeobj(sede);

                                //Al objeto sede le obtengo el HasMap de poblacion
                                for(Map.Entry<String,Boolean> entry: ticket.getTipo().entrySet()){

                                    String id_tipo = entry.getKey();

                                    FirebaseDatabase.getInstance().getReference("tipo").child(id_tipo).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Tipo tipo  = dataSnapshot.getValue(Tipo.class);
                                            ticket.setTipoobj(tipo);
                                            Log.i("asads","asdas");

                                            tickets_array.add(ticket);
                                            TicketAdapter adapter = new TicketAdapter(tickets_array);
                                            rvTicket.setAdapter(adapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i("ERROR SEDE", databaseError.getMessage());
                            }
                        });
                    }





                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);
    }


}
