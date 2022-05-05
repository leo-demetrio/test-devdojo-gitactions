package project.base.studiesspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.base.studiesspring.domain.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByName(String name);
}
