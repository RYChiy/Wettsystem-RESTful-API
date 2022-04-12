package fra.uas.User.Repository;

import java.time.LocalDate;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import fra.uas.User.model.Role;
import fra.uas.User.model.User;

@Repository
public class UserRepository {

	// @Autowired
	public ArrayList<User> userList = new ArrayList<>();

	//Admin creation
	@PostConstruct
	public void init() {

		
		User admin = new User();
		admin.setRole(Role.ADMIN);
		admin.setUsername("admin");
		admin.setBirthday(LocalDate.now().minusYears(20));
		admin.setAge(admin.getAge());
		userList.add(admin);

	}

}
