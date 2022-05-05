package project.base.studiesspring.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.repository.ProductRepository;
import project.base.studiesspring.util.ProductCreator;
import project.base.studiesspring.util.ProductPostRequestBodyCreator;
import project.base.studiesspring.util.ProductPutRequestBodyCreator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepositoryMock;

    @BeforeEach
    void setUp(){
        PageImpl<Product> productPage =  new PageImpl<>(List.of(ProductCreator.createProductValid()));
        BDDMockito.when(productRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(productPage);

        BDDMockito.when(productRepositoryMock.findAll())
                .thenReturn(List.of(ProductCreator.createProductValid()));

        BDDMockito.when(productRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(ProductCreator.createProductValid()));

        BDDMockito.when(productRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(ProductCreator.createProductValid()));

        BDDMockito.when(productRepositoryMock.save(ArgumentMatchers.any(Product.class)))
                .thenReturn(ProductCreator.createProductValid());

        BDDMockito.doNothing().when(productRepositoryMock).delete(ArgumentMatchers.any(Product.class));
    }

    @Test
    @DisplayName("listAll return list products inside page object when successful")
    void listAll_ReturnListOfProductsInsidePageObject_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();
        Page<Product> productPage = productService.listAll(PageRequest.of(1,1));

        Assertions.assertThat(productPage).isNotNull();
        Assertions.assertThat(productPage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(productPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("listAllNonPageable return list products when successful")
    void listAllNonPageable_ReturnListOfProducts_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();

        List<Product> productList = productService.listAllNonPageable();

        Assertions.assertThat(productList).isNotNull();
        Assertions.assertThat(productList)
                .isNotEmpty()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(productList.get(0).getName()).isEqualTo(expectedName);
    }
    @Test
    @DisplayName("findByIdOrThrowBadRequestException return product when successful")
    void findByIdOrThrowBadRequestException_ReturnProduct_WhenSuccessful(){
        Product product = ProductCreator.createProductValid();

        Product productResult = productService.findByIdOrThrowBadRequestException(product.getId());

        Assertions.assertThat(productResult).isNotNull();

        Assertions.assertThat(productResult.getId()).isNotNull().isEqualTo(product.getId());
    }

    @Test
    @DisplayName("Return list of product when successful")
    void findByName_ReturnListOfProduct_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();

        List<Product> productList = productService.findByName("name");

        Assertions.assertThat(productList).isNotNull();
        Assertions.assertThat(productList)
                .isNotEmpty()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(productList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Return empty list when not found")
    void findByName_ReturnEmptyList_WhenProductNotFound(){
        BDDMockito.when(productRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Product> productList = productService.findByName("name");
        Assertions.assertThat(productList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return product")
    void save_ReturnProduct_WhenSuccessful(){
        Long expectedId = ProductCreator.createProductValid().getId();
        Product product = this.productService.save(ProductPostRequestBodyCreator.createProductPostRequestBody());
        Assertions.assertThat(product).isNotNull();
        Assertions.assertThat(product).isEqualTo(ProductCreator.createProductValid());
        Assertions.assertThat(product.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("replace update product when successful")
    void replace_UpdateProduct_WhenSuccessful(){
        Assertions.assertThatCode(() -> productService.replace(ProductPutRequestBodyCreator.createProductPutRequestBody()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete remove product when successful")
    void delete_RemoveProduct_WhenSuccessful(){
        Assertions.assertThatCode(() -> productService.delete(1L))
                .doesNotThrowAnyException();

    }

}