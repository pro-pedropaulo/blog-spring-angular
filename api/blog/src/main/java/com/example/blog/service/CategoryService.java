package com.example.blog.service;

import com.example.blog.model.Category;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> update(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDetails.getName());
                    return categoryRepository.save(existingCategory);
                });
    }

    public boolean delete(Long id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return true;
                }).orElse(false);
    }
}
