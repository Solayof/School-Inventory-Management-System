package com.solayof.schoolinventorymanagement.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.ItemNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ItemRepository;

@Service
public class ItemService {
    @Autowired // Using Spring's @Autowired to inject the ItemRepository
    private ItemRepository itemRepository; // Injecting the ItemRepository to interact with the database

    /**
     * Finds an item by its ID.
     *
     * @param itemId the ID of the item to find
     * @return the found Item entity
     * @throws ItemNotFoundException if no item is found with the given ID
     */
    public Item findByItemId(UUID itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(()-> new ItemNotFoundException("Item not found with id: " + itemId));
    }

    /**
     * Finds an item by its name.
     *
     * @param name the name of the item to find
     * @return the found Item entity
     * @throws ItemNotFoundException if no item is found with the given name
     */
    public Item findByName(String name) {
        return itemRepository.findByName(name)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with name: " + name));
    }

    /**
     * Finds an item by its serial number.
     *
     * @param serialNumber the serial number of the item to find
     * @return the found Item entity
     * @throws ItemNotFoundException if no item is found with the given serial number
     */
    public Item findBySerialNumber(String serialNumber) {
        return itemRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with serial number: " + serialNumber));
    }

    /**
     * Finds an item by its category ID.
     *
     * @param categoryId the ID of the category to find items in
     * @return the found Item entity
     * @throws ItemNotFoundException if no item is found with the given category ID
     */
    public Item findByCategoryId(UUID categoryId) {
        return itemRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with category ID: " + categoryId));
    }

    /**
     * Saves an item to the repository.
     *
     * @param item the Item entity to save
     * @return the saved Item entity
     */
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
}

