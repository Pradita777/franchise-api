terraform {
  # >= 1.7: los bloques `import` de imports.tf usan for_each.
  required_version = ">= 1.7.0"

  required_providers {
    azurerm = {
      source = "hashicorp/azurerm"
      # >= 4.53: primera version con azurerm_mongo_cluster_firewall_rule.
      version = ">= 4.53.0, < 5.0.0"
    }
  }
}

provider "azurerm" {
  subscription_id = var.subscription_id

  features {
    virtual_machine {
      # La VM actual tiene deleteOption=Delete en el OS disk.
      delete_os_disk_on_deletion = true
    }
  }
}
