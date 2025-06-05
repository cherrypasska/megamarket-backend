package backend.megamarket.inventoryService.service.config;

import backend.megamarket.inventoryService.service.InventoryServise;
import backend.megamarket.inventoryService.service.db.dao.ProductRepository;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class InventoryServiceStarter {
    private final ProductRepository productRepository;
    private Server server;
    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder
                .forPort(9090)
                .addService(new InventoryServise(productRepository))
                .build()
                .start();
        System.out.println("Server started, listening on " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.shutdown();
            }
        }));
    }
}
