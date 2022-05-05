package project.base.studiesspring.integration;


import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.domain.ProductUser;
import project.base.studiesspring.repository.ProductRepository;
import project.base.studiesspring.repository.ProductUserRepository;
import project.base.studiesspring.requests.ProductPostRequestBody;
import project.base.studiesspring.util.ProductCreator;
import project.base.studiesspring.util.ProductPostRequestBodyCreator;
import project.base.studiesspring.wrapper.PageableResponse;

import java.util.List;

@Log4j2
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate restTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate restTemplateRoleAdmin;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductUserRepository productUserRepository;

    private static final ProductUser ADMIN = ProductUser.builder()
            .name("Leo tests")
            .password("{bcrypt}$2a$10$77VbhAJsbv0UxrJaGDv0NuXFYq9Zq4/HLy/vsJ4YAjEkFjjLOK8ra")
            .authorities("ROLE_ADMIN,ROLE_USER")
            .username("leo")
            .build();

    private static final ProductUser USER = ProductUser.builder()
            .name("Leo tests")
            .password("{bcrypt}$2a$10$77VbhAJsbv0UxrJaGDv0NuXFYq9Zq4/HLy/vsJ4YAjEkFjjLOK8ra")
            .authorities("ROLE_USER")
            .username("leo1")
            .build();


    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("leo1","leo");
            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("leo","leo");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("Return list products inside page object when successful")
    void list_ReturnListOfProductsInsidePageObject_WhenSuccessful(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(USER);
        String expectedName = productSaved.getName();
        PageableResponse<Product> productPage = restTemplateRoleUser.exchange("/products", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Product>>() {
                }).getBody();
        log.info(productPage);
        log.info(productSaved);

        Assertions.assertThat(productPage).isNotNull();
        Assertions.assertThat(productPage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(productPage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Return list products when successful")
    void listAll_ReturnListOfProducts_WhenSuccessful(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(USER);
        String expectedName = productSaved.getName();
        List<Product> products = restTemplateRoleUser.exchange("/products/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();

        Assertions.assertThat(products)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
        Assertions.assertThat(products.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("Return product when successful")
    void findById_ReturnProduct_WhenSuccessful(){
        Product productSaved = productRepository.save( Product.builder().name("Leo test 01").id(1L).build());
        productUserRepository.save(USER);
        Long productId = productSaved.getId();

        Product productResult = restTemplateRoleUser.getForObject("/products/{id}",Product.class, productId);
        Assertions.assertThat(productResult).isNotNull();

        Assertions.assertThat(productResult.getId()).isNotNull().isEqualTo(productId);
    }
    @Test
    @DisplayName("Return list of product when successful")
    void findByName_ReturnListOfProduct_WhenSuccessful(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(USER);
        String expectedName = productSaved.getName();
        String url = String.format("/products/find?name=%s",expectedName);
        List<Product> productList = restTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();

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
        productUserRepository.save(ADMIN);
        List<Product> productList = restTemplateRoleAdmin.exchange("/products/find?name=dbz", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Product>>() {
                }).getBody();
        log.info(productList);
        Assertions.assertThat(productList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Return product")
    void save_ReturnProduct_WhenSuccessful(){
        productUserRepository.save(USER);
       ProductPostRequestBody productPostRequestBody = ProductPostRequestBodyCreator.createProductPostRequestBody();
       ResponseEntity<Product> productResponseEntity = restTemplateRoleUser.postForEntity("/products",productPostRequestBody,Product.class);
       Assertions.assertThat(productResponseEntity).isNotNull();
       Assertions.assertThat(productResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
       Assertions.assertThat(productResponseEntity.getBody()).isNotNull();
       Assertions.assertThat(productResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace update product when successful")
    void replace_UpdateProduct_WhenSuccessful(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(USER);
        productSaved.setName("new name");
        ResponseEntity<Void> productResponseEntity = restTemplateRoleUser.exchange("/products",HttpMethod.PUT,new HttpEntity<>(productSaved),Void.class);
        log.info(productResponseEntity);
        Assertions.assertThat(productResponseEntity).isNotNull();
        Assertions.assertThat(productResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    @DisplayName("delete remove product when successful")
    void delete_RemoveProduct_WhenSuccessful(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(ADMIN);
        ResponseEntity<Void> productResponseEntity = restTemplateRoleAdmin.exchange("/products/admin/{id}",HttpMethod.DELETE,null,Void.class, productSaved.getId());
        log.info(productResponseEntity);
        Assertions.assertThat(productResponseEntity).isNotNull();
        Assertions.assertThat(productResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("delete returns 403 when is not admin")
    void delete_Returns403_WhenUserIsNotAdmin(){
        Product productSaved = productRepository.save(ProductCreator.createProductForBeSaved());
        productUserRepository.save(USER);
        ResponseEntity<Void> productResponseEntity = restTemplateRoleUser.exchange("/products/admin/{id}",HttpMethod.DELETE,null,Void.class, productSaved.getId());
        Assertions.assertThat(productResponseEntity).isNotNull();
        Assertions.assertThat(productResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
