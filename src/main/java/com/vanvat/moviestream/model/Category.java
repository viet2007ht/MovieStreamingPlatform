package com.vanvat.moviestream.model;

import java.util.Objects;

/**
 * A movie genre/category, e.g. "Action", "Sci-Fi".
 */
public class Category {

    private String id;
    private String name;
    private String description;
    private boolean deleted;

    public Category() {
    }

    public Category(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deleted = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Serializes to a single pipe-delimited line for file storage.
     * Any literal '|' or newline in field values is escaped so the
     * line format stays parseable.
     */
    public String toFileLine() {
        return String.join("|",
                id,
                escape(name),
                escape(description == null ? "" : description),
                String.valueOf(deleted));
    }

    public static Category fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        Category c = new Category();
        c.id = parts[0];
        c.name = unescape(parts[1]);
        c.description = parts.length > 2 ? unescape(parts[2]) : "";
        c.deleted = parts.length > 3 && Boolean.parseBoolean(parts[3]);
        return c;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n");
    }

    private static String unescape(String value) {
        return value.replace("\\n", "\n").replace("\\p", "|").replace("\\\\", "\\");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
