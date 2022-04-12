package fra.uas.Security;

import org.springframework.stereotype.Service;

@Service
public interface SecurityService {

	public Boolean checkToken(String username, String token);

	public String createToken(String username);

	public String getToken();

}
