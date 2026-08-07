resource "azurerm_resource_group" "compute" {
  name     = var.compute_resource_group_name
  location = var.compute_location
  tags     = var.tags
}

resource "azurerm_resource_group" "data" {
  name     = var.data_resource_group_name
  location = var.data_location
  tags     = var.tags
}
