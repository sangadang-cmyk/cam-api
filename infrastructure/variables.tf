#region Root variables
variable "core_app_code" {
  type    = string
  default = "cam"
}

variable "core_app_type" {
  type    = string
  default = "api"
}

variable "core_environment" {
  type    = string
  default = "dev"
}

variable "core_azure_location" {
  type    = string
  default = "southeastasia"
}

variable "enabled_local_access" {
  type    = bool
  default = true
}

variable "github_environment" {
  type    = string
  default = "development"
}

variable "github_org" {
  type    = string
  default = "sangadang-cmyk"
}

variable "github_repo" {
  type    = string
  default = "cam-api"
}
#endregion

variable "app_container_name" {
  type    = string
  default = "app"
}

variable "sidecar_container_name" {
  type    = string
  default = "grafana-alloy"
}

variable "grafana_endpoint" {
  type    = string
  default = "n/a"
}

variable "grafana_api_key" {
  type    = string
  default = "n/a"
}
