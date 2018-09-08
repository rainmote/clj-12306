FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/clj-12306.jar /clj-12306/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clj-12306/app.jar"]
