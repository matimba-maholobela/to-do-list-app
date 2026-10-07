FROM openjdk:17-jdk-alpine

#set working directory
WORKDIR /app

# copy jar file into the container
# *.jar is not is not found
COPY target/*.jar app.jar

#create impact group and impactUser
RUN addgroup -S impactGroup && adduser -S impactUser -G impactGroup

#grant permissions to the working dir
RUN chown -R impactUser:impactGroup /app

USER impactUser

EXPOSE 8080

#command to run the jar file
ENTRYPOINT ["java","-jar","app.jar"]