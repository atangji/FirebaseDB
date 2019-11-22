package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tipo implements Parcelable {

    String id;
    String tipo_nombre;

    public Tipo() {

    }

    public Tipo(String id, String tipo_nombre) {
        this.id = id;
        this.tipo_nombre = tipo_nombre;
    }

    protected Tipo(Parcel in) {
        id = in.readString();
        tipo_nombre = in.readString();
    }

    public static final Creator<Tipo> CREATOR = new Creator<Tipo>() {
        @Override
        public Tipo createFromParcel(Parcel in) {
            return new Tipo(in);
        }

        @Override
        public Tipo[] newArray(int size) {
            return new Tipo[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo_nombre() {
        return tipo_nombre;
    }

    public void setTipo_nombre(String tipo_nombre) {
        this.tipo_nombre = tipo_nombre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tipo_nombre);
    }
}
