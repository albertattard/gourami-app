# Gourami App

## Prerequisites

| Name   | Version  | Used to                                           |
| ------ | -------- | ------------------------------------------------- |
| Java   | `21`     | build the project                                 |
| Docker | `24.0.4` | create the container image and push to repository |

## Useful commands

- Build the application

  ```shell
  $ ./mvnw clean package
  ```

- Set up the multi-platform builder

  ```shell
  $ docker buildx ls
  ```

  This is based on an
  [open issue](https://github.com/abiosoft/colima/issues/764) in colima.

  ```shell
  $ docker buildx create \
    --name multi-platform-builder \
    --driver-opt 'image=moby/buildkit:v0.12.1-rootless' \
    --bootstrap \
    --use
  ```

  ```shell
  $ docker buildx use multi-platform-builder
  ```

- Build the image and load it locally

  ```shell
  $ docker build \
    --file=container/Dockerfile \
    --tag gourami-app:local \
    --load \
    .
  ```

- Run the local image

  ```shell
  $ docker run \
    --rm \
    --detach \
    --name gourami-app \
    --publish 8080:8080 \
    gourami-app:local
  ```

  ```shell
  $ curl http://localhost:8080/api/public/actuator/health | jq
  ```

  ```json
  {
    "status": "UP"
  }
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
