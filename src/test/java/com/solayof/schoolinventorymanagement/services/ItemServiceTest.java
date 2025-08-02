package com.solayof.schoolinventorymanagement.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.ItemNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ItemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository; // Mocked repository for items

    @Mock
    private CategoryService categoryService; // Mocked service for interacting with categories

    @InjectMocks
    private ItemService itemService; // The service being tested

    private UUID itemId;
    private UUID categoryId;
    private Category category;
    private Item item;

    @BeforeEach
    void setup() {
        // Initialize test data before each test
        categoryId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        category = new Category(categoryId, "Electronics", "Electronic items", new HashSet<>());
        item = new Item(itemId, "Projector", "HD Projector", "SN123", Status.AVAILABLE, null, null, category, null);
    }

    @Test
    void findByItemId_success() {
        // Simulate finding an item by ID
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item found = itemService.findByItemId(itemId);

        assertEquals("Projector", found.getName());
    }

    @Test
    void findByItemId_notFound() {
        // Simulate item not found by ID
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByItemId(itemId));
    }

    @Test
    void existsById_returnsTrue() {
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertTrue(itemService.existsById(itemId));
    }

    @Test
    void existsById_returnsFalse() {
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertFalse(itemService.existsById(itemId));
    }

    @Test
    void findByName_success() {
        // Simulate finding an item by name
        when(itemRepository.findByName("Projector")).thenReturn(Optional.of(item));

        Item found = itemService.findByName("Projector");

        assertEquals("Projector", found.getName());
    }

    @Test
    void findByName_notFound() {
        // Simulate not finding an item by name
        when(itemRepository.findByName("Scanner")).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByName("Scanner"));
    }

    @Test
    void existsByName_returnsTrue() {
        when(itemRepository.existsByName("Projector")).thenReturn(true);

        assertTrue(itemService.existsByName("Projector"));
    }

    @Test
    void existsBySerialNumber_returnsTrue() {
        when(itemRepository.existsBySerialNumber("SN123")).thenReturn(true);

        assertTrue(itemService.existsBySerialNumber("SN123"));
    }

    @Test
    void findBySerialNumber_success() {
        // Simulate finding an item by serial number
        when(itemRepository.findBySerialNumber("SN123")).thenReturn(Optional.of(item));

        Item found = itemService.findBySerialNumber("SN123");

        assertEquals("SN123", found.getSerialNumber());
    }

    @Test
    void findBySerialNumber_notFound() {
        when(itemRepository.findBySerialNumber("SN999")).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findBySerialNumber("SN999"));
    }

    @Test
    void findByCategoryId_success() {
        // Simulate finding item by its category
        when(itemRepository.findByCategoryId(categoryId)).thenReturn(Optional.of(item));

        Item found = itemService.findByCategoryId(categoryId);

        assertEquals("Projector", found.getName());
    }

    @Test
    void findByCategoryId_notFound() {
        // Simulate item not found in category
        when(itemRepository.findByCategoryId(categoryId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByCategoryId(categoryId));
    }

    @Test
    void saveItem_success() {
        // Simulate saving an item
        when(itemRepository.save(item)).thenReturn(item);

        Item saved = itemService.saveItem(item);

        assertNotNull(saved);
        verify(itemRepository).save(item);
    }

    @Test
    void findAllItems_success() {
        // Simulate fetching all items
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> found = itemService.findAllItems();

        assertEquals(1, found.size());
    }

    @Test
    void deleteItem_success_whenNotAssigned() {
        // Simulate successful deletion of an unassigned item
        item.setStatus(Status.AVAILABLE);
        category.getItems().add(item);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(categoryService.saveCategory(any())).thenReturn(category);

        itemService.deleteItem(itemId);

        // Ensure the item was removed from the category and saved
        assertFalse(category.getItems().contains(item));
        verify(categoryService).saveCategory(category);
    }

    @Test
    void deleteItem_fails_whenAssigned() {
        // Simulate failure to delete an ASSIGNED item
        item.setStatus(Status.ASSIGNED);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(itemId));
    }

    @Test
    void findByCategoryIdIn_success() {
        // Simulate finding multiple items by category IDs
        List<UUID> ids = List.of(categoryId);
        List<Item> items = List.of(item);
        when(itemRepository.findByCategoryIdIn(ids)).thenReturn(items);

        List<Item> result = itemService.findByCategoryIdIn(ids);

        assertEquals(1, result.size());
    }

    @Test
    void findByCategoryIdInAndNameContainingIgnoreCase_success() {
        // Simulate case-insensitive name search within categories
        List<UUID> ids = List.of(categoryId);
        List<Item> items = List.of(item);
        when(itemRepository.findByCategoryIdInAndNameContainingIgnoreCase(ids, "proj")).thenReturn(items);

        List<Item> result = itemService.findByCategoryIdInAndNameContainingIgnoreCase(ids, "proj");

        assertEquals(1, result.size());
    }

    @Test
    void findByStatus_success() {
        // Simulate fetching items by status
        List<Item> items = List.of(item);
        when(itemRepository.findByStatus(Status.AVAILABLE)).thenReturn(items);

        List<Item> result = itemService.findByStatus(Status.AVAILABLE);

        assertEquals(1, result.size());
    }

    @Test
    void findByNameContainingIgnoreCase_success() {
        // Simulate partial name match search
        List<Item> items = List.of(item);
        when(itemRepository.findByNameContainingIgnoreCase("proj")).thenReturn(items);

        List<Item> result = itemService.findByNameContainingIgnoreCase("proj");

        assertEquals(1, result.size());
    }
}
