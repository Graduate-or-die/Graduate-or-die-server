FROM eclipse-temurin:21-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        fontconfig \
        fonts-noto-cjk \
        texlive-lang-korean \
        texlive-xetex \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY build/libs/*.jar /app/app.jar
EXPOSE 8080
ENV LATEX_ENABLE_INSTALLER=false
ENTRYPOINT ["java","-jar","/app/app.jar"]
