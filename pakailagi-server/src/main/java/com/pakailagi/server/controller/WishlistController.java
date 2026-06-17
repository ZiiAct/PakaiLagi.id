package com.pakailagi.server.controller;

import com.pakailagi.server.entity.Wishlist;
import com.pakailagi.server.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public List<Wishlist> getAllWishlists() {
        return wishlistService.getAllWishlists();
    }

    @PostMapping
    public Wishlist addWishlist(@RequestBody Wishlist wishlist) {
        return wishlistService.addWishlist(wishlist);
    }
}