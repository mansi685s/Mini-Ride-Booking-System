package com.mansi.ridebooking.demo.model;

import jakarta.persistence.*;


@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;
    
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;
    
    private String pickupLocation;
    private String dropOffLocation;
    private double distanceKm;
    private double fare;
    private String status; // REQUESTED, ACCEPTED, COMPLETED

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getPassenger() { return passenger; }
    public void setPassenger(User passenger) { this.passenger = passenger; }
    public User getDriver() { return driver; }
    public void setDriver(User driver) { this.driver = driver; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDropOffLocation() { return dropOffLocation; }
    public void setDropOffLocation(String dropOffLocation) { this.dropOffLocation = dropOffLocation; }
    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}