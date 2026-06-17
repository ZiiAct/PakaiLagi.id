package com.pakailagi.server.controller;

import com.pakailagi.server.entity.ItemCategory;
import com.pakailagi.server.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ItemCategoryController {

    @Autowired
    private ItemCategoryService itemCategoryService;

    @GetMapping
    public List<ItemCategory> getAllCategories() {
        return itemCategoryService.getAllCategories();
    }

    @PostMapping
    public ItemCategory createCategory(@RequestBody ItemCategory category) {
        return itemCategoryService.createCategory(category);
    }
}