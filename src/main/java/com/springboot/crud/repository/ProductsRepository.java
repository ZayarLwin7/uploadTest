package com.springboot.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.crud.model.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{
	
}
