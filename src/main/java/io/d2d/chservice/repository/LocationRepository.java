package io.d2d.chservice.repository;

import io.d2d.chservice.model.db.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {

}
