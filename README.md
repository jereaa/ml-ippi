# ML-IPPI

ML-IPPI (MercadoLibre - IP Private Investigator) is just a small tool to get information on any desired IP address.


## Installation

ML-IPPI is set up to run inside a Docker container, so you should go ahead and install [Docker](https://www.docker.com/get-started) if you haven't already.

If you want to run the tool directly without using Docker then you will need to install the Java JDK to compile and run the project.

Once we have Docker installed, we will execute the following from the project root folder:
```
docker build -t ippi .
```
This single command has built our Docker container with everything we need to execute our ML-IPPI tool.

## Usage

To use the tool we just have to run the following command:
```
docker run ippi [IP Address]
```
where `[IP Address]` is the IP address we want to get information on.

## Testing

Testing is only supported locally, not in the Docker container.

Once the JDK is installed, run the following command:
```
./gradlew clean test
```