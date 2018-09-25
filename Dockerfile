FROM maven

WORKDIR /

RUN git clone https://github.com/waffleboot/microservices

WORKDIR /microservices

RUN mvn package

CMD mvn spring-boot:run

EXPOSE 8080