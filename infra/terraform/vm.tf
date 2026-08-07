resource "azurerm_ssh_public_key" "main" {
  name                = "${var.vm_name}_key"
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  public_key          = var.ssh_public_key
  tags                = var.tags
}

resource "azurerm_linux_virtual_machine" "main" {
  name                = var.vm_name
  computer_name       = var.vm_name
  resource_group_name = azurerm_resource_group.compute.name
  location            = azurerm_resource_group.compute.location
  size                = var.vm_size
  zone                = var.vm_zone
  tags                = var.tags

  admin_username                  = var.admin_username
  disable_password_authentication = true

  network_interface_ids = [azurerm_network_interface.main.id]

  admin_ssh_key {
    username   = var.admin_username
    public_key = var.ssh_public_key
  }

  # Refleja el estado actual de la VM; sin el bloque, el import genera un diff.
  additional_capabilities {
    hibernation_enabled = false
    ultra_ssd_enabled   = false
  }

  # Trusted Launch (securityType = TrustedLaunch en la VM actual).
  secure_boot_enabled = true
  vtpm_enabled        = true

  source_image_reference {
    publisher = "canonical"
    offer     = "ubuntu-24_04-lts"
    sku       = "server"
    version   = "latest"
  }

  os_disk {
    name                 = "${var.vm_name}_OsDisk_1_935650728cfd4afa8fb4ceb30f2b8cda"
    caching              = "ReadWrite"
    storage_account_type = "Premium_LRS"
    disk_size_gb         = 30
  }

  boot_diagnostics {}

  # El disco de SO se recrea desde imagen; su nombre lo fija Azure al crear.
  lifecycle {
    ignore_changes = [os_disk[0].name]
  }
}
