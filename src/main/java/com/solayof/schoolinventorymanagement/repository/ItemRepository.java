package com.solayof.schoolinventorymanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.constants.Status;


public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByName(String name);
    Optional<Item> findBySerialNumber(String serialNumber);
    Optional<Item> findByCategoryId(UUID categoryId);
    Boolean existsBySerialNumber(String serialNumber);
    List<Item> findByCategoryIdIn(List<UUID> categoryIds);
    List<Item> findByNameContainingIgnoreCase(String name);
    List<Item> findByCategoryIdInAndNameContainingIgnoreCase(List<UUID> categoryIds, String name);
    List<Item> findByStatus(Status status);
    Boolean existsByName(String name);
    
}
