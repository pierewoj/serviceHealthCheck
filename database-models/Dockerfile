FROM maven:3.6.3-jdk-8

RUN mkdir /app
WORKDIR /app 

COPY ./src/ ./  

RUN mvn clean package
