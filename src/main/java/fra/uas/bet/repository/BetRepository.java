package fra.uas.bet.repository;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import fra.uas.bet.model.Bet;

@Repository
public class BetRepository {
	public ArrayList<Bet> fullBetList= new ArrayList<>();
}
