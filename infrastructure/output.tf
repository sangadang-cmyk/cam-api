#region FOR CLOUD RUNTIME
locals {
  runtime_config = {
    AZURE_COSMOS_ENDPOINT              = azurerm_cosmosdb_account.this.endpoint
    AZURE_COSMOS_DATABASE              = azurerm_cosmosdb_sql_database.this.name
    AZURE_COSMOS_CONTAINER             = azurerm_cosmosdb_sql_container.session_summaries.name
    AZURE_EVENTHUB_NAMESPACE           = azurerm_eventhub_namespace.this.name
    AZURE_EVENTHUB_NAME                = azurerm_eventhub.this.name
    AZURE_STORAGE_ACCOUNT              = azurerm_storage_account.this.name
    AZURE_STORAGE_ACCOUNT_ENDPOINT     = "https://${azurerm_storage_account.this.name}.blob.core.windows.net"
    AZURE_STORAGE_CHECKPOINT_CONTAINER = azurerm_storage_container.evh_checkpoint.name
    # This is the main full access client ID
    AZURE_MANAGED_IDENTITY_CLIENT_ID   = azurerm_user_assigned_identity.this.client_id
  }
  github_actions_config = {
    # This is the GitHub Actions client ID
    AZURE_CLIENT_ID       = azurerm_user_assigned_identity.github_actions.client_id
    AZURE_SUBSCRIPTION_ID = data.azurerm_client_config.current.subscription_id
    AZURE_TENANT_ID       = data.azurerm_client_config.current.tenant_id
    ACR_NAME              = azurerm_container_registry.this.name
    CONTAINER_APP_NAME    = azurerm_container_app.this.name
    RESOURCE_GROUP   = azurerm_resource_group.this.name
  }
}
#endregion

#region FOR LOCAL RUNTIME
resource "local_file" "this" {
  filename = "../.env.infra"
  content  = join("\n", [for k, v in local.runtime_config : "${k}=${v}"])
}
#endregion

#region GITHUB ACTIONS ENVIRONMENT
resource "local_file" "github_actions" {
  filename = "../.env.gha"
  content  = join("\n", [for k, v in local.github_actions_config : "${k}=${v}"])
}
# resource "null_resource" "push_secrets_to_github" {
#   triggers = {
#     config_hash = sha256(join(";", [for k, v in local.github_actions_config : "${k}=${v}"]))
#   }
#   provisioner "local-exec" {
#     command = "gh secret set --env-file ${local_file.github_actions.filename} --env ${var.github_environment}"
#   }
#   
#   depends_on = [local_file.github_actions]
# }
output "github_actions_env" {
  value = local.github_actions_config
}
#endregion
