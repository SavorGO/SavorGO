package iuh.fit.se.repository;

import iuh.fit.se.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,String> {
    Permission findByName(String name);
}
