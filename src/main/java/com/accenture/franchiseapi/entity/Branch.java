package com.accenture.franchiseapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    

    private String name;

    @Builder.Default
    private List<Product> products = new ArrayList<>();
}