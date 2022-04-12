package fra.uas.Tracks.Service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import fra.uas.Tracks.Model.Track;
import fra.uas.Tracks.Repository.TrackRepository;

@Service
public class TrackServiceImpl implements TrackService {

	TrackRepository trackRepository = new TrackRepository();

	@Override
	public Boolean trackExists(Track track) {
		for (int i = 0; i < trackRepository.trackList.size(); i++) {
			if (trackRepository.trackList.get(i).getName().equalsIgnoreCase(track.getName())) {
				return true;
			}

		}
		return false;
	}

	//Add a new Track
	@Override
	public void addTrack(Track track) {
		trackRepository.trackList.add(track);
		System.out.println("new Track added (" + track + ")");

	}

	//Delete a Track
	@Override
	public String deleteTrack(Track track) {
		trackRepository.trackList.remove(track);
		return "Track has been deleted";
	}

	//Search for a Track by it's name
	@Override
	public Track getTrackByName(String name) {
		for (int i = 0; i < trackRepository.trackList.size(); i++) {
			if (trackRepository.trackList.get(i).getName().toLowerCase().equals(name.toLowerCase())) {
				return trackRepository.trackList.get(i);
			}
		}
		System.out.println("Track does not exist");
		return null;
	}

	//Replace a track
	@Override
	public Track replaceTrack(Track oldTrack, Track newTrack) {
		if (newTrack.getCurves() > 0 && newTrack.getLength() > 0 && newTrack.getName() != null) {
			oldTrack.setCurves(newTrack.getCurves());
			oldTrack.setLength(newTrack.getLength());
			oldTrack.setName(newTrack.getName());
			return oldTrack;
		}
		return null;
	}

	//Get all Tracks
	@Override
	public ArrayList<Track> getTrackList() {

		return trackRepository.trackList;
	}

	//Update a track
	@Override
	public Track updateTrack(Track track, Track patchTrack) {
		if (patchTrack.getCurves() > 0) {
			track.setCurves(patchTrack.getCurves());
		}
		if (patchTrack.getLength() > 0) {
			track.setLength(patchTrack.getLength());
		}

		return track;
	}

	//Check if a track exists
	public Boolean trackExists(String trackname) {
		for (int i = 0; i < trackRepository.trackList.size(); i++) {
			if (trackRepository.trackList.get(i).getName().toLowerCase().equals(trackname.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
