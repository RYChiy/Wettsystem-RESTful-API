package fra.uas.race.Service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import fra.uas.race.Model.Race;
import fra.uas.race.Model.RaceCreator;

@Service
public interface RaceService {

	public Race getRaceByEvent(String event);

	public ArrayList<Race> getRaceList();

	public Race addRace(Race race);

	public String startRace(Race race);

	public Boolean raceExists(String event);

	public Race createRace(RaceCreator raceCreator);

	public ArrayList<Race> getRaceListActive();

	public ArrayList<Race> getRaceListCompleted();

//public double getQuote(String eventName);

//public Race deleteRace(String event);

}
