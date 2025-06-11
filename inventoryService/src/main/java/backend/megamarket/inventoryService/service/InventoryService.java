package backend.megamarket.inventoryservice.service;

import jakarta.annotation.PostConstruct;

import java.io.IOException;

public interface InventoryService {
    @PostConstruct
    void start() throws IOException;
}
