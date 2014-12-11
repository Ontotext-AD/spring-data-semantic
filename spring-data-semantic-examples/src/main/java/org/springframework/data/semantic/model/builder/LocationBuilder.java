package org.springframework.data.semantic.model.builder;

import org.springframework.data.semantic.model.Location;

/**
 * Created by nafets on 12/10/14.
 */
public class LocationBuilder extends ConceptBuilder<Location> {

	protected Long latitude;
	protected Long longtitude;

	protected LocationBuilder() {
		super();
	}

	public static LocationBuilder aLocationBuilder() {
		return new LocationBuilder();
	}

	public LocationBuilder withLatitude(Long latitude) {
		this.latitude = latitude;
		return this;
	}

	public LocationBuilder withLongtitude(Long longtitude) {
		this.longtitude = longtitude;
		return this;
	}

	@Override
	protected Location newConcept() {
		if (latitude == null || longtitude == null) {
			throw new IllegalArgumentException("Locations should have both latitude and longtitude, but either or both are null");
		}

		Location location = new Location();
		location.setLatitude(latitude);
		location.setLongtitude(longtitude);

		return location;
	}
}
