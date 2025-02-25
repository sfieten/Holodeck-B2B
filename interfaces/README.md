# Holodeck B2B Interfaces

This project contains the interfaces layer of Holodeck B2B which defines the public API that can be used to build custom extensions to add or replace functionality to/in the standard Holodeck B2B distribution. 

## License
This project is licensed under the under the **Lesser GPL v3 licence** which means that Holodeck B2B extensions can be built using a different license, and possibly a closed source license, then the GPLv3 which applies to the implementation of the Holodeck B2B Core and default components as found in the _[implementations](../implementations)_ project.

## Usage
As the interfaces are published to Maven Central you can directly include the interfaces as a dependency:
```xml
    <dependency>
		<groupId>org.holodeckb2b</groupId>
		<artifactId>holodeckb2b-interfaces</artifactId>
		<version>${project.version}</version>
    </dependency>
```
Other option is to clone the Holodeck B2B repository and build the interfaces locally. This will be required when you want to make a new version of Holodeck B2B. In this case you will first need to build the [parent project](../) and install it into the local Maven repository.  

## Issues / RFCs
You can report issues directly on the [parent project Issue Tracker](https://github.com/holodeck-b2b/Holodeck-B2B/issues).
As the interfaces project is doesn't have much code itself, issues are probably about the specified functionality. When you would like to have something added or changed please clearily describe the use case for the requested change.

## Versioning
The interface project uses the same version as the [parent project](../).

