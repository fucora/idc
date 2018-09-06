FROM openjdk:8
ARG profile
ENV TZ=Asia/Shanghai
RUN mkdir /app
WORKDIR /app
ADD target/idc-app.zip /app/
ADD hosts-${profile} /app/
RUN cat hosts-${profile} >> /etc/hosts
RUN unzip /app/idc-app.zip && rm -f /app/idc-app.zip
CMD cd /app/idc-app && cat /app/hosts-${profile} >> /etc/hosts && java -cp "./conf:lib/*" com.iwellmass.idc.IDCApplication  --spring.profiles.active=${profile}
