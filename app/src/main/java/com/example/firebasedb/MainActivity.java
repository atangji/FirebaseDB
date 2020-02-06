package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Adapters.TicketAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Resolucion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvTicket;
    ArrayList<Ticket> tickets_array = new ArrayList<Ticket>();
    Button btnSede;
    TextView emptyTv;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabMenuCrearTicket;
    Usuario u;

    ArrayList<Sede> sedes_obj_array = new ArrayList<>();
    ArrayList<String> sedes_array = new ArrayList<>();
    int sedes;
    final static String EXTRA_USER = "USER";

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Método para inicializar la vista
        initViews();

        //Si tocamos fuera del menú se cierra
        fabMenu.setClosedOnTouchOutside(true);
        fabMenu.setForegroundGravity(Gravity.RIGHT);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            u = bundle.getParcelable(Constants.EXTRA_USER);

            //Controlamos el usuario que se loga. En caso de ser un usuario admin ocultamos el botón crear ticket
            if(Constants.ID_ADMIN.equals(u.getId())){

                fabMenuCrearTicket = (FloatingActionButton) findViewById(R.id.fabMenuCrearTicket);
                fabMenu.removeMenuButton(fabMenuCrearTicket);
                fabMenuCrearTicket.setVisibility(View.GONE);
            }

            rvTicket.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            //Método para cargar tickets del usuario logado
            cargarTicketFirebase();
            //Méotodo para cargar sedes de los tickets
            cargarSedes();

        }else{
            Toast.makeText(getApplicationContext(), "error al cargar usuarios", LENGTH_LONG).show();
        }


    }


    protected void initViews(){

        fabMenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        rvTicket = (RecyclerView)findViewById(R.id.rvTicket);
        rvTicket.setHasFixedSize(true);
        emptyTv = (TextView)findViewById(R.id.tvItemIDTicket);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    u = data.getParcelableExtra(Constants.EXTRA_USER);
                }
            }
        }
    }


    private void cargarSedes() {

        //Accedemos al nodo sede de firebase y leemos las sedes del usuario logado
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("sede");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot sedes : dataSnapshot.getChildren()) {
                    final Sede sede = sedes.getValue(Sede.class);
                    String usuario_id_sede="";
                    for(Map.Entry<String,Boolean> entry: sede.getUsuarios().entrySet()) {
                        usuario_id_sede= entry.getKey();
                        if(usuario_id_sede.equals(u.getId())){
                            sedes_obj_array.add(sede);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());

            }
        };

        mDatabase.orderByChild("fecha_creacion").addValueEventListener(postListener);
    }

    private void cargarTicketFirebase(){
        final Ticket ticket;

        //Accedemos al nodo ticket de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("ticket");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot tickets: dataSnapshot.getChildren()){
                   final Ticket ticket = tickets.getValue(Ticket.class);
                    for(Map.Entry<String,Boolean> entryUsuario: ticket.getUsuarios().entrySet()){

                        if(entryUsuario.getKey().equals(u.getId()) || Constants.ID_ADMIN.equals(u.getId())){

                            //Al objeto ticket le obtengo el HasMap de sede
                            for(Map.Entry<String,Boolean> entry: ticket.getSede().entrySet()){

                                String id_sede = entry.getKey();

                                FirebaseDatabase.getInstance().getReference("sede").child(id_sede).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Sede sede  = dataSnapshot.getValue(Sede.class);
                                        ticket.setSedeobj(sede);

                                        //Al objeto ticket le obtengo el HasMap de tipo
                                        for(Map.Entry<String,Boolean> entry: ticket.getTipo().entrySet()){

                                            String id_tipo = entry.getKey();

                                            FirebaseDatabase.getInstance().getReference("tipo").child(id_tipo).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                    Tipo tipo = dataSnapshot.getValue(Tipo.class);
                                                    ticket.setTipoobj(tipo);

                                                    FirebaseDatabase.getInstance().getReference("resolucion").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            for(DataSnapshot resoluciones: dataSnapshot.getChildren()) {
                                                                Resolucion resolucion  = resoluciones.getValue(Resolucion.class);

                                                                //Al objeto ticket le obtengo el HasMap de resuelto
                                                                for (Map.Entry<String, Boolean> entry : resolucion.getTicket().entrySet()) {
                                                                    String ticketId = entry.getKey();

                                                                    if (ticketId.equals(ticket.getId())) {
                                                                        ticket.setSolucionado(resolucion.getResuelto());
                                                                    }
                                                                }
                                                            }

                                                        tickets_array.add(ticket);
                                                        Collections.reverse(tickets_array);
                                                        TicketAdapter adapter = new TicketAdapter(tickets_array);
                                                        rvTicket.setAdapter(adapter);

                                                        if (tickets_array.size() == 0) {
                                                            rvTicket.setVisibility(View.GONE);
                                                            emptyTv.setVisibility(View.VISIBLE);
                                                        } else {
                                                            rvTicket.setVisibility(View.VISIBLE);
                                                            emptyTv.setVisibility(View.GONE);
                                                        }

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
        //Ordenamos los tickets por fecha de creación
        mDatabase.orderByChild("fecha_creacion").addValueEventListener(postListener);
    }


    public void clickCrearTicket(View v){

        //Método para comprobar si el usuario tiene sedes creadas, requisito previo para crear un ticket
        sedes = sedes_obj_array.size();

        if (sedes>0) {

            //en caso de tener sedes abrimos la pantalla sedes
            Intent i = new Intent(v.getContext(), InsertarTicketActivity.class);

            i.putExtra(Constants.EXTRA_USER, u);
            startActivity(i);

        }else{

            //en caso contrario avisamos que debe crear una sede previamente
            Toast.makeText(getApplicationContext(), "El usuario no tiene sedes, tienes que crear una sede para crear un ticket", LENGTH_LONG).show();
        }
    }

    public void clickAbrirSedes(View v){

        Intent i = new Intent(v.getContext(), SedeActivity.class);
        i.putExtra(Constants.EXTRA_USER, u);
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
                Intent i = new Intent(getApplicationContext(), PerfilUsuario.class);

                i.putExtra(Constants.EXTRA_USER, u);
                startActivityForResult(i,1);
                return true;

            case R.id.menu_item2:
                firebaseAuth.signOut();
                Intent i2=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i2);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
