package com.bikebonus;

import java.io.Serializable;
import org.osmdroid.api.IGeoPoint;

public class ConfigurationParams implements Serializable{
	
	private static final long serialVersionUID = 00000000;
	private PointFormat actualPointFormat=PointFormat.DECIMAL_E6;
	
	private long timeBikeUpdates = 30000;
	
	public enum PointFormat {
		  DECIMAL_E6 ("Millionesimes of Degree"),
		  DECIMAL_DEGREES_PERIOD ("Degrees.decimals"),
		  DECIMAL_DEGREES_COMMA ("Degrees,decimals");
		  
		  private final String description;		  
		  PointFormat(String d){
			  this.description = d;
		  }
		  
		  // String after formatting point
		  public String formatPoint(IGeoPoint p) {
			  switch (this){
				  case DECIMAL_E6: return String.format("l=%d L=%d", 
						  	p.getLatitudeE6(), 
						  	p.getLongitudeE6());
				  case DECIMAL_DEGREES_PERIOD: return String.format("l=%02.6fº L=%02.6fº", 
		  					(float) p.getLatitudeE6()/1E6, 
		  					(float) p.getLongitudeE6()/1E6);
				  case DECIMAL_DEGREES_COMMA: return String.format("l=%02,6fº L=%02,6fº", 
		  					(float) p.getLatitudeE6()/1E6, 
		  					(float) p.getLongitudeE6()/1E6);
				  default: return p.toString(); 
			  }
		}
	}
	
		
	public long getTimeBikeUpdates(){
		return this.timeBikeUpdates;
	}
	
	public void setTimeBikeUpdates(long l){
		this.timeBikeUpdates = l;
	}
	
}
