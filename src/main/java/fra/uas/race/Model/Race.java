package fra.uas.race.Model;

import fra.uas.Car.Model.Car;
import fra.uas.Tracks.Model.Track;

public class Race {

	private String event;
	private Track track;
	private Car car1;
	private Car car2;
	private Condition condition;

	public Race(String event, Track track, Car car1, Car car2) {
		this.event = event;
		this.track = track;
		this.car1 = car1;
		this.car2 = car2;
		this.condition = Condition.ACTICVE;
	}

	public Race() {

	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public Car getCar1() {
		return car1;
	}

	public void setCar1(Car car1) {
		this.car1 = car1;
	}

	public Car getCar2() {
		return car2;
	}

	public void setCar2(Car car2) {
		this.car2 = car2;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	@Override
	public String toString() {
		String r = "Event: " + event + ", Track: " + track + ", 1. Car: " + car1.getModel() + ", 2. Car: "
				+ car2.getModel() + ", State: " + condition;
		return r;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

}
