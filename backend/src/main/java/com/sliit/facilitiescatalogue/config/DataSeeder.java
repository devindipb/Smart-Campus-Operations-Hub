package com.sliit.facilitiescatalogue.config;

import com.sliit.facilitiescatalogue.resource.ResourceEntity;
import com.sliit.facilitiescatalogue.resource.ResourceRepository;
import com.sliit.facilitiescatalogue.resource.ResourceStatus;
import com.sliit.facilitiescatalogue.resource.ResourceType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedResources(ResourceRepository resourceRepository) {
        return args -> {
            if (resourceRepository.count() > 0) {
                return;
            }

            resourceRepository.saveAll(List.of(
                    buildResource("LH-001", "Main Lecture Hall A", ResourceType.LECTURE_HALL, 180,
                            "Malabe Campus - Block A", "Large lecture hall with projector and audio system",
                            LocalTime.of(8, 0), LocalTime.of(18, 0), ResourceStatus.ACTIVE, true),
                    buildResource("LAB-201", "Network Lab 201", ResourceType.LAB, 40,
                            "Malabe Campus - Block B", "Configured with networking hardware for practical sessions",
                            LocalTime.of(9, 0), LocalTime.of(17, 0), ResourceStatus.ACTIVE, true),
                    buildResource("EQ-PRJ-09", "Epson Projector 09", ResourceType.EQUIPMENT, 0,
                            "Facilities Store", "Portable projector available for short-term bookings",
                            LocalTime.of(8, 30), LocalTime.of(16, 30), ResourceStatus.OUT_OF_SERVICE, true)
            ));
        };
    }

    private ResourceEntity buildResource(String code,
                                         String name,
                                         ResourceType type,
                                         int capacity,
                                         String location,
                                         String description,
                                         LocalTime from,
                                         LocalTime to,
                                         ResourceStatus status,
                                         boolean active) {
        ResourceEntity entity = new ResourceEntity();
        entity.setResourceCode(code);
        entity.setName(name);
        entity.setType(type);
        entity.setCapacity(capacity);
        entity.setLocation(location);
        entity.setDescription(description);
        entity.setAvailableFrom(from);
        entity.setAvailableTo(to);
        entity.setStatus(status);
        entity.setActive(active);
        return entity ;
    }
}



