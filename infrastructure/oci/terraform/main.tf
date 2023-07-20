terraform {
  required_providers {
    oci = {
      source  = "oracle/oci"
      version = ">= 4.0.0"
    }
  }
}

provider "oci" {
  region = var.region
}

resource "oci_core_vcn" "this" {
  display_name   = "Website"
  compartment_id = var.compartment_id
  cidr_blocks    = ["10.15.0.0/16"]
  dns_label      = "website"

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_core_internet_gateway" "this" {
  display_name   = "Website Internet Gateway"
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id
  enabled        = true

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_core_default_route_table" "this" {
  display_name               = "Website Default Routing Table"
  manage_default_resource_id = oci_core_vcn.this.default_route_table_id

  route_rules {
    description       = "Allow communication between the VCN and the internet (without this traffic from within the VCN will not find its way to the internet)"
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
    network_entity_id = oci_core_internet_gateway.this.id
  }

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_core_default_security_list" "this" {
  display_name               = "Website Default Security List"
  manage_default_resource_id = oci_core_vcn.this.default_security_list_id

  egress_security_rules {
    description = "Allow all egress traffic (without this traffic from within the VCN will not be allowed to the internet)"
    destination = "0.0.0.0/0"
    protocol    = "all"
  }

  ingress_security_rules {
    description = "Allow HTTP from the anywhere (without this cannot HTTP to the website)"
    protocol    = "6"
    source      = "0.0.0.0/0"

    tcp_options {
      min = 80
      max = 80
    }
  }

  ingress_security_rules {
    description = "Allow HTTPS from the anywhere (without this cannot HTTPS to the website)"
    protocol    = "6"
    source      = "0.0.0.0/0"

    tcp_options {
      min = 443
      max = 443
    }
  }

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_core_subnet" "public" {
  display_name               = "Website Public Subnet"
  compartment_id             = var.compartment_id
  vcn_id                     = oci_core_vcn.this.id
  cidr_block                 = "10.15.1.0/24"
  prohibit_internet_ingress  = false
  prohibit_public_ip_on_vnic = false
  dns_label                  = "public"

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

data "oci_objectstorage_namespace" "this" {
  compartment_id = var.compartment_id
}

resource "oci_objectstorage_bucket" "website" {
  name           = "website"
  compartment_id = var.compartment_id
  namespace      = data.oci_objectstorage_namespace.this.namespace
  access_type    = "ObjectReadWithoutList"

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_apigateway_gateway" "website" {
  display_name   = "Website API Gateway"
  compartment_id = var.compartment_id
  endpoint_type  = "PUBLIC"
  subnet_id      = oci_core_subnet.public.id

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}

resource "oci_apigateway_deployment" "website" {
  display_name   = "Website API Gateway Deployment"
  compartment_id = var.compartment_id
  gateway_id     = oci_apigateway_gateway.website.id
  path_prefix    = "/"

  specification {
    routes {
      backend {
        type = "HTTP_BACKEND"
        url  = "https://objectstorage.${var.region}.oraclecloud.com/n/${oci_objectstorage_bucket.website.namespace}/b/${oci_objectstorage_bucket.website.name}/o/$${request.path[object]}"
      }
      path    = "/{object*}"
      methods = ["GET"]
    }
  }

  defined_tags  = var.defined_tags
  freeform_tags = var.freeform_tags

  lifecycle {
    ignore_changes = [defined_tags]
  }
}