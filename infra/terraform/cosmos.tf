resource "azurerm_mongo_cluster" "main" {
  name                = var.mongo_cluster_name
  resource_group_name = azurerm_resource_group.data.name
  location            = azurerm_resource_group.data.location

  administrator_username = var.mongo_admin_username
  administrator_password = var.mongo_admin_password

  compute_tier           = var.mongo_compute_tier
  storage_size_in_gb     = var.mongo_storage_size_gb
  shard_count            = var.mongo_shard_count
  version                = var.mongo_server_version
  high_availability_mode = "Disabled"
  public_network_access  = "Enabled"

  tags = var.tags
}

resource "azurerm_mongo_cluster_firewall_rule" "rules" {
  for_each = var.mongo_firewall_rules

  name             = each.key
  mongo_cluster_id = azurerm_mongo_cluster.main.id
  start_ip_address = each.value.start_ip_address
  end_ip_address   = each.value.end_ip_address
}
