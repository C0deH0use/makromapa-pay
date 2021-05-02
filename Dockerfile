FROM openjdk:16-oraclelinux8

ARG commit_short_sha

ENV CI_COMMIT_SHA=${commit_short_sha}

COPY build/libs/makromapa-pay.jar /

EXPOSE 9090

ENTRYPOINT [ "java", "-jar", "/makromapa-pay.jar" ]
