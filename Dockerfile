# Stage 1 - Create build image
FROM gradle:5.0.0-jdk8-alpine AS builder
RUN java -version

COPY ["build.gradle", "settings.gradle", "/usr/src/ml-ippi/"]
COPY ["src/", "/usr/src/ml-ippi/src/"]
WORKDIR /usr/src/ml-ippi/

# RUN apk --no-cache add bash
USER root
RUN chown -R gradle /usr/src/ml-ippi/
USER gradle

RUN gradle assemble

# Stage 2 - Create downsized executable container
FROM gradle:5.0.0-jre8-alpine
WORKDIR /usr/projects/ml-ippi/
COPY --from=builder /usr/src/ml-ippi/build/libs/ml-ippi-1.0-all.jar .

ENTRYPOINT [ "java", "-cp", "./ml-ippi-1.0-all.jar", "com.jereaa.MainApplication" ]
