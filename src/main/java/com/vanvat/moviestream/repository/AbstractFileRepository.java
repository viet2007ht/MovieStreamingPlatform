package com.vanvat.moviestream.repository;

import com.vanvat.moviestream.util.FileUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base implementation shared by all entity repositories: loads every line
 * of the backing file into memory, applies mutations, then rewrites the
 * whole file. This is deliberately simple (fine for the dataset sizes in
 * a course project) and keeps every subclass tiny — they just supply a
 * (de)serializer and an id extractor.
 *
 * @param <T>  entity type
 * @param <ID> id type
 */
public abstract class AbstractFileRepository<T, ID> implements Repository<T, ID> {

    private final String filePath;
    private final Function<T, String> serializer;
    private final Function<String, T> deserializer;
    private final Function<T, ID> idExtractor;

    protected AbstractFileRepository(String filePath,
                                      Function<T, String> serializer,
                                      Function<String, T> deserializer,
                                      Function<T, ID> idExtractor) {
        this.filePath = filePath;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.idExtractor = idExtractor;
    }

    @Override
    public List<T> findAll() {
        return FileUtils.readLines(filePath).stream()
                .map(deserializer)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> findById(ID id) {
        return findAll().stream()
                .filter(e -> idExtractor.apply(e).equals(id))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        List<T> all = findAll();
        ID id = idExtractor.apply(entity);
        boolean replaced = false;
        for (int i = 0; i < all.size(); i++) {
            if (idExtractor.apply(all.get(i)).equals(id)) {
                all.set(i, entity);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            all.add(entity);
        }
        persist(all);
        return entity;
    }

    @Override
    public void deleteById(ID id) {
        List<T> all = findAll();
        all.removeIf(e -> idExtractor.apply(e).equals(id));
        persist(all);
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    protected void persist(List<T> all) {
        List<String> lines = all.stream().map(serializer).collect(Collectors.toList());
        FileUtils.writeLines(filePath, lines);
    }

    protected String getFilePath() {
        return filePath;
    }
}
