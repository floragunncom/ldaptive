#!/bin/bash
mvn clean verify -Pdistribution,apache,jldap,opendj,unboundid,ldapi
