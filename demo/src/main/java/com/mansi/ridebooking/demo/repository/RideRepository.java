package com.mansi.ridebooking.demo.repository;
import com.mansi.ridebooking.demo.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByStatus(String status);
}