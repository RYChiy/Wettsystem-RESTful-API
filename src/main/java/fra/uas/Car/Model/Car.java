package fra.uas.Car.Model;

public class Car {

	//private String carID;
	private String brand;
	private String model;
	private int horsePower;
	//private int weight;

	public Car(String brand, String model, int horsePower, int weight) {
		this.brand = brand;
		this.model = model;
		this.horsePower = horsePower;
		//this.weight = weight;
		//carID= UUID.randomUUID().toString();
	}

	public Car() {
	
	}

	public int getHorsePower() {
		return horsePower;
	}

	public void setHorsePower(int horsePower) {
		this.horsePower = horsePower;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		String carFormat;
		carFormat = "Brand: " + brand + ", Model: " + model + ", HP: " + horsePower ;
		return carFormat;
	}
}
