package fra.uas.Car.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.fge.jsonpatch.JsonPatch;
//import com.github.fge.jsonpatch.JsonPatchException;
import fra.uas.Car.Model.Car;
import fra.uas.Car.Repository.CarRepository;

@Service
public class CarServiceImpl implements CarService {

	@Autowired
	CarRepository carRepository = new CarRepository();

	// add a new car to the repository
	@Override
	public void addCar(Car car) {
		carRepository.carList.add(car);
		System.out.println("Car (" + car.getModel() + ") has been added!");
	}

	// delete a car
	@Override
	public String deleteCar(String carName) {
		for (int i = 0; i < carRepository.carList.size(); i++) {
			String carName2 = carRepository.carList.get(i).getModel();
			if (carName.toLowerCase().equals(carName2.toLowerCase())) {
				
				String found;
				found = "The car (" + carRepository.carList.get(i).getBrand() + ": " + carName + ") has been deleted.";
				carRepository.carList.remove(i);
				return found;
			}
		}
		String r;
		r = "A car with the Model-Name: " + carName + "could'nt be found in the repository";
		return r;
	}

	// Get a car by the model name
	@Override
	public Car getCarByModel(String model) {
		for (int i = 0; i < carRepository.carList.size(); i++) {
			if (model.toLowerCase().equals(carRepository.carList.get(i).getModel().toLowerCase())) {
				return carRepository.carList.get(i);
			}
		}
		System.out.println("No Car found with the Model: " + model + "!");
		return null;
	}

	//return all cars
	@Override
	public ArrayList<Car> getCarList() {
		return carRepository.carList;

	}

	@Override
	// This method checks if the car already exists in the Repository
	// A specific model by a brand can only be created once
	public Boolean carExists(Car car) {
		Boolean exists = true;
		for (int i = 0; i < carRepository.carList.size(); i++) {
			if (car.getModel().toLowerCase().equals(carRepository.carList.get(i).getModel().toLowerCase())
					&& car.getBrand().toLowerCase().equals(carRepository.carList.get(i).getBrand().toLowerCase())) {
				System.out.println("Car (" + car.getBrand() + ": " + car.getModel() + ") already exists!");
				return exists;

			}
		}
		exists = false;
		return exists;
	}

	// Patch certain aspects of a car
	@Override
	public Car adjustCar(Car car, Car transferCar) {
		if (transferCar.getBrand() != null) {
			car.setBrand(transferCar.getBrand());
		}
		if (transferCar.getHorsePower() != 0 && transferCar.getHorsePower() > 0) {
			car.setHorsePower(transferCar.getHorsePower());
		}
		if (transferCar.getModel() != null) {
			car.setModel(transferCar.getModel());
			;
		}
		

		return car;
	}

	// replace a car
	@Override
	public Car replaceCar(Car currentCar, Car replacementCar) {
		currentCar.setBrand(replacementCar.getBrand());
		currentCar.setHorsePower(replacementCar.getHorsePower());
		currentCar.setModel(replacementCar.getModel());
		return currentCar;
	}

	//Check if a car model already exists
	@Override
	public Boolean carExistsByModel(String name) {
		for (int i = 0; i < carRepository.carList.size(); i++) {
			if (carRepository.carList.get(i).getModel().toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ArrayList<Car> getCarListDescHP() {
		Collections.sort(carRepository.carList, new Comparator<Car>() {
			// Descending sort by horsepower
			public int compare(Car car1, Car car2) {
				return Integer.valueOf(car2.getHorsePower()).compareTo(Integer.valueOf(car1.getHorsePower()));
			}
		});
		for (int i = 0; i < carRepository.carList.size(); i++) {
			System.out.println((i + 1) + ". -->" + carRepository.carList.get(i).getBrand() + ": "
					+ carRepository.carList.get(i).getModel() + ", HP: "
					+ carRepository.carList.get(i).getHorsePower());
		}
		return null;
	}

	@Override
	public ArrayList<Car> getCarListAscHP() {
		Collections.sort(carRepository.carList, new Comparator<Car>() {
			// Ascending sort by horsepower
			public int compare(Car car1, Car car2) {
				return Integer.valueOf(car1.getHorsePower()).compareTo(Integer.valueOf(car2.getHorsePower()));
			}
		});
		for (int i = 0; i < carRepository.carList.size(); i++) {
			System.out.println((i + 1) + ". -->" + carRepository.carList.get(i).getBrand() + ": "
					+ carRepository.carList.get(i).getModel() + ", HP: "
					+ carRepository.carList.get(i).getHorsePower());

		}
		return null;
	}

}
