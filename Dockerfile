FROM java:8

COPY target/Manage-1.0-SNAPSHOT.jar /Manage.jar

EXPOSE 8080

CMD ["java", "-jar", "Manage.jar"]
