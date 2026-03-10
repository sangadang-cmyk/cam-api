terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "=4.62.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "=2.7.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "=3.2.4"
    }
  }
}

provider "azurerm" {
  features {}
}
