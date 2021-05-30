package com.example.pretect.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretect.R;

import java.util.ArrayList;

public class AdapterEstacionesClass extends RecyclerView.Adapter<AdapterEstacionesClass.EstacionViewHolder>{
    ArrayList<Estacion> list;

    public AdapterEstacionesClass(ArrayList<Estacion> list){ this.list = list; }

    @NonNull
    @Override
    public EstacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estacion_policia, parent, false);
        return new EstacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstacionViewHolder holder, int position){
        holder.setLocalidad(list.get(position).getCiudad_municipio());
        holder.setCaiNombre(list.get(position).getTipo_unidad());
        holder.setCaiCelular(list.get(position).getNumero_celular_cuadrante());
    }

    @Override
    public int getItemCount(){ return list.size(); }

    class EstacionViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView localidad;
        TextView caiNombre;
        TextView caiCelular;

        public EstacionViewHolder(@NonNull android.view.View itemView){
            super(itemView);
            mView = itemView;
            localidad = mView.findViewById(R.id.localidad);
            caiNombre = mView.findViewById(R.id.caiNombre);
            caiCelular = mView.findViewById(R.id.caiCelular);
        }

        public void setLocalidad(String localidadT){
            if(localidadT != null){
                localidad.setText(localidadT);
            }
        }
        public void setCaiNombre(String caiNombreT){
            if(caiNombreT != null){
                caiNombre.setText(caiNombreT);
            }
        }
        public void setCaiCelular(String caiCelularT){
            if(caiCelularT != null){
                caiCelular.setText(caiCelularT);
            }
        }
    }
}
