package com.pakailagi.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "items_category")
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_items_category")
    private Integer idCategory;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_description")
    private String categoryDescription;

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }
}