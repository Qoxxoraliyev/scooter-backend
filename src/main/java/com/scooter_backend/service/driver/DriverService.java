package com.scooter_backend.service.driver;
import com.scooter_backend.entity.Driver;
import com.scooter_backend.enums.DriverStatus;
import com.scooter_backend.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public void updateDriverStatus(Long driverId, DriverStatus status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(status);
        driverRepository.save(driver);
    }
}
