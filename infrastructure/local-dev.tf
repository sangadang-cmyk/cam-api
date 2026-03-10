# This gets the current logged in user info (from az login)
data "azurerm_client_config" "current" {}

# Give my logged in account access to CosmosDB for local development
resource "azurerm_cosmosdb_sql_role_assignment" "local" {
  count               = var.enabled_local_access ? 1 : 0
  resource_group_name = azurerm_resource_group.this.name
  account_name        = azurerm_cosmosdb_account.this.name
  role_definition_id  = "${azurerm_cosmosdb_account.this.id}/sqlRoleDefinitions/00000000-0000-0000-0000-000000000002"
  principal_id        = data.azurerm_client_config.current.object_id
  scope               = azurerm_cosmosdb_account.this.id
}

# Give my logged in account access to Storage Account for local development
resource "azurerm_role_assignment" "local_storage" {
  count                = var.enabled_local_access ? 1 : 0
  scope                = azurerm_cosmosdb_account.this.id
  principal_id         = data.azurerm_client_config.current.object_id
  role_definition_name = "Storage Blob Data Contributor"
}

# Give my logged in account access to EventHubs for local development
resource "azurerm_role_assignment" "local_evh_sender" {
  count                = var.enabled_local_access ? 1 : 0
  scope                = azurerm_eventhub_namespace.this.id
  principal_id         = data.azurerm_client_config.current.object_id
  role_definition_name = "Azure Event Hubs Data Sender"
}

resource "azurerm_role_assignment" "local_evh_receiver" {
  count                = var.enabled_local_access ? 1 : 0
  scope                = azurerm_eventhub_namespace.this.id
  principal_id         = data.azurerm_client_config.current.object_id
  role_definition_name = "Azure Event Hubs Data Receiver"
}

# Give my logged in account access to the EventHub Capture Blob Storage
resource "azurerm_role_assignment" "local_this_evh_event_log" {
  count                = var.enabled_local_access ? 1 : 0
  scope                = azurerm_storage_container.evh_event_log.id
  principal_id         = data.azurerm_client_config.current.object_id
  role_definition_name = "Storage Blob Data Contributor"
}

resource "azurerm_role_assignment" "local_this_evh_schema_registry" {
  count                = var.enabled_local_access ? 1 : 0
  scope                = azurerm_eventhub_namespace_schema_group.this.id
  principal_id         = data.azurerm_client_config.current.object_id
  role_definition_name = "Schema Registry Reader"
}