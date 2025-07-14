package com.solayof.schoolinventorymanagement.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solayof.schoolinventorymanagement.entity.Item;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByName(String name);
    Optional<Item> findBySerialNumber(String serialNumber);
    Optional<Item> findByCategoryId(UUID categoryId);
}
