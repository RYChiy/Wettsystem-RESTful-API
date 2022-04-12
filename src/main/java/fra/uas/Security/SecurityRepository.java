package fra.uas.Security;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

@Repository
public class SecurityRepository {

	ArrayList<Token> token = new ArrayList<>();

	@PostConstruct
	public void init() {

		//Create a token for the admin
		Token admin = new Token("admin", "7a3acfd6-53f7-44f8-916d-4f52632e301f");
		token.add(admin);

	}

}
