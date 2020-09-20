package io.d2d.chservice.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="LOCATION_LOG",
        indexes = { @Index(columnList = "LAT", name = "LLAT_INDX"),
                @Index(columnList = "LNG", name = "LLNG_INDX"),
                @Index(columnList = "VEHICLE_UUID", name = "LVHC_INDX")
        })
public class LocationLog {

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "SEQ_LOCATION_LOG", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_LOCATION_LOG", sequenceName = "SEQ_LOCATION_LOG",allocationSize = 1)
    @JsonIgnore
    private Integer id;

    @Column(name="LAT")
    @JsonProperty("lat")
    private Double latitude;

    @Column(name="LNG")
    @JsonProperty("lng")
    private Double longitude;

    @Column(name="UPDATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updateTime;

    @Column(name="VEHICLE_UUID")
    private String vehicleUUID;

}
