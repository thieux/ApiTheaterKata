package com.example.bestesttheaters.data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShowIndex {

	private final Map<Integer, List<Show>> showsById;

	public ShowIndex(List<Show> shows) {
		showsById = buildIndex(shows);
	}

	public Show getShow(int showId) {
		List<Show> shows = showsById.get(showId);
		if (shows == null) {
			return null;
		}
		if (shows.isEmpty()) {
			return null;
		}
		return shows.get(0);
	}

	private Map<Integer, List<Show>> buildIndex(List<Show> shows) {
		return shows.stream().collect(
			Collectors.groupingBy(Show::getId));
	}
}
