FROM ceregousa/dind:latest

COPY ./monitor-containers.sh /

ENTRYPOINT ["/bin/bash", "-c", "/monitor-containers.sh \"$@\"", "--"]