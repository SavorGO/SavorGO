package iuh.fit.se.repository;

import iuh.fit.se.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
//    Optional<User> findByEmail(String email);

    //    List<User> findByRole(UserRoleEnum role);
}
