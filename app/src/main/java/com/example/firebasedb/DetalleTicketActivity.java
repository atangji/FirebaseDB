package com.example.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedb.Adapters.SedeAdapter;
import com.example.firebasedb.Model.Poblacion;
import com.example.firebasedb.Model.Resolucion;
import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.Model.Tipo;
import com.example.firebasedb.Model.Usuario;
import com.example.firebasedb.Utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DetalleTicketActivity extends AppCompatActivity {

    Ticket t;
    TextView tvTipoTicket;
    TextView tvSedeTicket;
    TextView tvFechaCreacion;
    TextView tvDetalleTicket;
    TextView tvFechaSolTicket;
    TextView tvSolucionTicket;
    TextView tvMailTicket;
    TextView tvTelefonoTicket;
    TextView tvUsuarioTicket;
    TextView tvDatosContacto;
    EditText etSolucion;
    Button btnInsertar;
    CheckBox ckboxResuelto;
    String usuarioTicket;
    private Usuario u;
    private DatabaseReference mDatabase;
    private String fecha = "1900-01-01";
    private boolean resuelto = false;
    boolean noTieneResolucion = true;
    String id_res;
    Resolucion res;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detalle);
        initViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            t = bundle.getParcelable(Constants.EXTRA_TICKET);
            u = bundle.getParcelable(Constants.EXTRA_USER);
            tvTipoTicket.setText(t.getTipoobj().getTipo_nombre());
            tvSedeTicket.setText((t.getSedeobj().getDireccion()));
            tvFechaCreacion.setText(t.getFecha_creacion());
            tvDetalleTicket.setText(t.getComentario());


            setTitle("ID Ticket: " + t.getId());
            cargarUsuarioTicket();
            cargarResolucion();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Intent back = getIntent();
                back.putExtra(Constants.EXTRA_USER, u);
                setResult(RESULT_OK, back);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        tvTipoTicket = (TextView) findViewById(R.id.tvTipoTicket);
        tvSedeTicket = (TextView) findViewById(R.id.tvSedeTicket);
        tvFechaCreacion = (TextView) findViewById(R.id.tvFechaCreacion);
        tvFechaSolTicket = (TextView) findViewById(R.id.tvFechaSolTicket);
        tvSolucionTicket = (TextView) findViewById(R.id.tvSolucionTicket);
        tvDetalleTicket = (TextView) findViewById(R.id.tvDetalleTicket);
        etSolucion = (EditText) findViewById(R.id.etTextSolucion);
        btnInsertar = (Button) findViewById(R.id.btnInsertarSol);
        ckboxResuelto = (CheckBox) findViewById(R.id.ckboxResuelto);
        tvMailTicket = (TextView) findViewById(R.id.tvMailTicket);
        tvTelefonoTicket = (TextView) findViewById(R.id.tvTelefonoTicket);
        tvUsuarioTicket = (TextView) findViewById(R.id.tvNombreUsuarioTicket);
        tvDatosContacto = (TextView) findViewById(R.id.tvDatosContacto);

        progressDialog = new ProgressDialog(this);

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ckboxResuelto.isChecked()) {
                    resuelto = true;
                }

                if (TextUtils.isEmpty(etSolucion.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Debes de rellenar la solucion", Toast.LENGTH_LONG).show();

                } else {

                    HashMap<String, Boolean> idTicket = new HashMap<>();
                    idTicket.put(t.getId(), true);

                    progressDialog.setMessage("Guardando ticket..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    if (noTieneResolucion) {
                        id_res = UUID.randomUUID().toString();
                        setFechaHora();
                        Resolucion nuevaResolucion = new Resolucion(id_res, fecha, "(" + fecha + "): " + etSolucion.getText().toString(), "backend", idTicket, resuelto);
                        mDatabase = FirebaseDatabase.getInstance().getReference();

                        //Accedemos al nodo resolucion de firebase
                        mDatabase.child("resolucion").child(id_res).setValue(nuevaResolucion, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Se insertó la resolucion con éxito", Toast.LENGTH_LONG).show();

                                    Intent back = getIntent();
                                    back.putExtra(Constants.EXTRA_USER, u);
                                    setResult(RESULT_OK, back);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Ups¡ No se ha podido guardar la resolucion", Toast.LENGTH_LONG).show();

                                }

                            }

                        });


                    } else {
                        setFechaHora();

                        res.setComentario((res.getComentario().concat("\n" + "(" + fecha + "): " + etSolucion.getText().toString())));
                        res.setFecha(fecha);
                        res.setResuelto(resuelto);
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        //Accedemos al nodo resolución de firebase
                        mDatabase.child("resolucion").child(res.getId()).setValue(res, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Se insertó la resolucion con éxito", Toast.LENGTH_LONG).show();

                                    Intent back = getIntent();
                                    back.putExtra(Constants.EXTRA_USER, u);
                                    setResult(RESULT_OK, back);
                                    finish();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Ups¡ No se ha podido guardar la resolucion", Toast.LENGTH_LONG).show();
                                }
                            }

                        });

                    }

                }
            }
        });
    }

    private void initResolucion(Resolucion r) {

        tvFechaSolTicket.setText(r.getFecha());
        tvSolucionTicket.setText(r.getComentario());
        ckboxResuelto.setChecked(r.getResuelto());
        resuelto = r.getResuelto();
        res = r;

        if (u.getId().equals(Constants.ID_ADMIN)) {

            if (resuelto) {

                etSolucion.setVisibility(View.GONE);
                btnInsertar.setVisibility(View.GONE);
                ckboxResuelto.setEnabled(false);

            } else {

                etSolucion.setVisibility(View.VISIBLE);
                btnInsertar.setVisibility(View.VISIBLE);
                ckboxResuelto.setEnabled(true);

            }


            tvUsuarioTicket.setVisibility(View.VISIBLE);
            tvDatosContacto.setVisibility(View.VISIBLE);
            tvTelefonoTicket.setVisibility(View.VISIBLE);
            tvMailTicket.setVisibility(View.VISIBLE);

        } else {

            //En caso de no tratarse de un usuario administrador ocultamos los datos de contacto

            etSolucion.setVisibility(View.GONE);
            tvUsuarioTicket.setVisibility(View.GONE);
            tvDatosContacto.setVisibility(View.GONE);
            tvTelefonoTicket.setVisibility(View.GONE);
            tvMailTicket.setVisibility(View.GONE);
            btnInsertar.setVisibility(View.GONE);
            ckboxResuelto.setEnabled(false);

        }

    }

    private void setFechaHora() {
        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            fecha = dtf.format(now);
        } else {
            //fecha = "2019-11-28";
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            fecha = format1.format(cal.getTime());
        }

    }

    private void enabledResolucion() {

        if (u.getId().equals(Constants.ID_ADMIN)) {

            tvUsuarioTicket.setVisibility(View.VISIBLE);
            tvDatosContacto.setVisibility(View.VISIBLE);
            tvTelefonoTicket.setVisibility(View.VISIBLE);
            tvMailTicket.setVisibility(View.VISIBLE);


            if (resuelto) {

                etSolucion.setVisibility(View.GONE);
                btnInsertar.setVisibility(View.GONE);
                ckboxResuelto.setVisibility(View.VISIBLE);
                ckboxResuelto.setEnabled(false);
                setFechaHora();
                tvFechaSolTicket.setText(fecha);

            } else {

                etSolucion.setVisibility(View.VISIBLE);
                btnInsertar.setVisibility(View.VISIBLE);
                ckboxResuelto.setVisibility(View.VISIBLE);
                ckboxResuelto.setEnabled(true);
                setFechaHora();
                tvFechaSolTicket.setText(fecha);
            }

        } else {

            etSolucion.setVisibility(View.GONE);
            btnInsertar.setVisibility(View.GONE);
            ckboxResuelto.setVisibility(View.GONE);

            etSolucion.setVisibility(View.GONE);
            tvUsuarioTicket.setVisibility(View.GONE);
            tvDatosContacto.setVisibility(View.GONE);
            tvTelefonoTicket.setVisibility(View.GONE);
            tvMailTicket.setVisibility(View.GONE);

            tvFechaSolTicket.setText("Pendiente");
            tvSolucionTicket.setText("Pendiente de revisión");
        }
    }

    private void cargarResolucion() {

        //Accedemos al nodo resolucion de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("resolucion");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot resoluciones : dataSnapshot.getChildren()) {
                    final Resolucion resolucion = resoluciones.getValue(Resolucion.class);
                    //Al objeto resolucion le obtengo el HasMap de ticket
                    for (Map.Entry<String, Boolean> entry : resolucion.getTicket().entrySet()) {

                        String id_ticket = entry.getKey();

                        if (id_ticket.equals(t.getId())) {
                            noTieneResolucion = false;
                            initResolucion(resolucion);

                        }
                    }
                }

                if (noTieneResolucion) {
                    enabledResolucion();
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

    private void cargarUsuarioTicket() {

        //Buscamos con este método los datos del usuario que creó el ticket. ya que incluirlo en la consulta anterior
        //suponia anidar demasiados nodos.

        String id_ticket = t.getId();

        //Accedemos al nodo de firebase tickets al idticket del detalle
        mDatabase = FirebaseDatabase.getInstance().getReference("ticket").child(id_ticket);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                     Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    //Al objeto ticket le obtengo el HasMap de sede
                    for(Map.Entry<String,Boolean> entryUsuario: ticket.getUsuarios().entrySet()){

                        //Obtenemos el usuario que creo el ticket
                        String id_usuario = entryUsuario.getKey();

                        //Accedemos al nodo usuarios para obtener los datos
                        FirebaseDatabase.getInstance().getReference("usuarios").child(id_usuario).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Usuario user = dataSnapshot.getValue(Usuario.class);

                                    //Rellenamos con los datos del usuario que ha creado el ticket
                                    tvUsuarioTicket.setText("Persona de contacto: "+user.getNombre());
                                    tvMailTicket.setText("E-mail: "+user.getEmail());
                                    tvTelefonoTicket.setText("Teléfono: "+user.getTelefono());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i("ERROR USUARIOS", databaseError.getMessage());
                            }

                        });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("ERROR TICKETS", databaseError.getMessage());
            }
        };
        mDatabase.addValueEventListener(postListener);
    }
}




