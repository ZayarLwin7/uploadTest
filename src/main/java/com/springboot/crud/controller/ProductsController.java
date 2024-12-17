package com.springboot.crud.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.crud.model.Product;
import com.springboot.crud.model.ProductDto;
import com.springboot.crud.service.ProductService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products")
public class ProductsController {
	
    @Autowired
    private ProductService productService;

    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/index";
    }
	
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}
	
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
    	
		if (productDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("productDto","imageFile", "The image file is required"));
		}
		
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        productService.createProduct(productDto);
        return "redirect:/products";
    }
     
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return "redirect:/products";
            }
            // To display id, pass "product" model
            model.addAttribute("product", product);
            
            // Create a new ProductDto object and set its properties from the retrieved product
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            // Add the productDto object to the model
            model.addAttribute("productDto", productDto);
            
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }
    
    @PostMapping("/edit")
    public String editProduct(Model model, @RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        try {       	
        	Product product = productService.getProductById(id);
            
           	model.addAttribute("product", product);
        	model.addAttribute("productDto", productDto);
        	
            if (result.hasErrors()) {
                return "products/EditProduct";
            }
            productService.editProduct(id, productDto);
            // Redirect to the product list page after editing
            return "redirect:/products";
            
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            return "redirect:/products";
        }
    }
    
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            productService.deleteProduct(id);
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            // Handle exception if needed
        }
        // Redirect to the product list page after deletion
        return "redirect:/products";
    }
    
    
}
