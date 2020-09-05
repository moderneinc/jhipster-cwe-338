#!/bin/sh

gcloud functions deploy cwe338 --entry-point io.moderne.cwe338.CloudFunction --runtime java11 --trigger-http --memory 512MB --allow-unauthenticated
