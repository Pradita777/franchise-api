output "vm_id" {
  description = "Id de la maquina virtual."
  value       = azurerm_linux_virtual_machine.main.id
}

output "vm_public_ip" {
  description = "IP publica de la VM."
  value       = azurerm_public_ip.main.ip_address
}

output "vm_private_ip" {
  description = "IP privada de la VM."
  value       = azurerm_linux_virtual_machine.main.private_ip_address
}

output "vm_ssh_command" {
  description = "Comando para conectarse por SSH."
  value       = "ssh ${var.admin_username}@${azurerm_public_ip.main.ip_address}"
}

output "mongo_cluster_id" {
  description = "Id del cluster de Cosmos DB for MongoDB vCore."
  value       = azurerm_mongo_cluster.main.id
}

output "mongo_host" {
  description = "Host SRV del cluster de Mongo."
  value       = "${azurerm_mongo_cluster.main.name}.mongocluster.cosmos.azure.com"
}

output "mongodb_uri" {
  description = "Cadena de conexion para la variable de entorno 'mongodburi' de la API."
  sensitive   = true
  value = format(
    "mongodb+srv://%s:%s@%s.mongocluster.cosmos.azure.com/?tls=true&authMechanism=SCRAM-SHA-256&retrywrites=false&maxIdleTimeMS=120000",
    var.mongo_admin_username,
    urlencode(var.mongo_admin_password),
    azurerm_mongo_cluster.main.name,
  )
}
