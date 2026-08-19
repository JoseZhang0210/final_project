package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.entity.Product;
import com.hotel.repository.CategoryRepository;
import com.hotel.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(
            ProductService productService,
            CategoryRepository categoryRepository) {

        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute(
                "products",
                productService.findAllProducts());

        return "products/list";
    }

    @GetMapping("/products/add")
    public String showAddForm(Model model) {

        Product product = new Product();
        product.setStatus("ACTIVE");

        model.addAttribute("product", product);
        model.addAttribute(
                "categories",
                categoryRepository.findAll());

        return "products/add";
    }

@GetMapping("/products/edit/{id}")
public String showEditForm(
        @PathVariable Integer id,
        Model model
) {
    Product product = productService.findById(id);

    if (product == null) {
        return "redirect:/products";
    }

    model.addAttribute("product", product);
    model.addAttribute(
            "categories",
            categoryRepository.findAll()
    );

    return "products/edit";
}

@PostMapping("/products/update")
public String updateProduct(
        @ModelAttribute Product formProduct
) {
    Product existingProduct =
            productService.findById(formProduct.getProductId());

    if (existingProduct == null) {
        return "redirect:/products";
    }

    existingProduct.setProductName(
            formProduct.getProductName()
    );

    existingProduct.setCategory(
            formProduct.getCategory()
    );

    existingProduct.setDescription(
            formProduct.getDescription()
    );

    existingProduct.setPrice(
            formProduct.getPrice()
    );

    existingProduct.setStock(
            formProduct.getStock()
    );

    existingProduct.setImageUrl(
            formProduct.getImageUrl()
    );

    existingProduct.setStatus(
            formProduct.getStatus()
    );

    productService.save(existingProduct);

    return "redirect:/products";
}

@GetMapping("/products/delete/{id}")
public String deleteProduct(
        @PathVariable Integer id
) {
    Product product = productService.findById(id);

    if (product != null) {
        productService.deleteById(id);
    }

    return "redirect:/products";
}
}