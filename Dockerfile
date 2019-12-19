FROM java:8

EXPOSE 8080

COPY target/Manage-be-1.0-SNAPSHOT.jar /Manage-be.jar

CMD ["java", "-jar", "Manage-be.jar"]
