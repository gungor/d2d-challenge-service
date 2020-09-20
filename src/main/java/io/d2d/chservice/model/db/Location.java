package io.d2d.chservice.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="LOCATION",
        indexes = { @Index(columnList = "LAT", name = "LAT_INDX"),
                    @Index(columnList = "LNG", name = "LNG_INDX"),
                    @Index(columnList = "VEHICLE_ID", name = "VHC_INDX")
})
public class Location {

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "SEQ_LOCATION", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_LOCATION", sequenceName = "SEQ_LOCATION",allocationSize = 1)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID")
    @JsonBackReference
    private Vehicle vehicle;

}
