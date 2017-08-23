package net.proselyte.springsecurityapp.service;

import net.proselyte.springsecurityapp.model.Product;

import java.util.List;

public interface SearchService {
    List<Product> getNextPage();
    List<Product> getFirstPage(String query);
}
