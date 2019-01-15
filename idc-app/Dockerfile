FROM openjdk:8
ARG profile
ENV TZ=Asia/Shanghai
RUN mkdir /app
WORKDIR /app
ADD target/idc-app.zip /app/
RUN unzip /app/idc-app.zip && rm -f /app/idc-app.zip
CMD cd /app/idc-app && java -cp "./conf:lib/*" com.iwellmass.idc.app.IDCApplication  --spring.profiles.active=${profile}
