package de.tschuehly.easy.spring.auth.web;

public interface Page {
    record NavigationItem(String displayName, String URI){}

    NavigationItem navigationItem();
}