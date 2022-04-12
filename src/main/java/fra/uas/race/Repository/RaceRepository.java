package fra.uas.race.Repository;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import fra.uas.race.Model.Race;

@Repository
public class RaceRepository {

	public ArrayList<Race> raceList = new ArrayList<>();

}
