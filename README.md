AIOLOS Application Example
==========================

This project uses the AIOLOS framework which is automatically downloaded when this app is run. This project can only be run when a released version of the AIOLOS Framework is downloadable on [http://openstack1.test/](http://openstack1.test/).

The framework uses Felix OSGi Container and a Cloud Environment (currently OpenStack) to develop distributed applications.

An application is developed inside Eclipse with [BNDTools](http://bndtools.org/). Documentation on developing with BNDTools can be found on  [BNDTools](http://bndtools.org/tutorial.html).


The projects
------------

The example application consists of 6 projects: the Bndtools configuration project `cnf`, some application
implementation bundles `org.example.api`, `org.example.impls` and `org.example.servlet`, an Android
client application `org.example.android` and the AIOLOS `tools` project.

### The Bndtools configuration project

Bndtools uses a `cnf` project to keep workspace specific settings. Various settings can be configured
in the `ext/defaults.bnd` file, such as Java Compiler and BND options. In the `ext/repositories.bnd` file
you can configure which BND plugins to use, and which repositories should be used to provision your
OSGi runtime.

The `cnf` project also contains local repositories. The `localrepo` folder is used for 3rd party
libraries that are required by this project and are not available in an online repository.
The `releaserepo` folder is used to deploy the artifacts to when using the Bndtools release feature.

### The implementation bundles

The implementation projects `org.example.api`, `org.example.impls` and `org.example.servlet` are
regular Bndtools projects that contain an example OSGi application. The `org.example.api` project
contains the API that provides the `Greeting` service. In `org.example.impls` two implementations
of this `Greeting` service are provided, as well as a Gogo shell command to invoke the service 
from command line. The `org.example.servlet` project provides a `HttpService` to make the service
available from a web interface. These are regular OSGi projects, that use the Declarative Services
specification for binding services. These bundles are not dependent in any way of AIOLOS, and the
Greeting application can run standalone without AIOLOS using the .bndrun configurations in `org.example.impls`
or `org.example.servlet`.

### The Android client application

The `org.example.android` project is an example project on how to develop an Android application that 
will be registered as an AIOLOS node. To build and run this project, you will need to use our Androsgi 
build tools that enable to run OSGi on top of Android. Check the 
[androsgi repository](https://github.com/aiolos-dev/androsgi) for more info.

### The AIOLOS tools project

The `tools` project contains all necessary scripts and configurations to run the application on the
AIOLOS cloud platform. 

It contains the following directories:

- `configs`

  This contains all configuration files required to configure AIOLOS related OSGi bundles, i.e. 
  the CloudManager and Repository. Three subdirectories are present: `local`, containing configuration
  files for local testing, and `openstack` for testing on an OpenStack cloud, and `ec2` to test on the
  Amazon EC2 public cloud.
  
  The Repository configuration file contains a comma-separated list of urls to all repositories should
  be offered by the Repository service, or thus repositories that contain bundles that one should be 
  able to start on the nodes at runtime. An url can be both a http url, or a (relative) file path. By
  default these are the local repositories containing the application bundles.
 
- `resources`

  The `resources` directory contains a copy of one of the `configs` maps that is currently used
  for running. The and build system will copy the correct configuration files when running
  `ant run`.
  
- `scripts` 

  The `scripts` directory contains a number of utility scripts to generate an OSGi repository and generate
  an index of the repository.
  
- `*.bndrun`

  The `tools` project also contains .bndrun files that are used to start up the nodes with AIOLOS.
  Three predefined .bndruns exist:
  
  - `run-mgmt.bndrun` : this is the first node started when using the `ant run` command. This
    is the so-called management node, that starts various management bundles such as the CloudManager,
    the PlatformManager and the Webconsole UI. You can add additional application specific bundles
    to this .bndrun files that you want to have running on this node. In the case of the example
    application, this one also contains the servlet and command bundles.
  - `run-vm.bndrun` : this .bndrun is used when you click the "New Node" button in the Webconsole UI.
    This contains the basic AIOLOS bundles, and some application specific bundles you want on each 
    Node. In the case of the example application, this one contains the implementations of the Greeting
    service.
  - `run-vm-empty.bndrun` : this .bndrun contains the minimum required AIOLOS bundles, without any
    application-specific bundles. This one is used when you click the "Empty Node" button in the Webconsole UI,
    and is used as basis for scaling components.
    
  Other custom .bndrun files can be provided, i.e. a file to run all components on one runtime to test
  the application without distribution of bundles.
  
 
The build system
----------------

AIOLOS uses Ant as its build system. The top-level project provides the following ant build targets:

- `clean`

  Will remove all build artifacts such as .class and .jar files from the projects.
  
- `build` 

  Will compile and build all projects.
  
- `junit`

  Will run all junit tests (if provided) in all projects.
  
- `run`
  
  Will start an AIOLOS management OSGi runtime. Use `-Dconfig=xxx` to specify which configuration to use. The default config is `local`
  
- `kill`

  Will kill all running VMs when a cloud config is used. Use `-Dconfig=xxx` to specify which configuration to use.
