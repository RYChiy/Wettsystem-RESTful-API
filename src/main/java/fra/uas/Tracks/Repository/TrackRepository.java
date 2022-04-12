package fra.uas.Tracks.Repository;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import fra.uas.Tracks.Model.Track;


@Repository
public class TrackRepository {

	
	public ArrayList<Track> trackList= new ArrayList<>();
}
