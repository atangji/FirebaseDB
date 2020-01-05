package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvTicket;
    ArrayList<Ticket> tickets_array = new ArrayList<Ticket>();
    Button btnSede;
    FloatingActionButton fabSedes;
    FloatingActionMenu fabMenu;
    Usuario u;


    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);

        //Si tocamos fuera del menú se cierra
        fabMenu.setClosedOnTouchOutside(true);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            u = bundle.getParcelable(RegistroActivity.EXTRA_USER);

            rvTicket = (RecyclerView)findViewById(R.id.rvTicket);
            rvTicket.setHasFixedSize(true);
            rvTicket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            cargarTicketFirebase();

        }else{
            Toast.makeText(getApplicationContext(), "error al cargar usuarios", LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(resultCode==RESULT_OK){
           if(data != null){
               u = data.getParcelableExtra(Constants.EXTRA_USER);
           }
       }
    }

    private void cargarTicketFirebase(){
        final Ticket ticket;
        mDatabase = FirebaseDatabase.getInstance().getReference("ticket");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot tickets: dataSnapshot.getChildren()){
                   final Ticket ticket = tickets.getValue(Ticket.class);
                    for(Map.Entry<String,Boolean> entryUsuario: ticket.getUsuarios().entrySet()){
                        if(entryUsuario.getKey().equals(u.getId()) || Constants.ID_ADMIN.equals(u.getId())){


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


                                            tickets_array.add(ticket);
                                            TicketAdapter adapter = new TicketAdapter(tickets_array);
                                            rvTicket.setAdapter(adapter);
                                            adapter.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                 Ticket t = tickets_array.get(rvTicket.getChildAdapterPosition(v));
                                                 Intent i = new Intent(getApplicationContext(), DetalleTicketActivity.class);
                                                 i.putExtra("TICKET", t);
                                                 i.putExtra(RegistroActivity.EXTRA_USER, u);
                                                 startActivity(i);
                                                }
                                            });
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


    public void clickCrearTicket(View v){

        Intent i = new Intent(v.getContext(), InsertarTicketActivity.class);
        startActivity(i);
    }

    public void clickAbrirSedes(View v){

        Intent i = new Intent(v.getContext(), SedeActivity.class);
        i.putExtra(RegistroActivity.EXTRA_USER, u);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        if(Constants.ID_ADMIN.equals(u.getId())){

            inflater.inflate(R.menu.admin_menu, menu);
        }else{

            inflater.inflate(R.menu.user_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item1:
                Toast.makeText(this, "Perfil usuario", LENGTH_SHORT).show();
                return true;

            case R.id.menu_item2:
                firebaseAuth.signOut();
                Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
