FROM openjdk:11
VOLUME /tmp
EXPOSE 8019
ADD ./target/fixedtransaction-0.0.1-SNAPSHOT.jar fixedtransaction.jar
ENTRYPOINT ["java","-jar","/fixedtransaction.jar"]