package fra.uas.Tracks.Service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import fra.uas.Tracks.Model.Track;

@Service
public interface TrackService {
public Boolean trackExists(Track track);
	
	void addTrack(Track track);

	String deleteTrack(Track track);
	
	public Track getTrackByName(String name);
	
	public Track replaceTrack(Track oldTrack, Track newTrack);
	
	public Track updateTrack(Track track, Track patchTrack);
	
	ArrayList<Track> getTrackList();
	
	
}
