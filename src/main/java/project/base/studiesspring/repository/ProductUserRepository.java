package project.base.studiesspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.base.studiesspring.domain.Product;
import project.base.studiesspring.domain.ProductUser;

import java.util.List;

public interface ProductUserRepository extends JpaRepository<ProductUser, Long> {
    ProductUser findByUsername(String username);
}
