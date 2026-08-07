# Adopcion de la infraestructura que ya existe en Azure.
#
# Estos bloques hacen que `terraform apply` importe los recursos actuales al
# state en vez de intentar crearlos de nuevo. Una vez importado todo (y con el
# state guardado), este archivo se puede borrar.
#
# Para NO importar y crear infraestructura nueva desde cero, borra este archivo
# y cambia los nombres/RGs en terraform.tfvars.

locals {
  subscription_scope = "/subscriptions/${var.subscription_id}"
  compute_rg_id      = "${local.subscription_scope}/resourceGroups/${var.compute_resource_group_name}"
  data_rg_id         = "${local.subscription_scope}/resourceGroups/${var.data_resource_group_name}"
}

import {
  to = azurerm_resource_group.compute
  id = local.compute_rg_id
}

import {
  to = azurerm_resource_group.data
  id = local.data_rg_id
}

import {
  to = azurerm_virtual_network.main
  id = "${local.compute_rg_id}/providers/Microsoft.Network/virtualNetworks/${var.vm_name}-vnet"
}

import {
  to = azurerm_subnet.default
  id = "${local.compute_rg_id}/providers/Microsoft.Network/virtualNetworks/${var.vm_name}-vnet/subnets/default"
}

import {
  to = azurerm_public_ip.main
  id = "${local.compute_rg_id}/providers/Microsoft.Network/publicIPAddresses/${var.vm_name}-ip"
}

import {
  to = azurerm_network_security_group.main
  id = "${local.compute_rg_id}/providers/Microsoft.Network/networkSecurityGroups/${var.vm_name}-nsg"
}

import {
  to = azurerm_network_security_rule.http
  id = "${local.compute_rg_id}/providers/Microsoft.Network/networkSecurityGroups/${var.vm_name}-nsg/securityRules/HTTP"
}

import {
  to = azurerm_network_security_rule.ssh
  id = "${local.compute_rg_id}/providers/Microsoft.Network/networkSecurityGroups/${var.vm_name}-nsg/securityRules/SSH"
}

import {
  to = azurerm_network_interface.main
  id = "${local.compute_rg_id}/providers/Microsoft.Network/networkInterfaces/${var.vm_name}670_z1"
}

import {
  to = azurerm_network_interface_security_group_association.main
  id = "${local.compute_rg_id}/providers/Microsoft.Network/networkInterfaces/${var.vm_name}670_z1|${local.compute_rg_id}/providers/Microsoft.Network/networkSecurityGroups/${var.vm_name}-nsg"
}

import {
  to = azurerm_ssh_public_key.main
  id = "${local.compute_rg_id}/providers/Microsoft.Compute/sshPublicKeys/${var.vm_name}_key"
}

import {
  to = azurerm_linux_virtual_machine.main
  id = "${local.compute_rg_id}/providers/Microsoft.Compute/virtualMachines/${var.vm_name}"
}

import {
  to = azurerm_mongo_cluster.main
  id = "${local.data_rg_id}/providers/Microsoft.DocumentDB/mongoClusters/${var.mongo_cluster_name}"
}

import {
  for_each = var.mongo_firewall_rules

  to = azurerm_mongo_cluster_firewall_rule.rules[each.key]
  id = "${local.data_rg_id}/providers/Microsoft.DocumentDB/mongoClusters/${var.mongo_cluster_name}/firewallRules/${each.key}"
}
