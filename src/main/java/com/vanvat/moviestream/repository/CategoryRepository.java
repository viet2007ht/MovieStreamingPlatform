package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.model.Category;

public class CategoryRepository extends AbstractFileRepository<Category, String> {

    public CategoryRepository(String filePath) {
        super(filePath, Category::toFileLine, Category::fromFileLine, Category::getId);
    }
}
