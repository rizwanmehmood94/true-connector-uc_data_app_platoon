# Changelog
All notable changes to this project will be documented in this file.

## [1.7.8] - 2023-11-16

### Changed
 - cosign version upgrade with alpine image; must include --insecure-ignore-tlog flag
 - changed classpath folder in pom from /etc to /config
 
## [1.7.6] - 2023-10-30

### Changed

 - CertificationCheck now uses Cosign to verify if running version is signed

## [1.7.5] - 2023-10-02

### Added

 - add Security level and connector access policies
 - add GHA (Git Hub Actions)
 - changed healthcheck to https in Dockerfile
 
### Changed 
 - bumped spring-boot-parent to 2.5.14

## [1.7.4] - 2023-08-21

### Changed

 - change certified version

## [1.7.3] - 2023-08-01

### Changed

 - add log folder, and healtcheck in Dockerfile

## [1.7.2] - 2023-07-17

### Added

 - logic for checking if running version of UC app is certified or not

## [1.7.1] - 2023-06-22

### Changed

 - renamed maven groupId and artifactId
 - add checking uploaded policy is compatible with UC
  
## [1.7.0] - 2023-06-21

### Changed

 - Add requestedArtifact as mandatory filed for enforcing policy
 - Fixed bug with prohibition policy enforcement 
 - Add http port property (for http internal communication)
 - PIP Dockerfile changed to match other TC images; removed pip.property file from pip docker image

## [1.6.0] - 2023-05-30

### Changed
 
 - Switch to temurin base docker image
 - using non root user for java process
 - Upgraded infomodel to 4.2.7.
 - Fixed bug in RuleUtils when enforcing policy 

## [1.0.1] - 2023-02-??

### Changed

 - using externalized property files
 - 2 database profiles: H2, with persisting files and PostgreSQL