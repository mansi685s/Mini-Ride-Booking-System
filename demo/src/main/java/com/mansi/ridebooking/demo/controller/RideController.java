package com.mansi.ridebooking.demo.controller;

import com.mansi.ridebooking.demo.model.Location;
import com.mansi.ridebooking.demo.model.Ride;
import com.mansi.ridebooking.demo.model.User;
import com.mansi.ridebooking.demo.repository.LocationRepository;
import com.mansi.ridebooking.demo.repository.RideRepository;
import com.mansi.ridebooking.demo.repository.UserRepository;
import com.mansi.ridebooking.demo.util.DistanceUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    // Request Ride
    @PostMapping("/request")
    public ResponseEntity<?> requestRide(@RequestBody Ride rideRequest) {

        // Fetch pickup and drop locations from database
        Location pickup =
                locationRepository.findByLocationName(rideRequest.getPickupLocation());

        Location drop =
                locationRepository.findByLocationName(rideRequest.getDropLocation());

        if (pickup == null || drop == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid Pickup or Drop Location");
        }

        // Calculate distance
        double distance = DistanceUtil.calculateDistance(
                pickup.getLatitude(),
                pickup.getLongitude(),
                drop.getLatitude(),
                drop.getLongitude()
        );

        // Fare Calculation
        double baseFare = 5.0;
        double perKmRate = 2.0;
        double totalFare = baseFare + (distance * perKmRate);

        rideRequest.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        rideRequest.setFare(Math.round(totalFare * 100.0) / 100.0);
        rideRequest.setStatus("REQUESTED");

        // Driver Matching
        List<User> availableDrivers =
                userRepository.findByRoleAndStatus("DRIVER", "AVAILABLE");

        if (!availableDrivers.isEmpty()) {

            User assignedDriver = availableDrivers.get(0);

            assignedDriver.setStatus("BUSY");
            userRepository.save(assignedDriver);

            rideRequest.setDriver(assignedDriver);
            rideRequest.setStatus("ACCEPTED");
        }

        Ride savedRide = rideRepository.save(rideRequest);

        return ResponseEntity.ok(savedRide);
    }

    // Pending Rides
    @GetMapping("/pending")
    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    // Complete Ride
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeRide(@PathVariable Long id) {

        Optional<Ride> rideOpt = rideRepository.findById(id);

        if (rideOpt.isPresent()) {

            Ride ride = rideOpt.get();

            ride.setStatus("COMPLETED");

            if (ride.getDriver() != null) {

                User driver = ride.getDriver();

                driver.setStatus("AVAILABLE");

                userRepository.save(driver);
            }

            rideRepository.save(ride);

            return ResponseEntity.ok(ride);
        }

        return ResponseEntity.notFound().build();
    }
}
