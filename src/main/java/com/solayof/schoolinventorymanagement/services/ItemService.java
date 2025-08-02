package com.solayof.schoolinventorymanagement.services;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.ItemNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ItemRepository;

@Service
public class ItemService {
    @Autowired // Using Spring's @Autowired to inject the ItemRepository
    private ItemRepository itemRepository; // Injecting the ItemRepository to interact with the database
    @Autowired
    private CategoryService categoryService; // Injecting the CategoryService to handle category-related operations

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
     * Exists by id.
     * Checks if an item exists by its ID.
     * @param itemId the ID of the item to check
     * @return true if an item with the given ID exists, false otherwise
     */
    public boolean existsById(UUID itemId) {
        return itemRepository.existsById(itemId);
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
     * Checks if an item exists by its name.
     * 
     * @param name the name of the item to check
     * @return true if an item with the given name exists, false otherwise
     */
    public boolean existsByName(String name) {
        return itemRepository.existsByName(name);
    }

    /**
     * Exists by serial number.
     * Checks if an item exists by its serial number.
     * @param serialNumber the serial number of the item to check
     * @return true if an item with the given serial number exists, false otherwise
     */
    public boolean existsBySerialNumber(String serialNumber) {
        return itemRepository.existsBySerialNumber(serialNumber);
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

    /**
     * findAllItems
     * Retrieves all items from the repository.
     * @return a list of all Item entities
     * @throws ItemNotFoundException if no items are found
     * 
     */
    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Deletes a item by its ID.
     *
     * @param itemId the ID of the item to delete
     * @throws ItemNotFoundException if no item is found with the given ID
     */
    public void deleteItem(UUID itemId) {
        Item item = findByItemId(itemId);
        if (item.getStatus() == Status.ASSIGNED) {
            throw new IllegalArgumentException("Item with name '" + item.getName() + "'' is not available for deletion.");
        }
        Category category = item.getCategory();
        
        category.getItems().remove(item); // Remove the item from the category's list of items
        item.setCategory(null); // Clear the category reference in the item
        // Removing an item from a category automatically delete item
        categoryService.saveCategory(category); // Update the category in the database

    }

    /**
     * Finds items by their category IDs.
     *
     * @param categoryIds the list of category IDs to find items in
     * @return a list of Item entities that belong to the specified categories
     */
    public List<Item> findByCategoryIdIn(List<UUID> categoryIds) {
        return itemRepository.findByCategoryIdIn(categoryIds);
    }

    /**
     * Finds items by their category IDs and name containing a specific string.
     *
     * @param categoryIds the list of category IDs to find items in
     * @param name the string to search for in item names
     * @return a list of Item entities that belong to the specified categories and contain the specified name
     */
    public List<Item> findByCategoryIdInAndNameContainingIgnoreCase(List<UUID> categoryIds, String name) {
        return itemRepository.findByCategoryIdInAndNameContainingIgnoreCase(categoryIds, name);
    }

    /**
     * Finds items by their status.
     *
     * @param status the status of the items to find
     * @return a list of Item entities with the specified status
     */
    public List<Item> findByStatus(Status status) {
        return itemRepository.findByStatus(status);
    }

    /**
     * Finds items by their name containing a specific string.
     *
     * @param name the string to search for in item names
     * @return a list of Item entities that contain the specified name
     */
    public List<Item> findByNameContainingIgnoreCase(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Retrieves the count of items grouped by their status.
     * @return A map where keys are Status enum values and values are their respective counts.
     */
    public Map<Status, Long> getItemCountsByStatus() {
        return itemRepository.findAll().stream()
                .collect(Collectors.groupingBy(Item::getStatus, () -> new EnumMap<>(Status.class), Collectors.counting()));
    }

    /**
     * Retrieves the count of items grouped by their category name.
     * @return A map where keys are String values (category names) and values are their respective counts.
     */
    public Map<String, Long> getItemCountsByCategory() { 
        return itemRepository.findAll().stream()
                .collect(Collectors.groupingBy(item -> item.getCategory().getName(), Collectors.counting())); 
    }
}

