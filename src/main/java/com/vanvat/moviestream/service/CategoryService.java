package com.vanvat.moviestream.service;

import com.vanvat.moviestream.exception.NotFoundException;
import com.vanvat.moviestream.exception.ValidationException;
import com.vanvat.moviestream.model.Category;
import com.vanvat.moviestream.repository.CategoryRepository;
import com.vanvat.moviestream.util.IdGenerator;
import com.vanvat.moviestream.util.Validator;

import java.util.Comparator;
import java.util.List;

public class CategoryService {

    private final CategoryRepository repository;
    private final IdGenerator idGenerator;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
        int maxId = repository.findAll().stream()
                .map(c -> c.getId().replace("CT-", ""))
                .mapToInt(s -> s.matches("\\d+") ? Integer.parseInt(s) : 0)
                .max().orElse(0);
        this.idGenerator = new IdGenerator("CT", maxId);
    }

    public Category create(String name, String description) throws ValidationException {
        Validator.requireNonBlank(name, "Category name");
        Category c = new Category(idGenerator.next(), name.trim(), description);
        return repository.save(c);
    }

    public Category update(String id, String name, String description)
            throws ValidationException, NotFoundException {
        Validator.requireNonBlank(name, "Category name");
        Category existing = getById(id);
        existing.setName(name.trim());
        existing.setDescription(description);
        return repository.save(existing);
    }

    public void delete(String id) throws NotFoundException {
        getById(id); // throws if missing
        repository.deleteById(id);
    }

    public Category getById(String id) throws NotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    public List<Category> getAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Category::getName))
                .toList();
    }
}
