package com.springboot.crud.service;

import com.springboot.crud.model.Product;
import com.springboot.crud.model.ProductDto;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    void createProduct(ProductDto productDto);
    void deleteProduct(int id);
    Product getProductById(int id);
    void editProduct(int id, ProductDto productDto);
}
