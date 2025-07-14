package com.solayof.schoolinventorymanagement.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.entity.Collector;
import com.solayof.schoolinventorymanagement.exceptions.CollectorNotFoundException;
import com.solayof.schoolinventorymanagement.repository.CollectorRepository;

@Service
public class CollectorService {
    @Autowired
    private CollectorRepository collectorRepository; // Injecting the CollectorRepository to interact with collectors

    /**
     * Finds a collector by their ID.
     *
     * @param collectorId the ID of the collector to find
     * @return the found Collector entity
     * @throws CollectorNotFoundException if no collector is found with the given ID
     */
    public Collector findByCollectorId(UUID collectorId) {
        return collectorRepository.findById(collectorId)
                .orElseThrow(() -> new CollectorNotFoundException("Collector not found with id: " + collectorId));
    }
    /**
     * Finds a collector by their email.
     *
     * @param email the email of the collector to find
     * @return the found Collector entity
     * @throws ItemNotFoundException if no collector is found with the given email
     */
    public Collector findByEmail(String email) {
        return collectorRepository.findByEmail(email)
                .orElseThrow(() -> new CollectorNotFoundException("Collector not found with email: " + email));
    }

    /**
     * Saves a collector to the repository.
     *
     * @param collector the Collector entity to save
     * @return the saved Collector entity
     */
    public Collector saveCollector(Collector collector) {
        return collectorRepository.save(collector);
    }
}
