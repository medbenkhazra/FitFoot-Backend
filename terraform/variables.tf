variable "application_name" {
  type        = string
  description = "The name of your application"
  default     = "FitFoot--3878-7909"
}

variable "environment" {
  type        = string
  description = "The environment (dev, test, prod...)"
  default     = ""
}

variable "location" {
  type        = string
  description = "The Azure region where all resources in this example should be created"
  default     = "eastus"
}

variable "terraform_storage_account" {
  type        = string
  description = "The terraform_storage_account declared by mohammed"
  default     = "st7557091497791976"
}
