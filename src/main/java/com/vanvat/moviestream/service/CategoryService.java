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

    // Set after construction (see Main) to break the CategoryService <-> MovieService
    // circular dependency: MovieService needs a CategoryService at construction time
    // (to validate a movie's categoryId), while CategoryService only needs MovieService
    // later, to check for referencing movies before a delete.
    private MovieService movieService;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
        int maxId = repository.findAll().stream()
                .map(c -> c.getId().replace("CT-", ""))
                .mapToInt(s -> s.matches("\\d+") ? Integer.parseInt(s) : 0)
                .max().orElse(0);
        this.idGenerator = new IdGenerator("CT", maxId);
    }

    public void setMovieService(MovieService movieService) {
        this.movieService = movieService;
    }

    public Category create(String name, String description) throws ValidationException {
        Validator.requireNonBlank(name, "Category name");
        requireUniqueName(name.trim(), null);
        Category c = new Category(idGenerator.next(), name.trim(), description);
        return repository.save(c);
    }

    public Category update(String id, String name, String description)
            throws ValidationException, NotFoundException {
        Validator.requireNonBlank(name, "Category name");
        Category existing = getById(id);
        requireUniqueName(name.trim(), id);
        existing.setName(name.trim());
        existing.setDescription(description);
        return repository.save(existing);
    }

    /**
     * Enforces referential integrity: a category still referenced by at
     * least one movie can't be deleted, or every one of those movies would
     * be left pointing at a nonexistent categoryId.
     */
    public void delete(String id) throws NotFoundException, ValidationException {
        getById(id); // throws if missing
        if (movieService != null) {
            long referencing = movieService.getAll().stream()
                    .filter(m -> m.getCategoryId().equals(id))
                    .count();
            if (referencing > 0) {
                throw new ValidationException(
                        "Cannot delete category " + id + ": " + referencing
                                + " movie(s) still reference it. Reassign or delete them first.");
            }
        }
        repository.deleteById(id);
    }

    /** Category names must be unique (case-insensitive), matching how users pick them by name. */
    private void requireUniqueName(String name, String excludingId) throws ValidationException {
        boolean duplicate = repository.findAll().stream()
                .anyMatch(c -> (excludingId == null || !c.getId().equals(excludingId))
                        && c.getName().equalsIgnoreCase(name));
        if (duplicate) {
            throw new ValidationException("A category named '" + name + "' already exists.");
        }
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
