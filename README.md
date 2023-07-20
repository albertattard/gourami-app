# Gourami App

## Prerequisites

| Name   | Version  | Used to                                           |
| ------ | -------- | ------------------------------------------------- |
| Java   | `17.0.8` | build the project                                 |
| Docker | `24.0.4` | create the container image and push to repository |

## Useful commands

- Build the application

  ```shell
  $ ./gradle clean bootJar
  ```

- Set up the multi-platform builder

  ```shell
  $ docker buildx ls
  ```

  ```shell
  $ docker buildx create \
    --name multi-platform-builder \
    --driver docker-container \
    --bootstrap
  ```

  ```shell
  $ docker buildx use multi-platform-builder
  ```

- Run the local image

  ```shell
  $ docker run \
    --rm \
    --name gourami-app \
    --detach \
    --publish 8080:8080 \
    gourami-app
  ```

  ```shell
  $ curl http://localhost:8080/actuator/health | jq
  ```

  ```json
  {
    "status": "UP"
  }
  ```

- Build the image and load it locally

  ```shell
  $ docker build \
    --file=container/Dockerfile \
    --tag gourami-app \
    --load \
    .
  ```

- Build the image and push it to GitHub Container Registry

  Refer to:
  [Working with the Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry)

  ```shell
  $ export CR_PAT=<PAT>
  $ echo $CR_PAT | docker login ghcr.io -u albertattard@gmail.com   --password-stdin
  ```

  ```shell
  $ docker build \
    --file=container/Dockerfile \
    --platform linux/amd64,linux/arm64 \
    --tag ghcr.io/albertattard/gourami-app:latest \
    --push \
    .
  ```

- Run the image from GitHub Container Registry

  ```shell
  $ docker run \
    --rm \
    --name gourami-app \
    --detach \
    --publish 8080:8080 \
    ghcr.io/albertattard/gourami-app:latest
  ```

  ```shell
  $ curl http://localhost:8080/actuator/health | jq
  ```

  ```json
  {
    "status": "UP"
  }
  ```

- Stop the container

  ```shell
  $ docker stop gourami-app
  ```
