package com.kuong.inventory.service;

import com.kuong.inventory.dto.InventoryRequest;
import com.kuong.inventory.dto.InventoryResponse;
import com.kuong.inventory.entity.Inventory;
import com.kuong.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse reserve(InventoryRequest request) {
        Inventory inventory = findInventory(request.productId());
        int available = inventory.getAvailableQuantity();
        int quantity = request.quantity();

        if (available < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        inventory.setAvailableQuantity(available - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        return toResponse(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponse release(InventoryRequest request) {
        Inventory inventory = findInventory(request.productId());
        int reserved = inventory.getReservedQuantity();
        int quantity = request.quantity();

        if (reserved < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient reserved stock");
        }

        inventory.setReservedQuantity(reserved - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        return toResponse(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponse confirm(InventoryRequest request) {
        Inventory inventory = findInventory(request.productId());
        int reserved = inventory.getReservedQuantity();
        int quantity = request.quantity();

        if (reserved < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient reserved stock");
        }

        inventory.setReservedQuantity(reserved - quantity);

        return toResponse(inventoryRepository.save(inventory));
    }

    private Inventory findInventory(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}
