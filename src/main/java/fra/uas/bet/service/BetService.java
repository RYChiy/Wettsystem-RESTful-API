package fra.uas.bet.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import fra.uas.bet.model.Bet;
import fra.uas.race.Model.Race;

@Service
public interface BetService {
	

	//all bets of a user
	public ArrayList<Bet> getBetsByUser(String username);

	//all bets of an event
	public ArrayList<Bet> getBetsByEventName(String eventName);

	//generate the betting odds
	public double getQuote();

	//add a bet
	public Bet addBet(Bet bet);

	//close all bets of an event
	public ArrayList<Bet> resolveBetByEvent(String eventName);

	//get all bets of a user for a specific event
	public ArrayList<Bet> getBetyByUsernameAndEventName(String username, String eventName);

	// Average Bet performance of one user
	public String betAverage(String username);

	// refund a bet with a loss (10%)
	public Bet takeBackBets(String username, int betID);
	
	// refund a bet without a loss when an event is being deleted
		public Bet refundWithoutLoss(String username, int betID);

	//get the bet by the id
	public Bet getBetByID(int id);

	//if a race is deleted, all bets will be refunded
	public Race deleteRace(String event);

	//bet statistics for all user
	public String betAverageAllUsers();

	//bet statistics for an event
	public String betAverageEvent(String event);

	public ArrayList<Bet> beforeDeleteUser(String username);


}
