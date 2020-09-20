package io.d2d.chservice.repository;

import io.d2d.chservice.model.db.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    @Query("SELECT v FROM Vehicle v WHERE v.UUID = :uuid ")
    Optional<Vehicle> getVehicleByUUID(String uuid);

    @Query(value = "select v.* , lc.ID as LID, lc.LNG as LNG, lc.LAT as LAT, lc.UPDATE_TIME as UT, lc.VEHICLE_ID as VID " +
            "from vehicle v inner join location lc on v.id=lc.vehicle_id " +
            "INNER JOIN ( SELECT slc.vehicle_id,MAX(slc.id) maxid from location slc GROUP BY slc.vehicle_id  ) SUB_LC ON lc.id = SUB_LC.maxid " +
            "WHERE lc.lat< ?1 and lc.lng < ?2 and lc.lat > ?3 and lc.lng > ?4 and v.out=false " +
            "order by lc.id desc ", nativeQuery = true)
    List<Vehicle> getVehiclesByBoundaries(Double neLat, Double neLng, Double swLat, Double swLng);
}
