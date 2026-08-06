package com.accenture.franchiseapi.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(@NotBlank(message = "Product name is required")String name) {
}