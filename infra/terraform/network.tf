resource "azurerm_virtual_network" "main" {
  name                = "${var.vm_name}-vnet"
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  address_space       = var.vnet_address_space
  tags                = var.tags
}

resource "azurerm_subnet" "default" {
  name                 = "default"
  resource_group_name  = azurerm_resource_group.compute.name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = var.subnet_address_prefixes

  private_endpoint_network_policies             = "Disabled"
  private_link_service_network_policies_enabled = true
}

resource "azurerm_public_ip" "main" {
  name                = "${var.vm_name}-ip"
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  allocation_method   = "Static"
  sku                 = "Standard"
  ip_version          = "IPv4"
  zones               = [var.vm_zone]
  tags                = var.tags
}

resource "azurerm_network_security_group" "main" {
  name                = "${var.vm_name}-nsg"
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  tags                = var.tags
}

resource "azurerm_network_security_rule" "http" {
  name                        = "HTTP"
  resource_group_name         = azurerm_resource_group.compute.name
  network_security_group_name = azurerm_network_security_group.main.name
  priority                    = 300
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "80"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
}

resource "azurerm_network_security_rule" "ssh" {
  name                        = "SSH"
  resource_group_name         = azurerm_resource_group.compute.name
  network_security_group_name = azurerm_network_security_group.main.name
  priority                    = 320
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "22"
  source_address_prefix       = var.ssh_source_address_prefix
  destination_address_prefix  = "*"
}

resource "azurerm_network_interface" "main" {
  name                = "${var.vm_name}670_z1"
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  tags                = var.tags

  ip_configuration {
    name                          = "ipconfig1"
    subnet_id                     = azurerm_subnet.default.id
    private_ip_address_allocation = "Dynamic"
    private_ip_address_version    = "IPv4"
    public_ip_address_id          = azurerm_public_ip.main.id
    primary                       = true
  }
}

resource "azurerm_network_interface_security_group_association" "main" {
  network_interface_id      = azurerm_network_interface.main.id
  network_security_group_id = azurerm_network_security_group.main.id
}
