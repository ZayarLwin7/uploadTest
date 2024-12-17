package com.springboot.crud.service;

import com.springboot.crud.model.Product;
import com.springboot.crud.model.ProductDto;
import com.springboot.crud.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductsRepository repo;

    @Override
    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    @Override
    public void createProduct(ProductDto productDto) {
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String originalFileName = image.getOriginalFilename();

        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique file name
            String storageFileName = generateUniqueFileName(originalFileName);

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            Product product = new Product();
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(createdAt);
            product.setImageFileName(storageFileName);

            repo.save(product);

        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
        }
    }
    
    @Override
    public Product getProductById(int id) {
        return repo.findById(id).orElse(null);
    }
    
    @Override
    public void editProduct(int id, ProductDto productDto) {
        Product product = repo.findById(id).orElse(null); // Get product or null if not found
        if (product == null) {
            throw new IllegalArgumentException("Product not found with this_id: " + id);
        }
        
        String uploadDir = "public/images/";

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        
        try {
            if (!productDto.getImageFile().isEmpty()) {
                // Delete old image file if imageFileName is not null
                if (product.getImageFileName() != null) {
                    Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                    Files.delete(oldImagePath);
                }

                // Save new image file with unique file name
                MultipartFile image = productDto.getImageFile();
                String storageFileName = generateUniqueFileName(image.getOriginalFilename());
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            }
            repo.save(product);
        } catch (Exception ex) {
            System.out.println("IOException occurred: " + ex.getMessage());
        }
    }
  
    @Override
    public void deleteProduct(int id) {
        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        repo.deleteById(id);
        deleteImageFile(product.getImageFileName());
    }

    private void deleteImageFile(String fileName) {
        String uploadDir = "public/images/";
        Path imagePath = Paths.get(uploadDir + fileName);
        try {
            Files.deleteIfExists(imagePath);
        } catch (Exception ex) {
            System.out.println("Failed to delete image file: " + ex.getMessage());
            // Log the error or handle it based on your application's requirements
        }
    }
    
    private String generateUniqueFileName(String originalFileName) {
        // Extract file extension
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        
     // Extract file name without extension
        String fileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        
        // Time to Millisecond_UUID first 5 character
        String uuid = Instant.now().toEpochMilli() + "_" + UUID.randomUUID().toString().substring(0, 5);

        // Combine truncated original name + UUID + file extension
        String uniqueFileName = fileName + "_" + uuid + fileExtension;

        return uniqueFileName;
    }

}
