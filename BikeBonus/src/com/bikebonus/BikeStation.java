package com.bikebonus;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

public class BikeStation {

	/*id: CityBikes station id
	name: Station name
	lat: Latitude in E6 format
	lng: Longitude in E6 format
	bikes: Number of bikes in the station
	free: Number of free slots
	timestamp: The last time the station has been updated*/
	
	private int id, numberBikes, freeSlots; 
	private String stationName;
	private IGeoPoint stationUbication;
	private String lastUpdate;
	
	public BikeStation (int id, String name, int lat, int lng, int bikes, int free, String timestamp){
		this.id = id;
		this.stationName = name;
		this.stationUbication = new GeoPoint(lat,lng);
		this.numberBikes=bikes;
		this.freeSlots=free;
		this.lastUpdate=timestamp;
	}
	
	public int getStationId(){
		return this.id;
	}
	
	public String getStationName(){
		return this.stationName;
	}
	
	public GeoPoint getStationUbication(){
		return (GeoPoint)this.stationUbication;
	}
	
	public String getLastUpdate(){
		return this.lastUpdate;
	}

	public int getFreeSlots(){
		return this.freeSlots;
	}
	
	public int getNumberBikes(){
		return this.numberBikes;
	}
	
	
}
