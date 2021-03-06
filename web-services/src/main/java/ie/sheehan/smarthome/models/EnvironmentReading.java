package ie.sheehan.smarthome.models;

import java.util.Comparator;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import ie.sheehan.smarthome.repositories.EnvironmentReadingRepository;

@Document(collection = EnvironmentReadingRepository.COLLECTION_ENVIRONMENT_READING)
public class EnvironmentReading {
	
	@Id
	public String id;
	
	public double temperature;
	
	public double humidity;
	
	public long timestamp;
	
	
	@Override
	public String toString() {
		return String.format("ID: %s\n%s\nTemperature: %f\tHumidity: %f", 
				id, new Date(timestamp * 1000L).toString(), temperature, humidity);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof EnvironmentReading)){
			return false;
		}
		
		EnvironmentReading other = (EnvironmentReading) obj;
		return this.id.equals(other.id);
	}
	
	public static class EnvironmentReadingTimeComparator implements Comparator<EnvironmentReading> {
		@Override
		public int compare(EnvironmentReading o1, EnvironmentReading o2) {
			Date d1 = new Date(o1.timestamp * 1000L);
			Date d2 = new Date(o2.timestamp * 1000L);
			
			return d1.compareTo(d2);
		}
	}
	
}
