package io.d2d.chservice.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="VEHICLE_LOG")
public class VehicleLog {

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "SEQ_VEHICLE_LOG", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_VEHICLE_LOG", sequenceName = "SEQ_VEHICLE_LOG",allocationSize = 1)
    private Integer id;

    @Column(name="UUID")
    @JsonIgnore
    private String uuid;

}
