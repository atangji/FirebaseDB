package com.example.firebasedb.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Poblacion implements Parcelable {

    String id;
    String cp;
    String poblacion;
    String provincia;


    public Poblacion() {
    }

    public Poblacion(String id, String cp, String poblacion, String provincia) {
        this.id = id;
        this.cp = cp;
        this.poblacion = poblacion;
        this.provincia = provincia;
    }

    protected Poblacion(Parcel in) {
        id = in.readString();
        cp = in.readString();
        poblacion = in.readString();
        provincia = in.readString();
    }

    public static final Creator<Poblacion> CREATOR = new Creator<Poblacion>() {
        @Override
        public Poblacion createFromParcel(Parcel in) {
            return new Poblacion(in);
        }

        @Override
        public Poblacion[] newArray(int size) {
            return new Poblacion[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(cp);
        dest.writeString(poblacion);
        dest.writeString(provincia);
    }
}
