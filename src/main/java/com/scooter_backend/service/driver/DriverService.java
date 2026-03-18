package com.scooter_backend.service.driver;
import com.scooter_backend.entity.User;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final UserRepository userRepository;

    public DriverService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateDriverStatus(Long driverId, DriverStatus status) {

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!"DRIVER".equals(driver.getRole().name())) {
            throw new RuntimeException("User is not a driver");
        }

        driver.setStatus(status);
    }


}
