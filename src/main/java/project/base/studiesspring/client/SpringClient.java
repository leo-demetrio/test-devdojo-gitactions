package project.base.studiesspring.client;


import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import project.base.studiesspring.domain.Product;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Product> productResponseEntity = new RestTemplate().getForEntity("http://localhost:8080/api/v1/products/7", Product.class);
        log.info(productResponseEntity);

        Product product = new RestTemplate().getForObject("http://localhost:8080/api/v1/products/7", Product.class);
        log.info(product);

        Product[] productArray = new RestTemplate().getForObject("http://localhost:8080/api/v1/products/all", Product[].class);
        log.info(Arrays.toString(productArray));

        ResponseEntity<List<Product>> productList =
                new RestTemplate().exchange("http://localhost:8080/api/v1/products/all", HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {});
        log.info(productList.getBody());

        Product productBuilder = Product.builder().name("Leo post").build();
        Product productSaved = new RestTemplate().postForObject("http://localhost:8080/api/v1/products",productBuilder, Product.class);
        log.info(productSaved);

        Product productPost = Product.builder().name("Leo post headers").build();
        ResponseEntity<Product> productSavedWithHeaders =
                new RestTemplate().exchange("http://localhost:8080/api/v1/products",
                        HttpMethod.POST,
                        new HttpEntity<>(productPost,createJsonHeader()),
                        Product.class);
        log.info(productSavedWithHeaders);

        Product productToUpdate = productSavedWithHeaders.getBody();
        productToUpdate.setName("Leo update");
        ResponseEntity<Void> productUpdated =
                new RestTemplate().exchange("http://localhost:8080/api/v1/products",
                        HttpMethod.PUT,
                        new HttpEntity<>(productToUpdate,createJsonHeader()),
                        void.class);
        log.info(productUpdated);

        ResponseEntity<Void> productDeleted =
                new RestTemplate().exchange("http://localhost:8080/api/v1/products/{id}",
                        HttpMethod.DELETE,
                        new HttpEntity<>(productToUpdate,createJsonHeader()),
                        void.class, productToUpdate.getId());
        log.info(productUpdated);
    }
    public static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
