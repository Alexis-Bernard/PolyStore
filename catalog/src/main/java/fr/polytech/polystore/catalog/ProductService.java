package fr.polytech.polystore.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Product create(Product product) throws ResponseStatusException {
        logger.info("Creating product: " + product);

        return productRepository.save(product);
    }

    public List<Product> findAll() {
        logger.info("Getting all products");

        return productRepository.findAll();
    }

    public void deleteById(String id) {
        logger.info("Deleting product: " + id);

        productRepository.deleteById(id);
    }
}