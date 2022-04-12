package fra.uas.Tracks.Model;

public class Track {

	private String name;

	private int length;

	private int curves;

	public Track(String name, int length, int curves) {
		this.name = name;
		this.length = length;
		this.curves = curves;

	}

	public Track() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getCurves() {
		return curves;
	}

	public void setCurves(int curves) {
		this.curves = curves;
	}

	@Override
	public String toString() {
		String trackFormat;
		trackFormat = "Track: " + name + ", Lenght: " + length + ", curves: " + curves;
		return trackFormat;
	}

}
