package com.solayof.schoolinventorymanagement.services;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Category;
import com.solayof.schoolinventorymanagement.entity.Item;
import com.solayof.schoolinventorymanagement.exceptions.ItemNotFoundException;
import com.solayof.schoolinventorymanagement.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Unit test class for ItemService
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository; // Mocking the ItemRepository

    @Mock
    private CategoryService categoryService; // Mocking the CategoryService

    @InjectMocks
    private ItemService itemService; // Injecting mocks into the ItemService

    private UUID itemId;
    private Item item;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        itemId = UUID.randomUUID();
        category = new Category();
        category.setName("Electronics");
        item = new Item();
        item.setId(itemId);
        item.setName("Laptop");
        item.setSerialNumber("SN1234");
        item.setStatus(Status.AVAILABLE);
        item.setCategory(category);
    }

    @Test
    void testFindByItemId_Success() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Item result = itemService.findByItemId(itemId);

        assertEquals(item, result);
        verify(itemRepository).findById(itemId);
    }

    @Test
    void testFindByItemId_NotFound() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByItemId(itemId));
    }

    @Test
    void testExistsById() {
        when(itemRepository.existsById(itemId)).thenReturn(true);

        assertTrue(itemService.existsById(itemId));
    }

    @Test
    void testFindByName_Success() {
        when(itemRepository.findByName("Laptop")).thenReturn(Optional.of(item));

        Item result = itemService.findByName("Laptop");
        assertEquals(item, result);
    }

    @Test
    void testFindByName_NotFound() {
        when(itemRepository.findByName("Tablet")).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByName("Tablet"));
    }

    @Test
    void testExistsByName() {
        when(itemRepository.existsByName("Laptop")).thenReturn(true);

        assertTrue(itemService.existsByName("Laptop"));
    }

    @Test
    void testExistsBySerialNumber() {
        when(itemRepository.existsBySerialNumber("SN1234")).thenReturn(true);

        assertTrue(itemService.existsBySerialNumber("SN1234"));
    }

    @Test
    void testFindBySerialNumber_Success() {
        when(itemRepository.findBySerialNumber("SN1234")).thenReturn(Optional.of(item));

        Item result = itemService.findBySerialNumber("SN1234");
        assertEquals(item, result);
    }

    @Test
    void testFindBySerialNumber_NotFound() {
        when(itemRepository.findBySerialNumber("SN0000")).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findBySerialNumber("SN0000"));
    }

    @Test
    void testFindByCategoryId_Success() {
        UUID categoryId = UUID.randomUUID();
        when(itemRepository.findByCategoryId(categoryId)).thenReturn(Optional.of(item));

        Item result = itemService.findByCategoryId(categoryId);
        assertEquals(item, result);
    }

    @Test
    void testFindByCategoryId_NotFound() {
        UUID categoryId = UUID.randomUUID();
        when(itemRepository.findByCategoryId(categoryId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.findByCategoryId(categoryId));
    }

    @Test
    void testSaveItem() {
        when(itemRepository.save(item)).thenReturn(item);

        Item savedItem = itemService.saveItem(item);
        assertEquals(item, savedItem);
    }

    @Test
    void testFindAllItems() {
        List<Item> items = List.of(item);
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.findAllItems();
        assertEquals(items, result);
    }

    @Test
    void testDeleteItem_Success() {
        item.setStatus(Status.AVAILABLE);
        category.setItems(new HashSet<>(Set.of(item)));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId);

        verify(categoryService).saveCategory(category);
        assertFalse(category.getItems().contains(item));
        assertNull(item.getCategory());
    }

    @Test
    void testDeleteItem_AssignedStatus_ShouldThrowException() {
        item.setStatus(Status.ASSIGNED);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> itemService.deleteItem(itemId));
    }

    @Test
    void testFindByCategoryIdIn() {
        List<UUID> ids = List.of(UUID.randomUUID());
        when(itemRepository.findByCategoryIdIn(ids)).thenReturn(List.of(item));

        List<Item> result = itemService.findByCategoryIdIn(ids);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByCategoryIdInAndNameContainingIgnoreCase() {
        List<UUID> ids = List.of(UUID.randomUUID());
        when(itemRepository.findByCategoryIdInAndNameContainingIgnoreCase(ids, "lap"))
                .thenReturn(List.of(item));

        List<Item> result = itemService.findByCategoryIdInAndNameContainingIgnoreCase(ids, "lap");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByStatus() {
        when(itemRepository.findByStatus(Status.AVAILABLE)).thenReturn(List.of(item));

        List<Item> result = itemService.findByStatus(Status.AVAILABLE);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        when(itemRepository.findByNameContainingIgnoreCase("lap"))
                .thenReturn(List.of(item));

        List<Item> result = itemService.findByNameContainingIgnoreCase("lap");
        assertEquals(1, result.size());
    }

    @Test
    void testGetItemCountsByStatus() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        Map<Status, Long> counts = itemService.getItemCountsByStatus();
        assertEquals(1L, counts.get(Status.AVAILABLE));
    }

    @Test
    void testGetItemCountsByCategory() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        Map<String, Long> counts = itemService.getItemCountsByCategory();
        assertEquals(1L, counts.get("Electronics"));
    }
} 
