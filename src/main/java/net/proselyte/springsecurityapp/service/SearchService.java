package net.proselyte.springsecurityapp.service;

import net.proselyte.springsecurityapp.model.Product;

import java.util.List;

public interface SearchService {
    List<Product> getProductsFromBelchip(String query);
    List<Product> getProductsFromChipDip(String query);
    List<Product> getNextPage(String query);
}
