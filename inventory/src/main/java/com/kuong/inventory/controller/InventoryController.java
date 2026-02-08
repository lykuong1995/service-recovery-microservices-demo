package com.kuong.inventory.controller;

import com.kuong.inventory.dto.InventoryRequest;
import com.kuong.inventory.dto.InventoryResponse;
import com.kuong.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public InventoryResponse reserve(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/release")
    public InventoryResponse release(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.release(request);
    }

    @PostMapping("/confirm")
    public InventoryResponse confirm(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.confirm(request);
    }
}
