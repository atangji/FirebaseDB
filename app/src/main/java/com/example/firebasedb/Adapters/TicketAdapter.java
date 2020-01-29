package com.example.firebasedb.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class TicketAdapter  extends RecyclerView.Adapter<TicketAdapter.ViewHolderTicket>
        implements View.OnClickListener {


    private ArrayList<Ticket> datos;
    private View.OnClickListener listener;


    public TicketAdapter(ArrayList<Ticket> datos) {
        this.datos = datos;
    }

    @Override
    public ViewHolderTicket onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_ticket, viewGroup, false);

        itemView.setOnClickListener(this);
        ViewHolderTicket tvh = new ViewHolderTicket(itemView,viewGroup.getContext());

        return tvh;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderTicket viewHolder, int pos) {
        Ticket item = datos.get(pos);
        viewHolder.bindTicket(item);
    }


    @Override
    public int getItemCount() {
        return datos.size();
    }


public class ViewHolderTicket extends RecyclerView.ViewHolder {
    private View mView;
    TextView tvSede,tvFecha, tvTipo,tvId;

    private Context mContext;
    Ticket ticket;

    public ViewHolderTicket(View itemView, Context c) {
        super(itemView);
        this.mView = itemView;
        tvTipo = (TextView)itemView.findViewById(R.id.tvTipo);
        tvFecha = (TextView)itemView.findViewById(R.id.tvFecha);
        tvSede = (TextView)itemView.findViewById(R.id.tvSede);
        tvId = (TextView)itemView.findViewById(R.id.tvItemID);
        this.mContext=c;



    }

    public void bindTicket(Ticket t){
        ticket=t;
        tvFecha.setText(t.getFecha_creacion());
         tvSede.setText(t.getSedeobj().getDireccion());
         tvTipo.setText(t.getTipoobj().getTipo_nombre());
         tvId.setText("#"+t.getId());
    }




}

}


