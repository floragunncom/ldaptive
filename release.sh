#!/bin/bash
mvn clean release:clean release:prepare -Pdistribution,apache,jldap,opendj,unboundid,ldapi
