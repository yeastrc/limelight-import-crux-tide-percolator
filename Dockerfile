FROM amazoncorretto:11-alpine-jdk

COPY build/libs/cruxTide2LimelightXML.jar  /usr/local/bin/cruxTide2LimelightXML.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/cruxTide2LimelightXML.jar"]