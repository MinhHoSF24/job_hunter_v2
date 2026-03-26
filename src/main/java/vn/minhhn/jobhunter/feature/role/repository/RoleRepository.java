package vn.minhhn.jobhunter.feature.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.minhhn.jobhunter.feature.role.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}