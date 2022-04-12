package fra.uas.bet.service;

import java.util.ArrayList;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fra.uas.User.Service.UserServiceImpl;
import fra.uas.User.model.User;
import fra.uas.bet.model.Bet;
import fra.uas.bet.model.State;
import fra.uas.bet.repository.BetRepository;
import fra.uas.race.Model.Condition;
import fra.uas.race.Model.Race;
import fra.uas.race.Service.RaceServiceImpl;

@Service
public class BetServiceImpl implements BetService {
	private static int betid;

	@Autowired
	BetRepository betRep = new BetRepository();

	@Autowired
	UserServiceImpl userService = new UserServiceImpl();

	@Autowired
	RaceServiceImpl raceService = new RaceServiceImpl();

	// all bets of one user
	@Override
	public ArrayList<Bet> getBetsByUser(String userName) {
		ArrayList<Bet> transferList = new ArrayList<>();
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			if (betRep.fullBetList.get(i).getUsername().toLowerCase().equals(userName.toLowerCase())) {
				transferList.add(betRep.fullBetList.get(i));
			}
		}
		if (transferList.size() > 0) {
			return transferList;
		}
		return null;

	}

	// All bets of one event
	@Override
	public ArrayList<Bet> getBetsByEventName(String eventName) {
		ArrayList<Bet> transferList = new ArrayList<>();
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			if (betRep.fullBetList.get(i).getEventName().equals(eventName)) {
				transferList.add(betRep.fullBetList.get(i));
			}
		}
		if (transferList.size() > 0) {
			return transferList;
		}
		return null;
	}

	// Calculate the betting odds
	@Override
	public double getQuote() {
		Random r = new Random();
		return (r.nextInt((int) ((3 - 1) * 10 + 1)) + 1 * 10) / 10.0;

	}

	// Add a bet
	@Override
	public Bet addBet(Bet bet) {
		ArrayList<Race> raceList = raceService.getRaceListActive();
		for (int i = 0; i < raceList.size(); i++) {
			if (raceList.get(i).getEvent().toLowerCase().equals(bet.getEventName().toLowerCase())
					&& raceList.get(i).getCondition() == Condition.ACTICVE) {
				bet.setQuote(getQuote());

				double transferBalance = userService.getUserbyName(bet.getUsername()).getBalance();
				if (transferBalance >= bet.getAmount() && activeRaceExists(bet.getEventName().toLowerCase())) {

					double newBalance = transferBalance - bet.getAmount();
					userService.getUserbyName(bet.getUsername()).setBalance(newBalance);
					betRep.fullBetList.add(bet);
					bet.setID(betid);
					betid++;
					return bet;
				} else {

				}

			}

		}
		return null;

	}

	public UserServiceImpl getUserService() {
		return userService;
	}

	public RaceServiceImpl getRaceService() {
		return raceService;
	}

	// Get Bets for one User for one Event
	@Override
	public ArrayList<Bet> getBetyByUsernameAndEventName(String username, String eventName) {
		ArrayList<Bet> transferList = new ArrayList<>();
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			if (betRep.fullBetList.get(i).getEventName().toLowerCase().equals(eventName.toLowerCase())
					&& betRep.fullBetList.get(i).getUsername().toLowerCase().equals(username.toLowerCase())

			) {
				transferList.add(betRep.fullBetList.get(i));

				;
			}
		}
		if (transferList.size() > 0) {
			return transferList;
		}
		return null;
	}

	// Check if an active race exists
	public Boolean activeRaceExists(String raceNaame) {

		ArrayList<Race> activeRace = raceService.getRaceListActive();
		for (int i = 0; i < activeRace.size(); i++) {
			if (activeRace.get(i).getEvent().toLowerCase().equals(raceNaame.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	// Close all Bets for an Event
	@Override
	public ArrayList<Bet> resolveBetByEvent(String eventName) {
		String r = "";
		ArrayList<Bet> response = new ArrayList<>();
		raceService.getRaceByEvent(eventName).setCondition(Condition.COMPLETED);
		// loop for all users
		for (int x = 0; x < userService.getUserList().size(); x++) {
			User transferUser = userService.getUserList().get(x);
			Race transferRace = raceService.getRaceByEvent(eventName);
			String winner = raceService.startRace(transferRace);
			System.out.println(winner);
			// check if the user has bets related to this event
			if (getBetyByUsernameAndEventName(userService.getUserList().get(x).getUsername(), eventName) != null) {
				ArrayList<Bet> transferList = getBetyByUsernameAndEventName(
						userService.getUserList().get(x).getUsername(), eventName);
				// loop for the bets related to the event
				for (int i = 0; i < transferList.size(); i++) {
					// check if the user won
					if (transferList.get(i).getDecision().equals(winner)
							&& getBetyByUsernameAndEventName(transferUser.getUsername(), eventName).get(i)
									.getState() == State.OPEN) {
						r = r + ("Username: " + transferUser.getUsername() + ", Bet " + (i + 1) + ", Amount Bet: "
								+ transferList.get(i).getAmount() + ", Amount Won: "
								+ (transferList.get(i).getAmount() * transferList.get(i).getQuote()) + "\n");
						// Calculate the amountg won
						getBetyByUsernameAndEventName(transferUser.getUsername(), eventName).get(i)
								.setAmountWon(transferList.get(i).getAmount() * transferList.get(i).getQuote());
						double newBalance = transferUser.getBalance()
								+ (transferList.get(i).getAmount() * transferList.get(i).getQuote());
						// add the amount won to the balance of the user
						userService.getUserbyName(transferUser.getUsername()).setBalance(newBalance);
						// close the bet
						getBetyByUsernameAndEventName(transferUser.getUsername(), eventName).get(i)
								.setState(State.CLOSED);
						System.out.println("You won! \n Amount won: "
								+ (transferList.get(i).getAmount() * transferList.get(i).getQuote())
								+ "\n New Balance for " + transferUser.getUsername() + ": "
								+ transferUser.getBalance());
						response.add(transferList.get(i));
					} else if (!transferList.get(i).getDecision().equals(winner)
							&& getBetyByUsernameAndEventName(transferUser.getUsername(), eventName).get(i)
									.getState() == State.OPEN) {
						// close the bet, if the user did not win
						r = r + ("Username: " + transferUser.getUsername() + ", Bet " + (i + 1) + ", Amount Bet: "
								+ transferList.get(i).getAmount() + ", Amount Won: 0 \n");
						getBetyByUsernameAndEventName(transferUser.getUsername(), eventName).get(i)
								.setState(State.CLOSED);
						response.add(transferList.get(i));
					}

				}
			}
		}
		System.out.println(r);
		return response;
	}

	// refund a bet with a loss
	@Override
	public Bet takeBackBets(String username, int betID) {
		Bet refundedBet;
		// loop for all bets
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			// search for the right bet by it's id
			if (betRep.fullBetList.get(i).getID() == betID && betRep.fullBetList.get(i).getState() == State.OPEN) {
				refundedBet = betRep.fullBetList.get(i);
				double newBalance = userService.getUserbyName(username).getBalance()
						+ (betRep.fullBetList.get(i).getAmount() * 0.90);
				userService.getUserbyName(username).setBalance(newBalance);

				System.out.println("Bet: " + refundedBet + "has been taken back.");

				refundedBet.setState(State.REFUNDED);
				return refundedBet;

			} else if (betRep.fullBetList.get(i).getID() == betID
					&& betRep.fullBetList.get(i).getState() == State.CLOSED) {
				System.out
						.println("Bet with the id: " + betRep.fullBetList.get(i).getID() + " has already been closed.");
				refundedBet = betRep.fullBetList.get(i);
				refundedBet.setAmountWon(refundedBet.getAmount());
				return refundedBet;
			}

		}
		return null;

	}

	// refund the bets when an event is deleted
	@Override
	public Bet refundWithoutLoss(String username, int betID) {
		Bet refundedBet;
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			if (betRep.fullBetList.get(i).getID() == betID && betRep.fullBetList.get(i).getState() == State.OPEN) {
				refundedBet = betRep.fullBetList.get(i);
				double newBalance = userService.getUserbyName(username).getBalance()
						+ (betRep.fullBetList.get(i).getAmount());
				userService.getUserbyName(username).setBalance(newBalance);
				System.out.println("Bet with the id: " + refundedBet.getID() + " has been refunded.");
				refundedBet.setState(State.REFUNDED);
				refundedBet.setAmountWon(refundedBet.getAmount());
				return refundedBet;

			} else if (betRep.fullBetList.get(i).getID() == betID
					&& betRep.fullBetList.get(i).getState() == State.CLOSED) {
				System.out.println("Bet: " + betRep.fullBetList.get(i) + "has already been closed.");
				refundedBet = betRep.fullBetList.get(i);
				return refundedBet;
			}

		}
		return null;
	}

	// Average stats for one user
	@Override
	public String betAverage(String username) {
		String stats = "";
		ArrayList<Bet> bets = getBetsByUser(username);
		double amountbet = 0;
		double win = 0;
		int betstotal = 0;
		int betsclosed = 0;
		int betsopen = 0;
		int betsRefunded = 0;
		double averageBet = 0;
		double averageWin = 0;
		if (bets != null) {
			for (int i = 0; i < bets.size(); i++) {
				amountbet = amountbet + bets.get(i).getAmount();
				win = win + bets.get(i).getAmountWon();
				betstotal++;
				if (bets.get(i).getState() == State.OPEN) {
					betsopen++;

				} else if (bets.get(i).getState() == State.CLOSED) {
					betsclosed++;

				} else if (bets.get(i).getState() == State.REFUNDED) {
					betsRefunded++;
				}
			}
			averageBet = amountbet / bets.size();
			averageWin = win / bets.size();
			stats = "Bets made: " + betstotal + "\nOpen Bets: " + betsopen + "\nClosed Bets: " + betsclosed
					+ "\n Bets refunded: " + betsRefunded + "\nAverage spending: " + averageBet + "\nAverage win: "
					+ averageWin + "\nAmount spend: " + amountbet + "\nAmount won: " + win;
			return stats;
		} else {

			return null;
		}
	}

	// Get a bet by it's id
	@Override
	public Bet getBetByID(int id) {
		for (int i = 0; i < betRep.fullBetList.size(); i++) {
			if (betRep.fullBetList.get(i).getID() == id) {
				return betRep.fullBetList.get(i);
			}
		}
		return null;
	}

	// Delete a race and refund all bets
	@Override
	public Race deleteRace(String event) {
		for (int i = 0; i < userService.getUserList().size(); i++) {
			String username = userService.getUserList().get(i).getUsername();
			ArrayList<Bet> transferList = getBetyByUsernameAndEventName(username, event);
			if (transferList != null&&transferList.size()>0) {
				for (int x = 0; x < transferList.size(); x++) {
					int betID = transferList.get(x).getID();
					if (transferList.get(i).getState() == State.OPEN) {
						refundWithoutLoss(username, betID);
					}
				}
			}
		}
		Race cancelledRace = raceService.getRaceByEvent(event);
		cancelledRace.setCondition(Condition.COMPLETED);

		return cancelledRace;
	}

	// Bet statistics of all users combined
	@Override
	public String betAverageAllUsers() {
		String stats = "";
		double amountbet = 0;
		double win = 0;
		int betstotal = 0;
		int betsclosed = 0;
		int betsopen = 0;
		int betsRefunded = 0;
		double averageBet = 0;
		double averageWin = 0;
		for (int i = 0; i < userService.getUserList().size(); i++) {
			User transferUser = userService.getUserList().get(i);
			ArrayList<Bet> transferList = getBetsByUser(transferUser.getUsername());
			if (transferList != null && transferList.size() > 0) {
				for (int x = 0; x < transferList.size(); x++) {
					amountbet = amountbet + transferList.get(x).getAmount();
					win = win + transferList.get(x).getAmountWon();
					betstotal++;
					if (transferList.get(x).getState() == State.OPEN) {
						betsopen++;

					} else if (transferList.get(x).getState() == State.CLOSED) {
						betsclosed++;

					} else if (transferList.get(x).getState() == State.REFUNDED) {
						betsRefunded++;
					}

				}
			}
		}
		averageBet = amountbet / betstotal;
		averageWin = win / betstotal;
		stats = "Bets made: " + betstotal + "\nOpen Bets: " + betsopen + "\nClosed Bets: " + betsclosed
				+ "\nRefunded Bets: " + betsRefunded + "\nAverage spending: " + averageBet + "\nAverage win: "
				+ averageWin + "\nAmount spend: " + amountbet + "\nAmount won: " + win;
		return stats;
	}

	// Bet statistics of on event
	@Override
	public String betAverageEvent(String event) {
		String stats = "";
		double amountbet = 0;
		double win = 0;
		int betstotal = 0;
		int betsclosed = 0;
		int betsopen = 0;
		int betsrefunded = 0;
		double averageBet = 0;
		double averageWin = 0;
		ArrayList<Bet> bets = getBetsByEventName(event);
		for (Bet bet : bets) {
			if (bets.size() > 0) {
				amountbet = amountbet + bet.getAmount();
				win = win + bet.getAmountWon();
				averageBet = averageBet + bet.getAmount();

				betstotal++;
				if (bet.getState() == State.OPEN) {
					betsopen++;

				} else if (bet.getState() == State.CLOSED) {
					betsclosed++;

				} else if (bet.getState() == State.REFUNDED) {
					betsrefunded++;
				}

			}

		}
		averageBet = amountbet / bets.size();
		averageWin = win / bets.size();
		stats = "Bets made: " + betstotal + "\nOpen Bets: " + betsopen + "\nClosed Bets: " + betsclosed
				+ "\nRefunded Bets: " + betsrefunded + "\nAverage spending: " + averageBet + "\nAverage win: "
				+ averageWin + "\nAmount spend: " + amountbet + "\nAmount won: " + win;
		return stats;

	}

	// All open Bets of a user will be refunded and transferred back to his bank
	// account if the user is being deleted
	@Override
	public ArrayList<Bet> beforeDeleteUser(String username) {
		ArrayList<Bet> refundedBets = new ArrayList<>();
		if (getBetsByUser(username) != null) {
			for (int i = 0; i < getBetsByUser(username).size(); i++) {
				if (getBetsByUser(username).get(i).getState() == State.OPEN) {
					refundWithoutLoss(username, getBetsByUser(username).get(i).getID());
					refundedBets.add(getBetsByUser(username).get(i));
				}
			}
		}
		// userService.deleteUser(username);
		return refundedBets;

	}

}
