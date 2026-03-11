resource "azurerm_resource_group" "this" {
  name     = "rg-${var.core_app_code}-${var.core_environment}"
  location = var.core_azure_location
  tags = {
    environment = var.core_environment
    code        = var.core_app_code
  }
}

#region Identity
# Main User Assigned Identity for the app to use for accessing resources
resource "azurerm_user_assigned_identity" "this" {
  name                = "uai-${var.core_app_code}-${var.core_environment}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
}

# Allows GitHub Actions to do anything in this resource group.
resource "azurerm_user_assigned_identity" "github_actions" {
  name                = "uai-gha-${var.core_app_code}-${var.core_environment}"
  resource_group_name = azurerm_resource_group.this.name
  location            = azurerm_resource_group.this.location
}
resource "azurerm_role_assignment" "github_actions_contributor" {
  scope                = azurerm_resource_group.this.id
  principal_id         = azurerm_user_assigned_identity.github_actions.principal_id
  role_definition_name = "Contributor"
}
# Allows GitHub Actions to login using OIDC and get a token
resource "azurerm_federated_identity_credential" "github_actions" {
  name      = "fic-gha-${var.core_app_code}-${var.core_environment}"
  parent_id = azurerm_user_assigned_identity.github_actions.id
  audience  = ["api://AzureADTokenExchange"]
  issuer    = "https://token.actions.githubusercontent.com"
  subject   = "repo:${var.github_org}/${var.github_repo}:environment:development"
}
#endregion
#region Blob Storage
resource "azurerm_storage_account" "this" {
  name                      = "st${var.core_app_code}${var.core_environment}"
  resource_group_name       = azurerm_resource_group.this.name
  location                  = azurerm_resource_group.this.location
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  shared_access_key_enabled = true
}
resource "azurerm_role_assignment" "this_storage_account" {
  scope                = azurerm_storage_account.this.id
  principal_id         = azurerm_user_assigned_identity.this.principal_id
  role_definition_name = "Storage Blob Data Contributor"
}
#endregion
#region Logging
resource "azurerm_log_analytics_workspace" "this" {
  name                = "law-${var.core_app_code}-${var.core_environment}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}
#endregion
#region CosmosDB
resource "azurerm_cosmosdb_account" "this" {
  name                = "cosmos-${var.core_app_code}-${var.core_environment}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  offer_type          = "Standard"
  kind                = "GlobalDocumentDB"
  consistency_policy {
    consistency_level = "Session"
  }
  geo_location {
    failover_priority = 0
    location          = azurerm_resource_group.this.location
  }
}
resource "azurerm_cosmosdb_sql_database" "this" {
  name                = "cosmosdb-sql-${var.core_app_code}-${var.core_environment}"
  resource_group_name = azurerm_resource_group.this.name
  account_name        = azurerm_cosmosdb_account.this.name
}
resource "azurerm_cosmosdb_sql_container" "session_summaries" {
  name                = "session_summaries"
  resource_group_name = azurerm_resource_group.this.name
  account_name        = azurerm_cosmosdb_account.this.name
  database_name       = azurerm_cosmosdb_sql_database.this.name
  partition_key_paths = ["/userId"]
}
resource "azurerm_cosmosdb_sql_role_assignment" "this" {
  resource_group_name = azurerm_resource_group.this.name
  account_name        = azurerm_cosmosdb_account.this.name
  role_definition_id  = "${azurerm_cosmosdb_account.this.id}/sqlRoleDefinitions/00000000-0000-0000-0000-000000000002"
  principal_id        = azurerm_user_assigned_identity.this.principal_id
  scope               = azurerm_cosmosdb_account.this.id
}
#endregion
#region EventHubs
resource "azurerm_eventhub_namespace" "this" {
  name                          = "evhns-${var.core_app_code}-${var.core_environment}"
  location                      = azurerm_resource_group.this.location
  resource_group_name           = azurerm_resource_group.this.name
  sku                           = "Standard"
  public_network_access_enabled = true
  local_authentication_enabled  = true
  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.this.id]
  }
}
resource "azurerm_eventhub" "this" {
  name              = "evh-${var.core_app_code}-${var.core_environment}"
  namespace_id      = azurerm_eventhub_namespace.this.id
  partition_count   = 4
  message_retention = 7

  # This periodically saves all events in EventHub to Blob Storage for archival and reprocessing purposes
  capture_description {
    enabled             = true
    encoding            = "Avro"
    interval_in_seconds = 60
    skip_empty_archives = false
    destination {
      name                = "EventHubArchive.AzureBlockBlob"
      archive_name_format = "{Namespace}/{EventHub}/{PartitionId}/{Year}/{Month}/{Day}/{Hour}/{Minute}/{Second}"
      blob_container_name = azurerm_storage_container.evh_event_log.name
      storage_account_id  = azurerm_storage_account.this.id
    }
  }
}
resource "azurerm_role_assignment" "this_evh" {
  scope                = azurerm_eventhub_namespace.this.id
  principal_id         = azurerm_user_assigned_identity.this.principal_id
  role_definition_name = "Azure Event Hubs Data Owner"
}
# Used for checkpointing EventHub consumer progress
resource "azurerm_storage_container" "evh_checkpoint" {
  name                  = "stb-evh-checkpoint-${var.core_app_code}-${var.core_environment}"
  storage_account_id    = azurerm_storage_account.this.id
  container_access_type = "private"
}
# Used for EventHub capture
resource "azurerm_storage_container" "evh_event_log" {
  name                  = "stb-evh-event-log-${var.core_app_code}-${var.core_environment}"
  storage_account_id    = azurerm_storage_account.this.id
  container_access_type = "private"
}
# Schema registry
resource "azurerm_eventhub_namespace_schema_group" "this" {
  name                 = "evh-sg-${var.core_app_code}-${var.core_environment}"
  namespace_id         = azurerm_eventhub_namespace.this.id
  schema_compatibility = "None"
  schema_type          = "Json"
}
resource "azurerm_role_assignment" "this_evh_schema_registry" {
  scope                = azurerm_eventhub_namespace_schema_group.this.id
  principal_id         = azurerm_user_assigned_identity.this.principal_id
  role_definition_name = "Schema Registry Reader"
}

#endregion
#region Container Apps
resource "azurerm_container_registry" "this" {
  name                = "acr${var.core_app_code}${var.core_environment}"
  location            = azurerm_resource_group.this.location
  resource_group_name = azurerm_resource_group.this.name
  sku                 = "Standard"
  admin_enabled       = false
}
resource "azurerm_role_assignment" "this_acr" {
  scope                = azurerm_container_registry.this.id
  principal_id         = azurerm_user_assigned_identity.this.principal_id
  role_definition_name = "AcrPull"
}
resource "azurerm_container_app_environment" "this" {
  name                       = "acaenv-${var.core_app_code}-${var.core_environment}"
  location                   = azurerm_resource_group.this.location
  resource_group_name        = azurerm_resource_group.this.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.this.id
  logs_destination           = "log-analytics"
}
resource "azurerm_container_app" "this" {
  name                         = "aca-${var.core_app_code}-${var.core_environment}"
  container_app_environment_id = azurerm_container_app_environment.this.id
  resource_group_name          = azurerm_resource_group.this.name
  revision_mode                = "Single"
  ingress {
    target_port                = 80
    allow_insecure_connections = true
    external_enabled           = true
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }
  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.this.id]
  }
  template {
    min_replicas               = 0
    max_replicas               = 5
    cooldown_period_in_seconds = 60
    custom_scale_rule {
      name             = "eventhub-scaler"
      custom_rule_type = "azure-eventhub"
      metadata = {
        consumerGroup             = "$Default"
        unprocessedEventThreshold = "5"
        blobContainer             = azurerm_storage_container.evh_checkpoint.name
        checkpointStrategy        = "blobMetadata"
        eventHubName              = azurerm_eventhub.this.name
        eventHubNamespace         = azurerm_eventhub_namespace.this.name
        storageAccountName        = azurerm_storage_account.this.name
      }
      authentication {
        secret_name       = "eventhub-connection"
        trigger_parameter = "connection"
      }
      authentication {
        secret_name       = "storage-connection"
        trigger_parameter = "storageConnection"
      }
    }
    container {
      name   = "app"
      image  = "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest"
      cpu    = 0.25
      memory = "0.5Gi"
      # Injects the secrets from the ACA Environment into the container
      dynamic "env" {
        for_each = local.runtime_config
        content {
          name  = env.key
          value = env.value
        }
      }
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "cloud-dev"
      }
      startup_probe {
        port      = 80
        transport = "TCP"
        initial_delay = 60 # Wait Xs for the app to start up
        interval_seconds = 10
        failure_count_threshold = 10 # If after X attempts the app is still failing, then container is unhealthy
        timeout = 5 # Wait 5s for each probe attempt before considering it a failure
      }
    }
  }
  registry {
    server   = azurerm_container_registry.this.login_server
    identity = azurerm_user_assigned_identity.this.id
  }
  secret {
    name  = "eventhub-connection"
    value = azurerm_eventhub_namespace.this.default_primary_connection_string
  }
  secret {
    name  = "storage-connection"
    value = azurerm_storage_account.this.primary_connection_string
  }
  lifecycle {
    # This does work. But for some reason, Intellij flags this as an unresolved reference. Trust me bro
    # What it does it ignore the image field in the container definition so it doesn't replace the existing container...
    # noinspection HILUnresolvedReference
    ignore_changes = [
      template[0].container[0].image
    ]
  }
}
#endregion
