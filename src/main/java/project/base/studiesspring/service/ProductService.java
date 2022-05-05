package project.base.studiesspring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.exception.BadRequestException;
import project.base.studiesspring.mapper.ProductMapper;
import project.base.studiesspring.repository.ProductRepository;
import project.base.studiesspring.requests.ProductPostRequestBody;
import project.base.studiesspring.requests.ProductPutRequestBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> listAll(Pageable pageble) {
        log.info("ServiceProduct", productRepository.findAll());
        return productRepository.findAll(pageble);
    }
    public List<Product> listAllNonPageable() {
        return productRepository.findAll();
    }
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public Product findByIdOrThrowBadRequestException(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
    }

    @Transactional
    public Product save(ProductPostRequestBody productPostRequestBody) {
        Product product = ProductMapper.INSTANCE.toProduct(productPostRequestBody);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void replace(ProductPutRequestBody productPutRequestBody) {
        findByIdOrThrowBadRequestException(productPutRequestBody.getId());
        Product product = ProductMapper.INSTANCE.toProduct(productPutRequestBody);
        product.setId(product.getId());
        productRepository.save(product);
    }

}
