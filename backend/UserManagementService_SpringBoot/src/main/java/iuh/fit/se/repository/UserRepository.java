package iuh.fit.se.repository;

import iuh.fit.se.entity.User;
import iuh.fit.se.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
     Optional<User> findByEmail(String email);
<<<<<<< Updated upstream
     List<User> findByRole(UserRoleEnum role);
=======
     List<User> findByRole(Role role);
>>>>>>> Stashed changes
}
