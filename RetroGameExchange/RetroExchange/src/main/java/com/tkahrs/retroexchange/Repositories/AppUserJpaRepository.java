package com.tkahrs.retroexchange.Repositories;

import com.tkahrs.retroexchange.Models.AppUser;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@RestResource
public interface AppUserJpaRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> getFirstByEmail(String email);
    AppUser findByNameAndEmail(String username, String password);

    AppUser getByEmail(String email);
}
