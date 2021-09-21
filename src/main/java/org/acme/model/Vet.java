package org.acme.model;

import java.util.ArrayList;
import java.util.List;

// TODO: Import packages
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

// TODO: Add Entity and Cacheable annotation
@Entity(name="vets")
@Cacheable
public class Vet extends PanacheEntity {

    // TODO: Add Column and NotEmpty annotation for firstName
    @Column(name = "first_name")
    @NotEmpty
    public String firstName;

    // TODO: Add Column and NotEmpty annotation for lastName
    @Column(name = "last_name")
    @NotEmpty
    public String lastName;

    // TODO: Add the list of Specialty
    @ManyToMany
    @JoinTable(
            name = "vet_Specialties",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    public List<Specialty> specialties;

    /*public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    public void setLasttName(String lastName) {
        this.lastName = lastName;
    }
    public static List<Vet> listAll(){
        List vets = new ArrayList<Vet>();

        Vet vet = new Vet();
        vet.setFirstName("Daniel");
        vet.setLasttName("Oh");
        vets.add(vet);

        vet = new Vet();
        vet.setFirstName("Charles");
        vet.setLasttName("Moulliard");
        vets.add(vet);

        return vets;
    }*/

}