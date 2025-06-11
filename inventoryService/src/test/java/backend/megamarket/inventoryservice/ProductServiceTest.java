package backend.megamarket.inventoryservice;

import backend.megamarket.inventoryservice.entity.ProductEntity;
import backend.megamarket.inventoryservice.repository.ProductRepository;
import backend.megamarket.inventoryservice.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductEntity product1;
    private ProductEntity product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product1 = new ProductEntity();
        product1.setId(1L);
        product1.setName("Product1");
        product1.setQuantity(10L);

        product2 = new ProductEntity();
        product2.setId(2L);
        product2.setName("Product2");
        product2.setQuantity(5L);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        List<ProductEntity> products = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductEntity> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertTrue(result.contains(product1));
        assertTrue(result.contains(product2));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteAndReturnTrue() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        boolean deleted = productService.deleteProduct(1L);

        assertTrue(deleted);
        verify(productRepository).delete(product1);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldReturnFalse() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = productService.deleteProduct(99L);

        assertFalse(deleted);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void addProduct_WhenProductExists_ShouldIncreaseQuantity() {
        ProductEntity newProduct = new ProductEntity();
        newProduct.setName("Product1");
        newProduct.setQuantity(5L);

        when(productRepository.existsByName("Product1")).thenReturn(true);
        when(productRepository.findByName("Product1")).thenReturn(product1);
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductEntity saved = productService.addProduct(newProduct);

        assertEquals(15, saved.getQuantity()); // 10 + 5
        verify(productRepository).save(product1);
    }

    @Test
    void addProduct_WhenProductDoesNotExist_ShouldSaveNewProduct() {
        ProductEntity newProduct = new ProductEntity();
        newProduct.setName("NewProduct");
        newProduct.setQuantity(7L);

        when(productRepository.existsByName("NewProduct")).thenReturn(false);
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        ProductEntity saved = productService.addProduct(newProduct);

        assertEquals("NewProduct", saved.getName());
        assertEquals(7, saved.getQuantity());
        verify(productRepository).save(newProduct);
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        ProductEntity found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Product1", found.getName());
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnNull() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ProductEntity found = productService.getProductById(99L);

        assertNull(found);
    }
}
