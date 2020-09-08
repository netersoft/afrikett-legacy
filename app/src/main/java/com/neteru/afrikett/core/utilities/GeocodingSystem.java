package com.neteru.afrikett.core.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.neteru.afrikett.core.interfaces.GeocodingSystemListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class GeocodingSystem {
    private Geocoder geocoder;
    private List<Address> addresses;
    private GeocodingSystemListener listener;

    private GeocodingSystem(Context context){
        addresses = new ArrayList<>();
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }

    public static GeocodingSystem getInstance(Context c){

        return new GeocodingSystem(c);
    }

    public void convertCoordinatesToAddress(double latitude, double longitude, GeocodingSystemListener l){
        listener = l;

        try {

            listener.onTaskStarted();
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {

            listener.onErrorOccurred();
            e.printStackTrace();
        }

        listener.onTaskCompleted(addresses.get(0));
    }

    public void convertAddressToCoordinates(String address, GeocodingSystemListener l){
        listener = l;

        try {

            listener.onTaskStarted();
            addresses = geocoder.getFromLocationName(address, 1);

        } catch (IOException e) {

            listener.onErrorOccurred();
            e.printStackTrace();
        }
        if(addresses.size() > 0) {

            listener.onTaskCompleted(addresses.get(0));

        }else {
            listener.onErrorOccurred();
        }

    }

}
