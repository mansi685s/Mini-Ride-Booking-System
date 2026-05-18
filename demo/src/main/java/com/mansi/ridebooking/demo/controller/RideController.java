package com.mansi.ridebooking.demo.controller;
import com.mansi.ridebooking.demo.model.Ride;
import com.mansi.ridebooking.demo.model.User;
import com.mansi.ridebooking.demo.repository.RideRepository;
import com.mansi.ridebooking.demo.repository.UserRepository;
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

    // 1. Request a Ride (Fare calculation: Base $5 + $2 per km)
    @PostMapping("/request")
    public ResponseEntity<?> requestRide(@RequestBody Ride rideRequest) {
        double distance = Math.random() * 15 + 1; // Simulated distance between 1 and 16 km
        double baseFare = 5.0;
        double perKmRate = 2.0;
        double totalFare = baseFare + (distance * perKmRate);

        rideRequest.setDistanceKm(Math.round(distance * 100.0) / 100.0);
        rideRequest.setFare(Math.round(totalFare * 100.0) / 100.0);
        rideRequest.setStatus("REQUESTED");

        // Simple automated driver matching if available
        List<User> availableDrivers = userRepository.findByRoleAndStatus("DRIVER", "AVAILABLE");
        if (!availableDrivers.isEmpty()) {
            User assignedDriver = availableDrivers.get(0);
            assignedDriver.setStatus("BUSY");
            userRepository.save(assignedDriver);
            
            rideRequest.setDriver(assignedDriver);
            rideRequest.setStatus("ACCEPTED");
        }

        return ResponseEntity.ok(rideRepository.save(rideRequest));
    }

    // 2. Fetch pending rides (Useful for a manual Driver Dashboard view)
    @GetMapping("/pending")
    public List<Ride> getPendingRides() {
        return rideRepository.findByStatus("REQUESTED");
    }

    // 3. Complete a ride
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
            
            return ResponseEntity.ok(rideRepository.save(ride));
        }
        return ResponseEntity.notFound().build();
    }
}