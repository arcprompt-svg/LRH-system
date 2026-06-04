#!/bin/bash

# DNS Verification Script for LRH System
# Helps automate/instruct the process of adding TXT records.

echo "------------------------------------------------"
echo " LRH System - DNS Verification Utility"
echo "------------------------------------------------"

# Prompt for inputs
read -p "Enter Domain Name (e.g. wiki.supachai.org): " DOMAIN
if [ -z "$DOMAIN" ]; then
    echo "Error: Domain name is required."
    exit 1
fi

read -p "Enter TXT Record Name (default: _acme-challenge): " RECORD_NAME
RECORD_NAME=${RECORD_NAME:-_acme-challenge}

read -p "Enter Verification String: " VERIF_STRING
if [ -z "$VERIF_STRING" ]; then
    echo "Error: Verification string is required."
    exit 1
fi

echo ""
echo "------------------------------------------------"
echo " INSTRUCTIONS FOR CONFIGURATION"
echo "------------------------------------------------"
echo "Type:   TXT"
echo "Host:   $RECORD_NAME.$DOMAIN"
echo "Value:  $VERIF_STRING"
echo "TTL:    3600"
echo "------------------------------------------------"
echo ""

echo "Example for DigitalOcean CLI (doctl):"
echo "doctl compute domain records create $DOMAIN --record-type TXT --record-name $RECORD_NAME --record-data \"$VERIF_STRING\" --record-ttl 3600"
echo ""
echo "Example for Vercel CLI:"
echo "vercel dns add $DOMAIN $RECORD_NAME TXT \"$VERIF_STRING\""
echo ""
echo "Waiting for propagation... (Checking via dig)"
echo "You can check progress with: dig TXT $RECORD_NAME.$DOMAIN +short"
echo ""
echo "Verification process initiated. Monitor the dashboard for status updates."
