package fra.uas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import fra.uas.Car.Model.Car;
import fra.uas.Car.Service.CarServiceImpl;
import fra.uas.Security.SecurityServiceImpl;
import fra.uas.Tracks.Model.Track;
import fra.uas.Tracks.Service.TrackServiceImpl;
import fra.uas.User.Service.UserServiceImpl;
import fra.uas.User.model.Role;
import fra.uas.User.model.User;
import fra.uas.User.model.UserRespnseDTO;
import fra.uas.bet.model.Bet;
import fra.uas.bet.service.BetServiceImpl;
import fra.uas.race.Model.Race;
import fra.uas.race.Model.RaceCreator;
import fra.uas.race.Service.RaceServiceImpl;

@RestController
public class Controller {

	@Autowired
	SecurityServiceImpl securityService = new SecurityServiceImpl();
	@Autowired
	BetServiceImpl betService = new BetServiceImpl();
	@Autowired
	UserServiceImpl userService = betService.getUserService();
	@Autowired
	CarServiceImpl carService = betService.getRaceService().getCarService();
	@Autowired
	TrackServiceImpl trackService = betService.getRaceService().getTrackService();
	@Autowired
	RaceServiceImpl raceService = betService.getRaceService();

	// Mapping to cerate a new User
	@RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addUser(@RequestBody User user) {
		if (userService.usernameExists(user.getUsername()) == false) {
			if (user.getAge() >= 18) {
				user.setRole(Role.USER);
				userService.addUser(user);
				System.out.println("New User: " + user + " created!");
				String token = securityService.createToken(user.getUsername());
				HttpHeaders header = new HttpHeaders();
				header.add("token", token);

				return ResponseEntity.created(null).headers(header).body("Token in Header");
			} else {
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Users must be 18 or older");
			}
		} else {

			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		}
	}

	// Mapping to delete new User
	@RequestMapping(value = "/users/{username}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteUser(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			// User transferUser = userService.getUserbyName(username);
			ArrayList<Bet> refunds = betService.beforeDeleteUser(username);
			userService.deleteUser(username);
			return ResponseEntity.status(HttpStatus.OK)
					.body("User: " + username + " has been deleted.\nAll bets have been refunded.\n" + refunds);

		} else {
			return new ResponseEntity<String>(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	// Mapping to get the information for an User
	@RequestMapping(value = "/users/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUser(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			User user = userService.getUserbyName(username);
			UserRespnseDTO userResponseDTO = new UserRespnseDTO(user);

			Link selfLink = linkTo(Controller.class).slash("users").slash(username).withSelfRel();
			Link accountLink = linkTo(methodOn(Controller.class).deleteUser(username, token)).withRel("DELETE");
			userResponseDTO.add(selfLink);
			userResponseDTO.add(accountLink);

			return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);

		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body("You are not authorized to see this or the user doesn not exist");

	}

	// Mapping to change user-information
	@RequestMapping(value = "/users/{username}/options", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> adjustUser(@RequestBody User transferUser, @PathVariable String username,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			User userDTO = userService.adjustUser(userService.getUserbyName(username), transferUser);
			System.out.println("User adjusted: " + userDTO.getUsername());
			return ResponseEntity.status(HttpStatus.OK).body(userDTO);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this");

	}

	// Mapping to deposit Money
	@RequestMapping(value = "/users/{username}/deposit", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deposit(@RequestBody User user, @PathVariable String username,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			userService.depositMoney(userService.getUserbyName(username), user.getBalance());
			System.out.println("New Balance for " + username + ": " + userService.getUserbyName(username).getBalance());
			return ResponseEntity.status(HttpStatus.OK).body(userService.getUserbyName(username));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to deposit money");
	}

	// Mapping to create a new Car
	@RequestMapping(value = "users/{username}/cars", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addCar(@PathVariable String username, @RequestBody Car car, @RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (carService.carExists(car) == false) {
				carService.addCar(car);
				System.out.println("New Car: " + car + " created!");
				return new ResponseEntity<String>(HttpStatus.CREATED);
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(car.getBrand() + ": " + car.getModel() + " already exists");
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to create a new car");

	}

	// Mapping to get the information for a Car
	@RequestMapping(value = "users/{username}/cars/{model}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCar(@PathVariable String username, @PathVariable String model,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (carService.carExistsByModel(model)) {
				Car transferCar = carService.getCarByModel(model);

				return ResponseEntity.status(HttpStatus.OK).body(transferCar);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model: " + model + " could not be found");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to get a carList descending horsepower
	@RequestMapping(value = "users/{username}/cars/hp/desc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCarListDesc(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {

			carService.getCarListDescHP();

			return ResponseEntity.status(HttpStatus.OK).body(carService.getCarList());

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to get a carList ascending horsepower
	@RequestMapping(value = "users/{username}/cars/hp/asc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCarListAsc(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {

			carService.getCarListAscHP();

			return ResponseEntity.status(HttpStatus.OK).body(carService.getCarList());

		} else if (!userService.usernameExists(username)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username does not exist");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to update a car model
	@RequestMapping(value = "users/{username}/cars/{model}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> patchCar(@PathVariable String username, @PathVariable String model,
			@RequestBody Car transferCar, @RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (carService.carExistsByModel(model)) {
				carService.adjustCar(carService.getCarByModel(model), transferCar);

				return ResponseEntity.status(HttpStatus.OK).body(carService.getCarByModel(model));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model: " + model + " could not be found");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");

	}

	// Mapping to delete a certain car model
	@RequestMapping(value = "users/{username}/cars/{model}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteCar(@PathVariable String username, @PathVariable String model,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (carService.carExistsByModel(model)) {
				Car deletedCar = carService.getCarByModel(model);
				carService.deleteCar(model);
				System.out.println("Car model: " + model + " has been deleted.");
				return ResponseEntity.status(HttpStatus.OK).body("Car has been deleted:\n" + deletedCar);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model: " + model + " could not be found");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete a car");

	}

	// Mapping to replace a certain car model
	@RequestMapping(value = "users/{username}/cars/{model}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> replaceCar(@RequestBody Car car, @PathVariable String username, @PathVariable String model,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (carService.carExistsByModel(model)) {
				carService.replaceCar(carService.getCarByModel(model), car);
				System.out.println("Car model: " + model + " has been replaced.");
				return ResponseEntity.status(HttpStatus.OK).body(carService.getCarByModel(model));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Car " + car.getBrand() + ": " + car.getModel() + " could not be found");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to replace a car");

	}

	// Mapping to create a new Track
	@RequestMapping(value = "users/{username}/tracks", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addTrack(@RequestBody Track track, @PathVariable String username,
			@RequestHeader String token) {

		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (trackService.trackExists(track) == false) {
				trackService.addTrack(track);
				System.out.println("New Track: " + track.getName() + " created!");
				return new ResponseEntity<String>(HttpStatus.CREATED);
			} else {

				return ResponseEntity.status(HttpStatus.CONFLICT).body("Track " + track.getName() + " already exists");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to create a new Track");

	}

	// Mapping to get the information for a Track
	@RequestMapping(value = "users/{username}/tracks/{trackname}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTrack(@PathVariable String username, @PathVariable String trackname,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (trackService.getTrackByName(trackname) != null) {
				Track transferTrack = trackService.getTrackByName(trackname);

				return ResponseEntity.status(HttpStatus.OK).body(transferTrack);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Track " + trackname + " not found");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");

	}

	// Mapping to get all Tracks
	@RequestMapping(value = "users/{username}/tracks/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getTrackList(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (trackService.getTrackList() != null) {
				return ResponseEntity.status(HttpStatus.OK).body(trackService.getTrackList());
			}
		} else if (trackService.getTrackList() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Tracks available");
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");

	}

	// Mapping to update a Track
	@RequestMapping(value = "users/{username}/tracks/{trackname}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateTrack(@RequestBody Track updatetrack, @PathVariable String username,
			@PathVariable String trackname, @RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (trackService.trackExists(trackname)) {
				trackService.updateTrack(trackService.getTrackByName(trackname), updatetrack);
				return ResponseEntity.status(HttpStatus.OK).body(trackService.getTrackByName(trackname));
			} else {

				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to to update a Track");

	}

	// Mapping to create a new Racing Event
	@RequestMapping(value = "/users/{username}/event", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createRace(@RequestBody RaceCreator rc, @PathVariable String username,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (raceService.raceExists(rc.getEvent()) == false) {
				if (raceService.createRace(rc) != null) {

					System.out
							.println("New Racing-Event: (" + raceService.getRaceByEvent(rc.getEvent()) + ") created!");
					return new ResponseEntity<String>(HttpStatus.CREATED);
				} else {
					return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Parameters are missung");
				}
			} else {

				return ResponseEntity.status(HttpStatus.OK).body("Event with the same title already exists");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to create an event");
	}

	// Mapping to get the information for a Racing Event
	@RequestMapping(value = "users/{username}/event/{event}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEvent(@PathVariable String event, @PathVariable String username,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (raceService.getRaceByEvent(event) != null) {
				Race transferRace = raceService.getRaceByEvent(event);

				return ResponseEntity.status(HttpStatus.OK).body(transferRace);
			} else {
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to get all Events, active and completed
	@RequestMapping(value = "users/{username}/event/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEventListAll(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (raceService.getRaceList() != null) {

				return ResponseEntity.status(HttpStatus.OK).body(raceService.getRaceList());
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No completed Events at the moment");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to get all active events
	@RequestMapping(value = "users/{username}/event/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEventListActive(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (raceService.getRaceListActive() != null) {

				return ResponseEntity.status(HttpStatus.OK).body(raceService.getRaceListActive());
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No active Events at the moment");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to get all completed events
	@RequestMapping(value = "users/{username}/event/completed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEventListCompleted(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (raceService.getRaceListCompleted() != null) {

				return ResponseEntity.status(HttpStatus.OK).body(raceService.getRaceListCompleted());
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No completed Events at the moment");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Mapping to delete a Racing Event // All bets with this event will be refunded
	@RequestMapping(value = "users/{username}/event/{event}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteEvent(@PathVariable String event, @PathVariable String username,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (raceService.raceExists(event)) {
				return ResponseEntity.status(HttpStatus.OK).body(betService.deleteRace(event));
			}
		} else {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this");
	}

	// Mapping to create a new Bet
	@RequestMapping(value = "/users/{username}/bets", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addBet(@RequestBody Bet bet, @PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (userService.getUserbyName(username).getBalance() >= bet.getAmount()) {
				if (raceService.raceExists(bet.getEventName())) {
					if (bet.getDecision().equals("1") || bet.getDecision().equals("2")
							|| bet.getDecision().equals("3")) {
						betService.addBet(bet);

						System.out.println("New Bet created: " + bet);
						return new ResponseEntity<String>(HttpStatus.CREATED);

					} else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Decision must be '1', '2' or '3'");
					}
				} else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active Event found");
				}
			} else {
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Not enough Money to place this bet");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this");
	}

	// Mapping to get all Bets of a User
	@RequestMapping(value = "users/{username}/bets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserBets(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (betService.getBetsByUser(username) != null || betService.getBetsByUser(username).size() > 0) {
				return ResponseEntity.status(HttpStatus.OK).body(betService.getBetsByUser(username));
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No bets were made reakted to this event");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");

	}

	// Mapping to get all Bets for an event
	@RequestMapping(value = "users/{username}/bets/{event}/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getEventBets(@PathVariable String username, @PathVariable String event,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (raceService.raceExists(event)) {
				return ResponseEntity.status(HttpStatus.OK).body(betService.getBetsByEventName(event));
			} else if (!raceService.raceExists(event)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event could not be found");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// show all bets of one user for one event
	@RequestMapping(value = "users/{username}/bets/{event}/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAllBetsForOneUserAndOneEvent(@PathVariable String username, @PathVariable String event,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (raceService.getRaceByEvent(event) != null
					&& betService.getBetyByUsernameAndEventName(username, event) != null) {

				return ResponseEntity.status(HttpStatus.OK)
						.body(betService.getBetyByUsernameAndEventName(username, event));
			} else {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("You have not created any bets");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");

	}

	// Close all Bets for one Event
	@RequestMapping(value = "users/{username}/{event}/closeallbets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resolveAllBets(@PathVariable String username, @PathVariable String event,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && raceService.raceExists(event)
				&& userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (betService.activeRaceExists(event)) {

				return ResponseEntity.status(HttpStatus.OK).body(betService.resolveBetByEvent(event));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active Event");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this");
	}

	// refund a bet
	@RequestMapping(value = "users/{username}/bets/{betID}/refund", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> takeBackBet(@PathVariable String username, @PathVariable int betID,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {
			if (betService.getBetByID(betID) != null) {
				if (betService.getBetByID(betID).getUsername().equals(username)) {
					betService.takeBackBets(username, betID);
					return ResponseEntity.status(HttpStatus.OK).body(betService.getBetByID(betID));
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							.body("You are not authorized to refund this bet");
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bet with this id does not exist");
			}

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to do this");
	}

	// Average statistics for one user
	@RequestMapping(value = "users/{username}/statistics/mine", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBetAverageOne(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && securityService.checkToken(username, token)) {

			return ResponseEntity.status(HttpStatus.OK).body(betService.betAverage(username));

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Average statistics for every user
	@RequestMapping(value = "users/{username}/statistics/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBetAverageAll(@PathVariable String username, @RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {

			return ResponseEntity.status(HttpStatus.OK).body(betService.betAverageAllUsers());

		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

	// Average statistics for every user of one Event
	@RequestMapping(value = "users/{username}/statistics/{event}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBetAverageEvent(@PathVariable String username, @PathVariable String event,
			@RequestHeader String token) {
		if (userService.usernameExists(username) && userService.getUserbyName(username).getRole() == Role.ADMIN
				&& securityService.checkToken(username, token)) {
			if (raceService.raceExists(event)) {
				return ResponseEntity.status(HttpStatus.OK).body(betService.betAverageEvent(event));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Race could not be found");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to see this");
	}

}