package io.d2d.chservice.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="VEHICLE")
public class Vehicle {

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "SEQ_VEHICLE", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_VEHICLE", sequenceName = "SEQ_VEHICLE",allocationSize = 1)
    @JsonIgnore
    private Integer id;

    @Column(name="UUID", unique = true)
    @JsonIgnore
    private String UUID;

    @OneToMany(cascade={CascadeType.REMOVE},mappedBy = "vehicle",fetch = FetchType.LAZY)
    @OrderBy(value = "id DESC")
    @JsonManagedReference
    private List<Location> locations;

    // this column is to determine the vehicle is out of bounds
    // in order to discard it sending to map
    @Column(name="OUT", nullable = false, columnDefinition = "boolean default false" )
    @JsonIgnore
    private Boolean out = false;

}
