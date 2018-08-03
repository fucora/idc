FROM openjdk:8
ARG profile
ENV TZ=Asia/Shanghai
RUN mkdir /app
WORKDIR /app
ADD target/idc-admin-web.zip /app/
ADD hosts-${profile} /app/
RUN cat hosts-${profile} >> /etc/hosts
RUN unzip /app/idc-admin-web.zip && rm -f /app/idc-admin-web.zip
CMD cd /app/idc-admin-web && cat /app/hosts-${profile} >> /etc/hosts && java -cp "./conf:lib/*" com.iwellmass.idc.IDCApplication  --spring.profiles.active=${profile}
