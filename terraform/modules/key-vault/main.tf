terraform {
  required_providers {
    azurecaf = {
      source  = "aztfmod/azurecaf"
      version = "1.2.22"
    }
  }
}

resource "azurecaf_name" "key_vault" {
  random_length = "15"
  resource_type = "azurerm_key_vault"
  suffixes      = [var.environment]
}

  access_policy {
     tenant_id = "${data.azurerm_client_config.current.tenant_id}"
     object_id = "${data.azurerm_client_config.current.service_principal_object_id}"


     key_permissions = [
"get","list","update","create","import","delete","recover","backup","restore",
     ]

secret_permissions = [
  "get","list","delete","recover","backup","restore","set",
     ]

certificate_permissions = [
  "get","list","update","create","import","delete","recover","backup","restore", "deleteissuers", "getissuers", "listissuers", "managecontacts", "manageissuers", "setissuers",
]
  }

data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "application" {
  name                = azurecaf_name.key_vault.result
  resource_group_name = var.resource_group
  location            = var.location

  tenant_id                  = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days = 90

  sku_name = "standard"

  network_acls {
    default_action             = "Deny"
    bypass                     = "None"
    virtual_network_subnet_ids = [var.subnet_id]
    ip_rules                   = [var.myip]
  }

  tags = {
    "environment"      = var.environment
    "application-name" = var.application_name
  }
}

resource "azurerm_key_vault_access_policy" "client" {
  key_vault_id = azurerm_key_vault.application.id
  tenant_id    = data.azurerm_client_config.current.tenant_id
  object_id    = data.azurerm_client_config.current.object_id

  key_permissions = [
    "Get",
    "Update",
    "Create",
    "Import",
    "List",
    "Delete",
    "Recover",
    "Backup",
    "Restore"
  ]

  secret_permissions = [
    "Set",
    "Get",
    "List",
    "Delete",
    "Recover",
    "Backup",
    "Restore"
  ]


}

resource "azurerm_key_vault_secret" "database_username" {
  name         = "database-username"
  value        = var.database_username
  key_vault_id = azurerm_key_vault.application.id

  depends_on = [ azurerm_key_vault_access_policy.client ]
}

resource "azurerm_key_vault_secret" "database_password" {
  name         = "database-password"
  value        = var.database_password
  key_vault_id = azurerm_key_vault.application.id

  depends_on = [ azurerm_key_vault_access_policy.client ]
}
