package fra.uas.Security;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	SecurityRepository secRep = new SecurityRepository();

	//validate tokens
	@Override
	public Boolean checkToken(String username, String token) {
		for (int i = 0; i < secRep.token.size(); i++) {
			if (secRep.token.get(i).getUsername().toLowerCase().equals(username.toLowerCase())) {
				if (secRep.token.get(i).getToken().equals(token)) {
					return true;
				}
			}
		}
		System.out.println("Wrong Token!");
		return false;
	}
	

	//Create a token for a new user
	@Override
	public String createToken(String username) {
		String token = UUID.randomUUID().toString();
		secRep.token.add(new Token(username, token));
		return token;
	}

	//
	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return null;
	}

}
