[![Deploy SHACL-Play](https://github.com/sparna-git/shacl-play/actions/workflows/maven.yml/badge.svg)](https://github.com/sparna-git/shacl-play/actions/workflows/maven.yml)


# SHACL Play!
SHACL validator and printer **live at https://shacl-play.sparna.fr**

## Running the application.
### With Docker
The following `docker` commands should be sufficient to get you started.
First, build the image with:
```
docker build -t shacl-play:latest ./
```
Then, start the application on port `8080` with:
```
docker run -p 8080:8080 shacl-play:latest
```
Refer to [docker documentation](https://docs.docker.com) for advanced configuration.
