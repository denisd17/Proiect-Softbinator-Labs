package com.example.SoftbinatorProject.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("fundraiser")
public class Fundraiser extends Project{
    private Double goal;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fundraiser")
    private List<Donation> donations;
}
