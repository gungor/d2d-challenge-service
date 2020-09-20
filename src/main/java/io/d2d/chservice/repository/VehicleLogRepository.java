package io.d2d.chservice.repository;

import io.d2d.chservice.model.db.VehicleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleLogRepository extends JpaRepository<VehicleLog, Integer> {
}
