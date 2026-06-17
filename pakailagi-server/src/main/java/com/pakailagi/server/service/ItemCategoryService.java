package com.pakailagi.server.service;

import com.pakailagi.server.entity.ItemCategory;
import com.pakailagi.server.repository.ItemCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemCategoryService {

    @Autowired
    private ItemCategoryRepository itemCategoryRepository;

    public List<ItemCategory> getAllCategories() {
        return itemCategoryRepository.findAll();
    }

    public ItemCategory createCategory(ItemCategory category) {
        return itemCategoryRepository.save(category);
    }
}