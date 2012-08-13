package com.bikebonus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;


public class UbicationParams implements Serializable{
	
	private static final long serialVersionUID = 00000000;
	
	private static final char DECIMAL_DEGREES = 'F';

	private boolean gpsEnabled = false;
    private boolean networkEnabled = false;
    
    private int actualZoom = 10;
    private IGeoPoint actualPoint = new GeoPoint(41396815, 2175714);
    private IGeoPoint centerPoint = new GeoPoint(41396815, 2175714);
    private IGeoPoint currentLocation = new GeoPoint(0,0);
    ArrayList<BikeStation> bikeStationArray = new ArrayList<BikeStation>();
    
    public boolean getGpsEnabled(){
    	return gpsEnabled;
    }
    
    public void setGpsEnabled(boolean b){
    	gpsEnabled = b;
    }
    
    public boolean getNetworkEnabled(){
    	return networkEnabled;
    }
    
    public void setNetworkEnabled(boolean b){
    	networkEnabled = b;
    }
     
    public IGeoPoint getCurrentLocation(){
    	return currentLocation;
    }
    
    public void setCurrentLocation(IGeoPoint p){
    	currentLocation=p;
    }
    
    public IGeoPoint getCenterPoint(){
    	return centerPoint;
    }
    
    public void setCenterPoint(IGeoPoint p){
    	centerPoint=p;
    }
    
    public int getActualZoom(){
    	return actualZoom;
    }
    
    public void setActualZoom(int z){
    	actualZoom=z;
    }
    
    public IGeoPoint getActualPoint(){
    	return actualPoint;
    }
    
    public void setActualPoint(IGeoPoint p){
    	actualPoint=p;
    }

    public void setNumberBikeStations (int numStations){
    		bikeStationArray = new ArrayList<BikeStation>(numStations);
    }
    
    public void addBikeStation (int id, String name, int lat, int lng, int bikes, int free, String timestamp){ 
    	bikeStationArray.add(new BikeStation(id, name, lat, lng, bikes, free, timestamp));
    }
    
    public Iterator<BikeStation> getIteratorBikeStations(){
    	return bikeStationArray.iterator();
    }

}