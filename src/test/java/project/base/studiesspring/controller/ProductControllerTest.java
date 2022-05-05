package project.base.studiesspring.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.requests.ProductPostRequestBody;
import project.base.studiesspring.requests.ProductPutRequestBody;
import project.base.studiesspring.service.ProductService;
import project.base.studiesspring.util.ProductCreator;
import project.base.studiesspring.util.ProductPostRequestBodyCreator;
import project.base.studiesspring.util.ProductPutRequestBodyCreator;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productServiceMock;

    @BeforeEach
    void setUp(){
        PageImpl<Product> productPage =  new PageImpl<>(List.of(ProductCreator.createProductValid()));
        BDDMockito.when(productServiceMock.listAll(ArgumentMatchers.any()))
                    .thenReturn(productPage);

        BDDMockito.when(productServiceMock.listAllNonPageable())
                .thenReturn(List.of(ProductCreator.createProductValid()));

        BDDMockito.when(productServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(ProductCreator.createProductValid());

        BDDMockito.when(productServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(ProductCreator.createProductValid()));

        BDDMockito.when(productServiceMock.save(ArgumentMatchers.any(ProductPostRequestBody.class)))
                .thenReturn(ProductCreator.createProductValid());

        BDDMockito.doNothing().when(productServiceMock).replace(ArgumentMatchers.any(ProductPutRequestBody.class));
        BDDMockito.doNothing().when(productServiceMock).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("Return list products inside page object when successful")
    void list_ReturnListOfProductsInsidePageObject_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();
        Page<Product> productPage = productController.list(null).getBody();

        Assertions.assertThat(productPage).isNotNull();
        Assertions.assertThat(productPage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(productPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Return list products when successful")
    void listAll_ReturnListOfProducts_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();

        List<Product> productList = productController.listAll().getBody();

        Assertions.assertThat(productList).isNotNull();
        Assertions.assertThat(productList)
                .isNotEmpty()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(productList.get(0).getName()).isEqualTo(expectedName);
    }
    @Test
    @DisplayName("Return product when successful")
    void findById_ReturnProduct_WhenSuccessful(){
        Product product = ProductCreator.createProductValid();

        Product productResult = productController.findById(product.getId()).getBody();

        Assertions.assertThat(productResult).isNotNull();

        Assertions.assertThat(productResult.getId()).isNotNull().isEqualTo(product.getId());
    }

    @Test
    @DisplayName("Return list of product when successful")
    void findByName_ReturnListOfProduct_WhenSuccessful(){
        String expectedName = ProductCreator.createProductValid().getName();

        List<Product> productList = productController.findByName("name").getBody();

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
        BDDMockito.when(productServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Product> productList = productController.findByName("name").getBody();
        Assertions.assertThat(productList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return product")
    void save_ReturnProduct_WhenSuccessful(){
        Long expectedId = ProductCreator.createProductValid().getId();
        Product product = this.productController.save(ProductPostRequestBodyCreator.createProductPostRequestBody()).getBody();
        Assertions.assertThat(product).isNotNull();
        Assertions.assertThat(product).isEqualTo(ProductCreator.createProductValid());
        Assertions.assertThat(product.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("replace update product when successful")
    void replace_UpdateProduct_WhenSuccessful(){
        Assertions.assertThatCode(() -> productController.replace(ProductPutRequestBodyCreator.createProductPutRequestBody()))
                .doesNotThrowAnyException();
        ResponseEntity<Void> entity = productController.replace(ProductPutRequestBodyCreator.createProductPutRequestBody());
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete remove product when successful")
    void delete_RemoveProduct_WhenSuccessful(){
        Assertions.assertThatCode(() -> productController.delete(1))
                .doesNotThrowAnyException();
        ResponseEntity<Void> entity = productController.delete(1L);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


}