package project.base.studiesspring.repository;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.util.ProductCreator;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Log4j2
@DataJpaTest
@DisplayName("Tests for Product Repository")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Test persist product when successful")
    void save_PersistProduct_WhenSuccessful(){
        Product product = ProductCreator.createProductForBeSaved();
        Product productSaved = this.productRepository.save(product);
        Assertions.assertThat(productSaved).isNotNull();
        Assertions.assertThat(productSaved.getId()).isNotNull();
        Assertions.assertThat(productSaved.getName()).isEqualTo(product.getName());
    }
    @Test
    @DisplayName("Test update product when successful")
    void save_UpdateProduct_WhenSuccessful(){
        Product productForBeSaved = ProductCreator.createProductForBeSaved();
        Product productSaved = this.productRepository.save(productForBeSaved);
        productSaved.setName("Leo test 01 update");
        Product productUpdated = this.productRepository.save(productSaved);
        log.info("Test product " + productUpdated);
        Assertions.assertThat(productUpdated).isNotNull();
        Assertions.assertThat(productUpdated.getId()).isNotNull();
        Assertions.assertThat(productUpdated.getName()).isEqualTo(productSaved.getName());
    }
    @Test
    @DisplayName("Test delete product when successful")
    void delete_RemoveProduct_WhenSuccessful(){
        Product productForBeSaved = ProductCreator.createProductForBeSaved();
        Product productSaved = this.productRepository.save(productForBeSaved);
        this.productRepository.delete(productSaved);
        Optional<Product> productOptional = this.productRepository.findById(productSaved.getId());
        Assertions.assertThat(productOptional.isEmpty()).isTrue();
        Assertions.assertThat(productOptional).isEmpty();
    }

    @Test
    @DisplayName("Test find by product for name when successful")
    void findByName_ReturnListProduct_WhenSuccessful(){
        Product productForBeSaved = ProductCreator.createProductForBeSaved();
        Product productSaved = this.productRepository.save(productForBeSaved);
        List<Product> productList = this.productRepository.findByName(productSaved.getName());
        Assertions.assertThat(productList).isNotEmpty();
        Assertions.assertThat(productList).contains(productSaved);
    }
    @Test
    @DisplayName("Test find by product for name when returned not found")
    void findByName_ReturnEmptyList_WhenProductNotFound(){
        List<Product> productList = this.productRepository.findByName("returnEmptyList");
        Assertions.assertThat(productList).isEmpty();
    }

    @Test
    @DisplayName("Test throw ConstraintViolationException when name is empty")
    void save_ThrowConstraintViolationException_WhenNameIsEmpty(){
        Product product = new Product();
        Assertions.assertThatThrownBy(() -> this.productRepository.save(product))
            .isInstanceOf(ConstraintViolationException.class);

    }
    private Product createProduct(){
        return Product.builder().name("Leo test 01").build();
    }

}