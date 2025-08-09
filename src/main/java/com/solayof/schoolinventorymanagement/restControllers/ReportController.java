package com.solayof.schoolinventorymanagement.restControllers;

import com.solayof.schoolinventorymanagement.constants.Status;
import com.solayof.schoolinventorymanagement.entity.Assignment;
import com.solayof.schoolinventorymanagement.services.AssignmentService;
import com.solayof.schoolinventorymanagement.services.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private AssignmentService assignmentService;


    /**
     * Endpoint to generate a report on inventory levels.
     * Includes counts of items by status and by category.
     * Accessible by ADMIN and INVENTORY_MANAGER roles.
     * @return A map containing inventory level statistics.
     */
    @GetMapping("/inventory-levels")
    @Operation(summary = "Get inventory levels report", description = "Generates a report on inventory levels including item counts by status and category.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory levels report generated successfully"),
        @ApiResponse(responseCode = "404", description = "No items found in inventory")
    })
    public ResponseEntity<Map<String, Object>> getInventoryLevelsReport() {
        Map<String, Object> report = new HashMap<>();

        // Get total item count
        long totalItems = itemService.findAllItems().size();
        report.put("totalItems", totalItems);

        // Get item counts by status
        Map<Status, Long> itemCountsByStatus = itemService.getItemCountsByStatus();
        report.put("itemCountsByStatus", itemCountsByStatus);

        // Get item counts by category
        Map<String, Long> itemCountsByCategory = itemService.getItemCountsByCategory();
        report.put("itemCountsByCategory", itemCountsByCategory);

        return ResponseEntity.ok(report);
    }

    /**
     * Endpoint to generate a report on collector assignments.
     * Includes all active assignments and a count of overdue assignments per collector.
     * Accessible by ADMIN and INVENTORY_MANAGER roles.
     * @return A map containing collector assignment statistics.
     */
    @GetMapping("/collector-assignments")
    @Operation(summary = "Get collector assignments report", description = "Generates a report on collector assignments including active and overdue assignments.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Collector assignments report generated successfully"),
        @ApiResponse(responseCode = "404", description = "No assignments found")
    })
    public ResponseEntity<Map<String, Object>> getCollectorAssignmentsReport() {
        Map<String, Object> report = new HashMap<>();

        List<Assignment> allAssignments = assignmentService.getAllAssignments();
        List<Assignment> overdueAssignments = assignmentService.getOverdueAssignments();

        // Group assignments by collector for a detailed view
        Map<String, List<Assignment>> assignmentsByCollector = allAssignments.stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getCollector().getName()));
        report.put("assignmentsByCollector", assignmentsByCollector);

        // Count overdue assignments per collector
        Map<String, Long> overdueCountsByCollector = overdueAssignments.stream()
                .collect(Collectors.groupingBy(assignment -> assignment.getCollector().getName(), Collectors.counting()));
        report.put("overdueCountsByCollector", overdueCountsByCollector);

        report.put("totalActiveAssignments", allAssignments.stream().filter(a -> a.getActualReturnDate() == null).count());
        report.put("totalOverdueAssignments", overdueAssignments.size());

        return ResponseEntity.ok(report);
    }
}
