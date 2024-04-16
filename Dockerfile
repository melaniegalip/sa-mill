FROM sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_3.3.3
RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0 openjfx libgl1-mesa-dri libgl1-mesa-dev libcanberra-gtk-module libcanberra-gtk3-module
EXPOSE 8080
WORKDIR /sa-mill
ADD . /sa-mill
CMD sbt run
