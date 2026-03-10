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
  default = "sangdang-cmyk"
}

variable "github_repo" {
  type    = string
  default = "cam-api"
}
#endregion
