#!/bin/bash

REPOSITORY="ideawolf/fete-be"
TAG="latest"
SCRIPT_DIR="/home/ubuntu/bin"  # 스크립트가 있는 디렉토리 경로

# 스크립트가 있는 디렉토리로 이동
cd $SCRIPT_DIR

# 현재 로컬에 저장된 이미지의 SHA256 해시값
LOCAL_IMAGE=$(docker images --no-trunc --quiet ${REPOSITORY}:${TAG})

# 최신 이미지를 가져옴
docker compose pull fete-be

# 최신 이미지의 SHA256 해시값
REMOTE_IMAGE=$(docker images --no-trunc --quiet ${REPOSITORY}:${TAG})

# 이미지를 비교하고 다르면 컨테이너를 업데이트
if [ "$LOCAL_IMAGE" != "$REMOTE_IMAGE" ]; then
    echo "업데이트된 이미지가 있습니다. 컨테이너를 재시작합니다."

    # 기존 컨테이너 중지 및 제거
    docker compose down

    # 새로운 컨테이너 실행
    docker compose up -d

    # 모든 이전 이미지 제거
    ALL_IMAGES=$(docker images --no-trunc --quiet ${REPOSITORY})
    for IMAGE in $ALL_IMAGES; do
        if [ "$IMAGE" != "$REMOTE_IMAGE" ]; then
            echo "Removing image: $IMAGE"
            docker rmi $IMAGE
        fi
    done

    echo "이미지 정리가 완료되었습니다."

else
    echo "이미지가 최신 상태입니다. 컨테이너를 재시작하지 않습니다."
fi
