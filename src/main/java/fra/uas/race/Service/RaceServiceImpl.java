package fra.uas.race.Service;

import java.util.ArrayList;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fra.uas.Car.Model.Car;
import fra.uas.Car.Service.CarServiceImpl;
import fra.uas.Tracks.Model.Track;
import fra.uas.Tracks.Service.TrackServiceImpl;
import fra.uas.race.Model.Race;
import fra.uas.race.Model.RaceCreator;
import fra.uas.race.Model.Condition;
import fra.uas.race.Repository.RaceRepository;

@Service
public class RaceServiceImpl implements RaceService {

	@Autowired
	RaceRepository raceRep = new RaceRepository();

	@Autowired
	TrackServiceImpl trackService = new TrackServiceImpl();

	@Autowired
	CarServiceImpl carService = new CarServiceImpl();

	// Return all current races
	@Override
	public ArrayList<Race> getRaceList() {

		return raceRep.raceList;
	}

	// Method to start a race
	@Override
	public String startRace(Race race) {

		Random r = new Random();
		// double randome1=(r.nextInt((int)((2-1)*10+1))+1*10) / 10.0;
		// double random2=(r.nextInt((int)((2-1)*10+1))+1*10) / 10.0;
		double c1w = 0;
		double c2w = 0;
		// return (r.nextInt((int)((max-min)*10+1))+min*10) / 10.0;
		for (int i = 0; i < race.getTrack().getCurves(); i++) {
			double random1 = (r.nextInt((int) ((2 - 1) * 10 + 1)) + 1 * 10) / 10.0;
			double random2 = (r.nextInt((int) ((2 - 1) * 10 + 1)) + 1 * 10) / 10.0;
			c1w = ((race.getCar1().getHorsePower() * random1) / (race.getTrack().getLength()));
			c2w = ((race.getCar2().getHorsePower() * random2) / (race.getTrack().getLength()));
		}
		if (c1w > c2w) {
			return "1";
		} else if (c1w < c2w) {
			return "2";
		} else if (c1w == c2w) {
			return "3";
		}

		return null;
	}

	// Search for an event by name
	@Override
	public Race getRaceByEvent(String event) {
		for (int i = 0; i < raceRep.raceList.size(); i++) {
			if (raceRep.raceList.get(i).getEvent().toLowerCase().equals(event.toLowerCase())) {
				return raceRep.raceList.get(i);
			}
		}
		return null;
	}

	// Add a race
	@Override
	public Race addRace(Race race) {
		raceRep.raceList.add(race);
		return race;
	}

	//Check if a race already exists
	@Override
	public Boolean raceExists(String event) {
		for (int i = 0; i < raceRep.raceList.size(); i++) {
			if (raceRep.raceList.get(i).getEvent().toLowerCase().equals(event.toLowerCase())
					&& raceRep.raceList.get(i).getCondition() == Condition.ACTICVE) {
				return true;
			}
		}
		return false;
	}

	// Method to Create a new Race, using the RaceCreator Object to pass the
	// Information
	@Override
	public Race createRace(RaceCreator raceCreator) {
		if (raceCreator.getEvent() != null && carService.carExistsByModel(raceCreator.getModel1().toLowerCase()) == true
				&& carService.carExistsByModel(raceCreator.getModel2().toLowerCase()) == true
				&& trackService.getTrackByName(raceCreator.getTrack().toLowerCase()) != null) {
			String event = raceCreator.getEvent();
			Track track = trackService.getTrackByName(raceCreator.getTrack());
			Car model1 = carService.getCarByModel(raceCreator.getModel1());
			Car model2 = carService.getCarByModel(raceCreator.getModel2());
			Race race = new Race(event, track, model1, model2);

			raceRep.raceList.add(race);
			return race;
		}
		return null;
	}

	//Trackservice is implemented in raceService
	public TrackServiceImpl getTrackService() {
		return trackService;
	}

	//CarService is implemented in raceService
	public CarServiceImpl getCarService() {
		return carService;
	}

	//Get a list wiith all active races
	@Override
	public ArrayList<Race> getRaceListActive() {
		ArrayList<Race> raceListActive = new ArrayList<>();
		for (int i = 0; i < raceRep.raceList.size(); i++) {
			if (raceRep.raceList.get(i).getCondition() == Condition.ACTICVE) {
				raceListActive.add(raceRep.raceList.get(i));
			}
		}
		return raceListActive;
	}

	//get a list with all completed races
	@Override
	public ArrayList<Race> getRaceListCompleted() {
		ArrayList<Race> raceListCompleted = new ArrayList<>();
		for (int i = 0; i < raceRep.raceList.size(); i++) {
			if (raceRep.raceList.get(i).getCondition() == Condition.COMPLETED) {
				raceListCompleted.add(raceRep.raceList.get(i));
			}
		}
		return raceListCompleted;
	}

}
