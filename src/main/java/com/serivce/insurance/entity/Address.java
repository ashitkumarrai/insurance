package com.serivce.insurance.entity;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    @Override
    public String toString() {
        return ""+street + "  "+ city  +"  "+ state + " " + country
                + " ZipCode:- " + zipCode;
    }

}