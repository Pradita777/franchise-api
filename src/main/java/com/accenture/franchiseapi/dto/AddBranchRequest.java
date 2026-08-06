package com.accenture.franchiseapi.dto;

import jakarta.validation.constraints.NotBlank;

public record AddBranchRequest(@NotBlank(message = "Branch name is required")String name) {
}