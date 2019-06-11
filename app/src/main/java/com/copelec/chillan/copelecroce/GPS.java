package com.copelec.chillan.copelecroce;


import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

public class GPS implements Serializable{

    private List<Double> longitud ;
    private List<Double> latitud ;

    public GPS(List<Double> latitud, List<Double> longitud){
        this.latitud=latitud;
        this.longitud=longitud;
    }

    public List<Double> getLatitud(){

        return  latitud;
    }
    public List<Double> getLongitud(){
        return  longitud;
    }
}
