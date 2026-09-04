package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.Category;

import java.util.List;

public class CategoryRepository extends AbstractFileRepository<Category, String> {

    public CategoryRepository(String filePath) {
        super(filePath, Category::toFileLine, Category::fromFileLine, Category::getId);
    }

    @Override
    public List<Category> findAll() {
        return super.findAll().stream()
                .filter(c -> !c.isDeleted())
                .toList();
    }

    public List<Category> findAllIncludingDeleted() {
        return super.findAll();
    }
}
