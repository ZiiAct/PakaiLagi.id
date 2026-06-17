package com.pakailagi.server.service;

import com.pakailagi.server.entity.Wishlist;
import com.pakailagi.server.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }

    public Wishlist addWishlist(Wishlist wishlist) {
        // Otomatis mencatat tanggal saat wishlist ditambahkan
        wishlist.setAddedDate(new Date());
        return wishlistRepository.save(wishlist);
    }
}