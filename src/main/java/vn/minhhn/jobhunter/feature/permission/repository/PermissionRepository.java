package vn.minhhn.jobhunter.feature.permission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.minhhn.jobhunter.feature.permission.domain.Permission;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByApiPathAndMethod(String apiPath, String method);
    boolean existsByApiPathAndMethodAndIdNot(String apiPath, String method, Long id);
}
