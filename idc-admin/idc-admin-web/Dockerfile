FROM openjdk:8
ARG profile
ENV TZ=Asia/Shanghai
RUN mkdir /app
WORKDIR /app
ADD target/data-factory.zip /app/
ADD hosts-${profile} /app/
RUN cat hosts-${profile} >> /etc/hosts
RUN unzip /app/data-factory.zip && rm -f /app/data-factory.zip
CMD cd /app/data-factory && cat /app/hosts-${profile} >> /etc/hosts && java -cp "./conf:lib/*" com.iwellmass.datafactory.DataFactoryApplication --spring.profiles.active=${profile}
