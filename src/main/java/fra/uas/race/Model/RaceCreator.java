package fra.uas.race.Model;

public class RaceCreator {

	private String event;
	private String model1;
	private String model2;
	private String track;

	// Object to hand over the information for a new race
	public RaceCreator(String event, String model1, String model2, String track) {
		this.event = event;
		this.model1 = model1;
		this.model2 = model2;
		this.track = track;
	}

	public RaceCreator() {

	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getModel1() {
		return model1;
	}

	public void setModel1(String model1) {
		this.model1 = model1;
	}

	public String getModel2() {
		return model2;
	}

	public void setModel2(String model2) {
		this.model2 = model2;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

}
