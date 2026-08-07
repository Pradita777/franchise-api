# Infraestructura — franchise-api

Terraform de los dos componentes de infraestructura del proyecto en Azure:

- **VM** `franchise-api` (Ubuntu 24.04, `Standard_D2ads_v7`, zona 1, Trusted Launch)
  con su VNet, subnet, NSG, IP pública y NIC — resource group `franchise-api_group`
  en `centralus`.
- **Cosmos DB for MongoDB (vCore)** `franquicia-api-cluster` (tier `Free`, Mongo 8.0,
  32 GB, 1 shard) con sus reglas de firewall — resource group `franquicia-api-rg`
  en `westus`.

Los valores por defecto de [variables.tf](variables.tf) fueron leídos con `az` de la
suscripción real, así que el código refleja la infraestructura tal como está hoy.

## Archivos

| Archivo | Contenido |
|---|---|
| [versions.tf](versions.tf) | Terraform ≥ 1.7, provider `azurerm` ≥ 4.53 |
| [main.tf](main.tf) | Los dos resource groups |
| [network.tf](network.tf) | VNet, subnet, IP pública, NSG + reglas HTTP/SSH, NIC |
| [vm.tf](vm.tf) | Clave SSH y VM Linux |
| [cosmos.tf](cosmos.tf) | Cluster Mongo vCore y sus reglas de firewall |
| [outputs.tf](outputs.tf) | IPs, ids y `mongodb_uri` |
| [imports.tf](imports.tf) | Bloques `import` para adoptar lo que ya existe |

## Uso

```bash
cd franchise-api/infra/terraform
cp terraform.tfvars.example terraform.tfvars   # y poner mongo_admin_password
az login
terraform init
terraform plan
terraform apply
```

`imports.tf` hace que el primer `apply` **importe** los recursos existentes en vez
de crear duplicados. El plan verificado contra la suscripción real es:

```
Plan: 17 to import, 0 to add, 1 to change, 0 to destroy.
```

El único cambio es `azurerm_mongo_cluster.main`, porque Azure nunca devuelve el
password del administrador: Terraform lo escribe siempre en el primer apply. Si el
valor de `mongo_admin_password` es el actual, el cluster no cambia. Si es otro, el
password queda rotado.

Una vez importado todo, `imports.tf` se puede borrar.

Para levantar un entorno nuevo desde cero: borra `imports.tf` y cambia
`compute_resource_group_name`, `data_resource_group_name`, `vm_name` y
`mongo_cluster_name` en `terraform.tfvars`.

## Conexión de la API

`application.yml` espera la variable de entorno `mongodburi`. Se obtiene con:

```bash
terraform output -raw mongodb_uri
```

## Notas

- El `state` queda local y está en `.gitignore` junto con `terraform.tfvars`. Para
  trabajo compartido conviene un backend remoto (`azurerm` con storage account).
- `mongo_admin_password` no tiene default: al importar debe coincidir con el password
  actual del cluster, si no el primer `apply` lo va a rotar.
- La regla de firewall `0.0.0.0`–`0.0.0.0` del cluster es la que Azure usa para
  "Allow public access from Azure services".
- El NSG hoy permite SSH (22) desde cualquier origen. `ssh_source_address_prefix`
  permite restringirlo sin tocar el código.
- El nombre de la NIC (`franchise-api670_z1`) y el del disco de SO conservan el
  sufijo aleatorio que generó el portal, para que el import calce; en un entorno
  nuevo conviene simplificarlos.
