variable "subscription_id" {
  description = "Id de la suscripcion de Azure."
  type        = string
  default     = "0724e345-ca1b-4766-903a-110671bde61f"
}

# ---------------------------------------------------------------------------
# Compute / red (resource group franchise-api_group, centralus)
# ---------------------------------------------------------------------------

variable "compute_resource_group_name" {
  description = "Resource group que contiene la VM y su red."
  type        = string
  default     = "franchise-api_group"
}

variable "compute_location" {
  description = "Region de la VM y su red."
  type        = string
  default     = "centralus"
}

variable "vm_name" {
  description = "Nombre de la maquina virtual."
  type        = string
  default     = "franchise-api"
}

variable "vm_size" {
  description = "SKU de la VM."
  type        = string
  default     = "Standard_D2ads_v7"
}

variable "vm_zone" {
  description = "Zona de disponibilidad de la VM, el disco y la IP publica."
  type        = string
  default     = "1"
}

variable "admin_username" {
  description = "Usuario administrador de la VM (SSH por clave, sin password)."
  type        = string
  default     = "azureuser"
}

variable "ssh_public_key" {
  description = "Clave publica SSH autorizada en la VM."
  type        = string
  default     = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQCvF/J1w1KOWJWZ6FBO8hxi7cBAJv1NmeMkfLBBPBQD7InsZ4BI3nLL4AOq/D/UYhHZpcw/jdR7HmXTbtV5tZ+oWbIMtVsZs8KN+BGRvRRuvkFN0J5PKsdd9JnFHQ4OnuI01XTz0N/yYuIJ39XJmxp6w3SbEb8mmVqlWzeu3Zq7UQsyP+AAgLpE+UA4KOviaKCNsv0aF0uVcBTchWFalWYdpTOo3vDwshkVSqE06rZ7NmWlDrPlCs+d/zlisbmhZoU1UDXgCi0hHLBFQa7ctA8r4V9sOScB8ekePPtHK1up/SbDP9GrX4auTdA3I0RLNbtpzWGjapytH0ZIJwM3blK2mExU3LE+/PxqwdmquoFlPwDavkTaxSnWxjmDRMADa4Eo9Ra8ZZcuo54hD93ukYbRP07wKSXLKpIlnBgQqPkNBhSwkElFJztixRfFf/sOAK1PaRtrDPSG8KuRbcXemZUBMwlqnS3Nk2zdE1q9L/lWwtCiIf0MIw1y0p+4AXZa2a0= generated-by-azure"
}

variable "vnet_address_space" {
  description = "Espacio de direcciones de la VNet."
  type        = list(string)
  default     = ["10.0.0.0/16"]
}

variable "subnet_address_prefixes" {
  description = "Prefijos de la subnet default."
  type        = list(string)
  default     = ["10.0.0.0/24"]
}

variable "ssh_source_address_prefix" {
  description = "Origen permitido para SSH (22). '*' es lo que hay hoy; conviene restringirlo a una IP."
  type        = string
  default     = "*"
}

variable "tags" {
  description = "Tags aplicados a todos los recursos."
  type        = map(string)
  default     = {}
}

# ---------------------------------------------------------------------------
# Cosmos DB for MongoDB vCore (resource group franquicia-api-rg, westus)
# ---------------------------------------------------------------------------

variable "data_resource_group_name" {
  description = "Resource group del cluster de Cosmos DB for MongoDB (vCore)."
  type        = string
  default     = "franquicia-api-rg"
}

variable "data_location" {
  description = "Region del cluster de Cosmos DB."
  type        = string
  default     = "westus"
}

variable "mongo_cluster_name" {
  description = "Nombre del cluster de Cosmos DB for MongoDB vCore."
  type        = string
  default     = "franquicia-api-cluster"
}

variable "mongo_admin_username" {
  description = "Usuario administrador del cluster de Mongo."
  type        = string
  default     = "dbuser5xr6o2"
}

variable "mongo_admin_password" {
  description = "Password del administrador del cluster de Mongo. No tiene default a proposito."
  type        = string
  sensitive   = true

  # Azure rechaza el password con un 400 durante el apply si no cumple esto.
  # Validarlo aca hace que falle en el plan, con un mensaje entendible.
  validation {
    condition = (
      length(var.mongo_admin_password) >= 8 &&
      length(var.mongo_admin_password) <= 256 &&
      ((can(regex("[a-z]", var.mongo_admin_password)) ? 1 : 0) +
        (can(regex("[A-Z]", var.mongo_admin_password)) ? 1 : 0) +
        (can(regex("[0-9]", var.mongo_admin_password)) ? 1 : 0) +
      (can(regex("[^a-zA-Z0-9]", var.mongo_admin_password)) ? 1 : 0)) >= 3
    )
    error_message = "El password debe tener entre 8 y 256 caracteres e incluir al menos 3 de estos 4 tipos: minusculas, mayusculas, numeros, simbolos."
  }
}

variable "mongo_server_version" {
  description = "Version del servidor Mongo."
  type        = string
  default     = "8.0"
}

variable "mongo_compute_tier" {
  description = "Tier de computo del cluster ('Free' es el actual)."
  type        = string
  default     = "Free"
}

variable "mongo_storage_size_gb" {
  description = "Almacenamiento del cluster en GB."
  type        = number
  default     = 32
}

variable "mongo_shard_count" {
  description = "Cantidad de shards."
  type        = number
  default     = 1
}

variable "mongo_firewall_rules" {
  description = "Reglas de firewall del cluster. La regla 0.0.0.0/0.0.0.0 habilita 'Allow Azure services'."
  type = map(object({
    start_ip_address = string
    end_ip_address   = string
  }))
  default = {
    "AllowAllAzureServicesAndResourcesWithinAzureIps_2026-8-6_0-33-20" = {
      start_ip_address = "0.0.0.0"
      end_ip_address   = "0.0.0.0"
    }
    "ClientIPAddress_2026-8-6-0-30-17" = {
      start_ip_address = "181.58.39.38"
      end_ip_address   = "181.58.39.38"
    }
    "ClientIPAddress_2026-8-6-18-38-29" = {
      start_ip_address = "190.242.101.110"
      end_ip_address   = "190.242.101.110"
    }
    "ClientIPAddress_Juli" = {
      start_ip_address = "190.158.28.179"
      end_ip_address   = "190.158.28.179"
    }
  }
}
