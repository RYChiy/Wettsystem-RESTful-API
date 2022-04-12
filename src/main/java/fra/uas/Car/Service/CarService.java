package fra.uas.Car.Service;

import java.util.ArrayList;

import fra.uas.Car.Model.Car;

public interface CarService {

	public Boolean carExistsByModel(String name);

	public Boolean carExists(Car car);

	void addCar(Car car);

	String deleteCar(String model);

	public Car adjustCar(Car car, Car transferCar);

	public Car getCarByModel(String model);

	ArrayList<Car> getCarList();
	
	ArrayList<Car> getCarListDescHP();
	
	ArrayList<Car> getCarListAscHP();

	Car replaceCar(Car currentCar, Car replacementCar);

}
