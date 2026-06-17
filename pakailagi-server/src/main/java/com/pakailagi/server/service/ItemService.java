package com.pakailagi.server.service;

import com.pakailagi.server.entity.Item;
import com.pakailagi.server.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item createItem(Item item) {
        // Logika bisnis: Secara otomatis set ketersediaan barang menjadi "Tersedia" saat pertama kali diunggah
        item.setAvailability("Tersedia");
        return itemRepository.save(item);
    }
}