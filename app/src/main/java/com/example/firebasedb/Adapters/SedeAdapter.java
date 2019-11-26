package com.example.firebasedb.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.firebasedb.Model.Sede;
import com.example.firebasedb.Model.Ticket;
import com.example.firebasedb.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SedeAdapter  extends RecyclerView.Adapter<SedeAdapter.ViewHolderSede>
        implements View.OnClickListener {


    private ArrayList<Sede> datos;
    private View.OnClickListener listener;


    public SedeAdapter(ArrayList<Sede> datos) {
        this.datos = datos;
    }

    @Override
    public SedeAdapter.ViewHolderSede onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_sede, viewGroup, false);

        itemView.setOnClickListener(this);
        SedeAdapter.ViewHolderSede tvh = new SedeAdapter.ViewHolderSede(itemView, viewGroup.getContext());

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
    public void onBindViewHolder(SedeAdapter.ViewHolderSede viewHolder, int pos) {
        Sede item = datos.get(pos);
        viewHolder.bindSede(item);
    }


    @Override
    public int getItemCount() {
        return datos.size();
    }


    public class ViewHolderSede extends RecyclerView.ViewHolder {
        private View mView;
        TextView tvDireccion, tvCP, tvPoblacion;

        private Context mContext;
        Sede sede;

        public ViewHolderSede(View itemView, Context c) {
            super(itemView);
            this.mView = itemView;
            tvDireccion = (TextView) itemView.findViewById(R.id.tvDireccion);
            tvCP = (TextView) itemView.findViewById(R.id.tvCP);
            tvPoblacion = (TextView) itemView.findViewById(R.id.tvPoblacion);
            this.mContext = c;


        }

        public void bindSede(Sede s) {
            sede = s;
            tvDireccion.setText(s.getDireccion());
            tvPoblacion.setText(s.getPoblobj().getPoblacion());
            tvCP.setText(s.getPoblobj().getCp());
        }

    }
}
