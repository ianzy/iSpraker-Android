package com.iSpraker.android.dao;

import java.util.List;

import com.iSpraker.android.dos.Event;

public interface IEventsDAO {
	public List<Event> getEventsByLocation(double lat, double lng);
}
