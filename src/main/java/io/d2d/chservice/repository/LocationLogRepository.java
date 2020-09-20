package io.d2d.chservice.repository;

import io.d2d.chservice.model.db.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationLogRepository  extends JpaRepository<LocationLog, Integer> {

}
