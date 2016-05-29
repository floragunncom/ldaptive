#!/bin/bash
mvn clean release:clean release:prepare -Papache,jldap,opendj,unboundid,ldapi
