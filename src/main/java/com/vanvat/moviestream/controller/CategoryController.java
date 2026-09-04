package com.vanvat.moviestream.controller;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.exception.ValidationException;
import com.vanvat.moviestream.model.Category;
import com.vanvat.moviestream.service.CategoryService;

import java.util.List;

public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Category addCategory(String name, String description) throws ValidationException {
        return categoryService.create(name, description);
    }

    public Category editCategory(String id, String name, String description)
            throws ValidationException, NotFoundException {
        return categoryService.update(id, name, description);
    }

    public void deleteCategory(String id) throws NotFoundException {
        categoryService.delete(id);
    }

    public List<Category> listAll() {
        return categoryService.getAll();
    }

    public Category getById(String id) throws NotFoundException {
        return categoryService.getById(id);
    }
}
