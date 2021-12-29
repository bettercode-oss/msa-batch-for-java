# MSA Batch For Java

## Docker를 이용한 실행방법

프로젝트 루트에서 다음 순서로 명령어를 입력합니다.

```shell
./mvnw spring-boot:build-image
```

빌드가 되면

```shell
docker-compose up -d
```

을 입력합니다.

## 기술스택

- Java8
- Spring Boot 2.5
  - Spring Batch
- Maven
- MySQL 5.7
- MyBatis 3.5
- Lombok

## 예제 프로젝트

DB 하나에서 데이터를 Read(Extract)하고, 해당 데이터를 Process(Transform) 한 다음, 다른 테이블에 Write(Load)하는 배치 애플리케이션입니다.

1. contents 내용 trim후 해당 내용 저장
2. 요약 생성 후 저장

### Extract

DB(1)에서 데이터 추출(ID/날짜/시간/제목/내용)

### Transform

ID/날짜+시간/제목+ 빈 줄 +내용(500자)

### Load

DB(1)에서 변경된 데이터 저장

### 보완할 점

중복 작업을 방지하기 위한 방법의 추가가 필요합니다.
