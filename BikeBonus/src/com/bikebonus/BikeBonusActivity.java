package com.bikebonus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.*;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.*;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import android.widget.TextView;
import android.widget.Toast;


public class BikeBonusActivity extends Activity {
	
	private MapController mapController;
    private MapView mapView;
    
    // private TextView info;
    // private PathOverlay myPath;
    
    private LocationManager locManager;
    private LocationListener locListener;
    private ItemizedOverlay<OverlayItem> bikeStationOverlay;
    private ArrayList<OverlayItem> bikeStationItems ;
    private ResourceProxy bikeStationResourceProxy;
    private Resources resources;
    
    private Timer timeBikeStationUpdates;
    
    
    public UbicationParams ubicParams;
    
    //List<OverlayItem> pointsList = new ArrayList<OverlayItem>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Getting objects from the layout
        setContentView(R.layout.main);
        
        // Getting a resources object to save the time of calling "getResources()" 
        resources = getResources();
        
        mapView = (MapView) findViewById(R.id.mapview);
        // Elegir proveedor de mapa. MAPQUESTOSM parece menos "tragador de ancho de banda"
        //mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        // Quitar controles de zoom para usar el TouchListener del mapa
        //mapView.setBuiltInZoomControls(false);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        
        this.bikeStationResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        
		
        
        // info = (TextView) findViewById(R.id.info);
        /*info.setOnTouchListener(new OnTouchListener() {
    		public boolean onTouch(View v, MotionEvent e) {
    			// mapController.zoomOut();
			 			
    			Toast.makeText(BikeBonusActivity.this,"Current Location via " + locationProvider +"\n" + pointToDegs(currentLocation) ,Toast.LENGTH_SHORT).show();
    			info.setText(pointToDegs(currentLocation));
    	    	    
    			//myPath.addPoint((GeoPoint) currentLocation);
    			mapController.setCenter(currentLocation);
    			
    			return true;
    		}
    		
    	});*/

        // Cuando el mapa se toca (Para que funcione hay que deshabilitar los controles de zoom!)  
        //mapView.setOnTouchListener(new OnTouchListener() {
        //	public boolean onTouch(View v, MotionEvent e) {
        //		float x = e.getX();
       	//		float y = e.getY();
       			
       	//		Projection p = ((MapView) v).getProjection();
       			
       	//		IGeoPoint g = p.fromPixels(x, y);
       			
       			// info.setText(pointToDegs(actualPoint));
       			
       	//		Toast.makeText(BikeBonusActivity.this, g.getLatitudeE6()+" "+g.getLongitudeE6(), Toast.LENGTH_LONG);
       	        
       	        //myPath.addPoint((GeoPoint) lastPoint);
       	        //myPath.addPoint((GeoPoint) actualPoint);*/
       	        
        //		return true;
        //	}
        	
        //});
        
        //myPath = new PathOverlay(Color.RED, this);
        //mapView.getOverlays().add(myPath);
        
    	SetupActivity(savedInstanceState);
        
    }

    // Crea el menú de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Listener del menu de opciones
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        	default:
            case R.id.aboutMenuOption:  
            	Toast.makeText(this, resources.getString(R.string.about_message), Toast.LENGTH_LONG).show();
               break;
        }
    	
        return true;
    }
    
    // Antes de cambiar orientación, pausar, etc... Se llama a este método
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
    	super.onSaveInstanceState(savedInstanceState);
    	
    	// Guardamos long y lat del punto central para repintar
    	ubicParams.setCenterPoint(mapView.getMapCenter());
    	
    	// Guardamos el Zoom actual de la vista de mapa
    	ubicParams.setActualZoom(mapView.getZoomLevel());
    	
    	savedInstanceState.putSerializable("ubicParams", ubicParams);
    }
    
    // Cuando volvemos a activar la app
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      SetupActivity(savedInstanceState);
    }

    // Encargada de inicializar los valores necesarios
    private void SetupActivity(Bundle savedInstanceState) {
    	// Si la app está iniciada y tenemos un contexto
    	if (savedInstanceState != null){
    		
          	if (savedInstanceState.containsKey("ubicParams"))
          		ubicParams = (UbicationParams) savedInstanceState.getSerializable("ubicParams");
          	
          }
    	
    	// Y si no decimos que lo estamos inicializando 
    	else {
    		
    		Toast.makeText(getApplicationContext(), resources.getString(R.string.initializing_message), Toast.LENGTH_SHORT).show();
    		ubicParams = new UbicationParams();
    		
    		//setLocationListener();
    		//new setLocationListenerTask().execute();
    		
    		timeBikeStationUpdates = new Timer();
    		timeBikeStationUpdates.scheduleAtFixedRate(new UpdateBikeArrayTask(), 0, 10000);
    		//timeBikeStationUpdates.schedule(new UpdateBikeArrayTask(),0, 30000);
    		
    	}
    	
    	mapController.setZoom(ubicParams.getActualZoom());
    	mapController.setCenter(ubicParams.getCenterPoint());
  		
    	redrawBikeStations();
    }
    	    	    
    protected void setLocationListener(){
    	
    	// Acquire a reference to the system Location Manager
    	locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
    	  try{
    		  ubicParams.setGpsEnabled(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER));}
    	  catch(Exception ex){
    		  Toast.makeText(BikeBonusActivity.this, resources.getString(R.string.gps_init_exception), Toast.LENGTH_SHORT).show();}
    	  
    	  try{
    		  ubicParams.setNetworkEnabled(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));}
    	  catch(Exception ex){
				  Toast.makeText(BikeBonusActivity.this, resources.getString(R.string.network_init_exception), Toast.LENGTH_SHORT).show();}

    	// Define a listener that responds to location updates
    	locListener = new LocationListener() {
    		
    	    public void onLocationChanged(Location location) {
    	      // Called when a new location is found by the network location provider.
    	    	ubicParams.setCurrentLocation(new GeoPoint (location.getLatitude(),location.getLongitude()));
    	    	
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {
    	    	
    	    }

    	    public void onProviderEnabled(String provider) {
    	    	
    	    }

    	    public void onProviderDisabled(String provider) {
    	    	
    	    	if (provider.equals(LocationManager.GPS_PROVIDER))
    	    			ubicParams.setGpsEnabled(false);
    	    	
    	    }
    	    	
    	  };
    	  
    	  
    	// Register the listener with the Location Manager to receive location updates
      	// locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 5, locListener);
    	  
    	  if (ubicParams.getGpsEnabled()) {
			   locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			   }
    	  else if (ubicParams.getNetworkEnabled()) {
			   locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);}

    }
    
    private JSONArray getJSONInfo(String address){
		
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(address);
		String line;
		
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		try {
				return new JSONArray(builder.toString());
			}
		catch (JSONException e)
			{
				return null;
			}
    }
        
	private void updateBikeArray(String url){
		Log.v("updateBikeArray","Getting BikeStationInfo");
		JSONArray bikeArrayJSON = getJSONInfo(url);
		Log.v("updateBikeArray","Got BikeStationInfo, setting the params");
		ubicParams.setNumberBikeStations(bikeArrayJSON.length());
		
		for (int i=0; i<bikeArrayJSON.length(); i++){
			try{
				ubicParams.addBikeStation(
					bikeArrayJSON.getJSONObject(i).getInt(resources.getString(R.string.bikestation_id_field_JSON)), 
					bikeArrayJSON.getJSONObject(i).getString(resources.getString(R.string.bikestation_name_field_JSON)),
					bikeArrayJSON.getJSONObject(i).getInt(resources.getString(R.string.bikestation_lat_field_JSON)),
					bikeArrayJSON.getJSONObject(i).getInt(resources.getString(R.string.bikestation_long_field_JSON)),
					bikeArrayJSON.getJSONObject(i).getInt(resources.getString(R.string.bikestation_numbikes_field_JSON)),
					bikeArrayJSON.getJSONObject(i).getInt(resources.getString(R.string.bikestation_freeslots_field_JSON)),
					bikeArrayJSON.getJSONObject(i).getString(resources.getString(R.string.bikestation_timestamp_field_JSON)));
			}
			catch (Exception e) {
				Log.e("updateBikeArray", "Error on BikeStationInfo JSON (missed/mispelled parameter)");
			}
					
		}
		
	}
	
	private void redrawBikeStations(){
		Iterator<BikeStation> itBikeStations = ubicParams.getIteratorBikeStations();
		
		this.bikeStationItems = new ArrayList<OverlayItem>();
		
		Drawable RedBallMarker = this.resources.getDrawable(R.drawable.red_ball);
		Drawable YellowBallMarker = this.resources.getDrawable(R.drawable.yellow_ball);
		Drawable GreenBallMarker = this.resources.getDrawable(R.drawable.green_ball);
		
		
		while (itBikeStations.hasNext()){
			BikeStation bikeStationAux = itBikeStations.next();
			
			String stationName = bikeStationAux.getStationName();
			int numBikes = bikeStationAux.getNumberBikes();
			int numFreeSlots = bikeStationAux.getFreeSlots();
			String lastUpdate = bikeStationAux.getLastUpdate();
			
			OverlayItem bikeStationOverlayItem = new OverlayItem(stationName, 
					 numBikes +" bikes, "+numFreeSlots+" free slots\nUpdated "+lastUpdate, 
					bikeStationAux.getStationUbication());
			
			if (numBikes>0){
				if (numBikes<3)
					bikeStationOverlayItem.setMarker(YellowBallMarker);
				else 
					bikeStationOverlayItem.setMarker(GreenBallMarker);
			}
			else bikeStationOverlayItem.setMarker(RedBallMarker);
		
			bikeStationItems.add(bikeStationOverlayItem);
		}
		
		this.bikeStationOverlay = new ItemizedIconOverlay<OverlayItem>(this.bikeStationItems,  
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
				
	            	public boolean onItemSingleTapUp(final int index,
	            		final OverlayItem item) {
	            			Toast.makeText(
	            			BikeBonusActivity.this,
	            			item.mTitle +"\n"+ item.mDescription, Toast.LENGTH_SHORT).show();
	                	return true; // We 'handled' this event.
	            	}
	            
	            	public boolean onItemLongPress(final int index,
	                    final OverlayItem item) {
	            		Toast.makeText(
	                		BikeBonusActivity.this,
	                		item.mTitle +"\n"+ item.mDescription ,Toast.LENGTH_SHORT).show();
	                return false;
	            }
		}, bikeStationResourceProxy );
				
		this.mapView.getOverlays().add(this.bikeStationOverlay);
	}

	/*private class UpdateBikeArrayTask extends AsyncTask<String,Void,Void>{
		
		// Baixem el JSON amb l'estat de les estacions de bicicletes
		protected Void doInBackground(String... url){
			Log.i("UpdateBikeArrayTask", "Updateado en=" + System.currentTimeMillis());
			updateBikeArray(url[0]);
			redrawBikeStations();
			return null;
		}

		protected void onPostExecute(){
			redrawBikeStations();
		}

	}*/
	
	
	private class setLocationListenerTask extends AsyncTask<Void,Void,Void>{
	 
		protected Void doInBackground(Void... v){
			// Acquire a reference to the system Location Manager
			Log.i("setLocationListenerTask","Trying to get a reference to the System Location Manager");
			locManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    	
			try{
				ubicParams.setGpsEnabled(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER));}
			catch(Exception ex){
				Log.e("setLocationListenerTask", resources.getString(R.string.gps_init_exception));}
    	  
			try{
				ubicParams.setNetworkEnabled(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));}
			catch(Exception ex){
				Log.e("setLocationListenerTask", resources.getString(R.string.network_init_exception));}

			// Define a listener that responds to location updates
			locListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					// Called when a new location is found by the network location provider.
					Log.i("setLocationListenerTask","Location changed in ubicParams");
					ubicParams.setCurrentLocation(new GeoPoint (location.getLatitude(),location.getLongitude()));
				}

				public void onStatusChanged(String provider, int status, Bundle extras) {}

				public void onProviderEnabled(String provider) {}

				public void onProviderDisabled(String provider) {
					if (provider.equals(LocationManager.GPS_PROVIDER))
						ubicParams.setGpsEnabled(false);
				}
			};
    	  
			// Register the listener with the Location Manager to receive location updates
			//locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 5, locListener);
    	  
			/*if (ubicParams.getGpsEnabled()) {
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			}
			else if (ubicParams.getNetworkEnabled()) {
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
			}*/
			
			return null;
		}
		
		protected void onPostExecute(){
		}
	}
	
	
	private class UpdateBikeArrayTask extends TimerTask{
		
		// 
		@Override
		public void run() {			
			Log.v("UpdateBikeArrayTask", "Called TimerTask in " + System.currentTimeMillis());
			updateBikeArray(resources.getString(R.string.bikestation_service_address_JSON));
			Log.v("UpdateBikeArrayTask", "Finished getting BikeStationInfo in " + System.currentTimeMillis()+ ", starting redrawing");
			redrawBikeStations();
			Log.v("UpdateBikeArrayTask", "Finished redrawing in " + System.currentTimeMillis());
		}
		
	}
}