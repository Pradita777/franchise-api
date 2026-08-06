package com.accenture.franchiseapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest(@NotBlank(message = "Franchise name is required")String name) {
}