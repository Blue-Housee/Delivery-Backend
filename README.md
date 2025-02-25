# 프로젝트 이름
Blue House 배달 서비스

# 프로젝트 소개
Blue House 배달 서비스는 코드의 가독성과 유지보수성을 높이고, 안정적인 서비스 운영을 목표로 한 배달 시스템입니다.
저희 팀은 효율적인 개발 프로세스를 위해 일관된 코드 스타일, 철저한 예외 처리, 코드 최적화에 집중하였습니다.

🎯 기술적 목표

✔ 일관된 코드 스타일 유지 ➝ 팀원 간의 협업을 원활하게 하고 유지보수성을 높이기 위해 코드 스타일을 통일하고 Best Practice를 적용했습니다.

✔ 철저한 예외 처리 ➝ 예상치 못한 오류로 인한 서비스 중단을 방지하기 위해 전역 예외 처리를 적용하고, 사용자에게 의미 있는 에러 메시지를 반환하도록 개선했습니다.

✔ 코드 간소화 및 최적화 ➝ 중복된 로직을 제거하고 공통 모듈화를 진행하여 코드 복잡도를 줄이고, 성능을 개선했습니다.

✔ 안정적인 인증 및 권한 관리 ➝ Spring Security와 JWT를 활용하여 안전한 사용자 인증 및 접근 제어를 구현했습니다.


# 개발 환경 소개
| 분류 | 상세 |
| ------------ | ------------- |
| IDE | IntelliJ |
| Language | Java 17 |
| Framework | Spring Boot 3.4.2 |
| Repository | PostgreSQL 17.2, H2 In-memeory |
| Build Tool | Gradle 8.8 |
| DevOps - dev |  Docker, Docker-compose, GitHub Actions, AWS EC2, NGINX |

# 프로젝트 실행 방법

<details>
<summary>프로젝트 실행 방법</summary>

## 1. 필수 환경 설정

### 1.1 필수 설치 프로그램

- **JDK 17** 이상
- **PostgreSQL 15** 이상
- **Gradle** (프로젝트 내 포함)
- **IntelliJ IDEA**
### 1.2 환경 변수 설정

`.env.properties` 파일을 프로젝트 루트 디렉토리에 생성한 후, 아래 정보를 입력합니다.

> ⚠️ **주의:** `.env.properties` 파일에는 보안이 필요한 정보(비밀번호, API 키 등)가 포함되므로, `.gitignore`에 추가하여 Git에 커밋되지 않도록 합니다.

이 파일은 `application.properties`에서 `@PropertySource("classpath:.env.properties")` 등의 설정을 통해 로드됩니다.

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/{DB_NAME}
SPRING_DATASOURCE_USERNAME={DB_USERNAME}
SPRING_DATASOURCE_PASSWORD={DB_PASSWORD}
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect

JWT_SECRET_KEY={JWT_SECRET_KEY}

ADMIN_TOKEN={ADMIN_TOKEN}

# Gemini
gemini.api.url={GEMINI_URL}
gemini.api.key={GEMINI_KEY}
```

(해당 설정은 `application.properties`에서 로드됩니다.)

## 2. 프로젝트 빌드 및 실행

### 2.1 Git 저장소 클론

```sh
git clone https://github.com/Blue-Housee/Delivery-Backend.git
cd Delivery-Backend
```

### 2.2 데이터베이스 설정

PostgreSQL에서 새로운 데이터베이스를 생성합니다.

```sql
CREATE DATABASE mydb;
```

### 2.3 프로젝트 실행

터미널에서 아래 명령어를 실행합니다.

```sh
./gradlew bootRun   # Gradle 사용 시
```

**( `docker-compose` 설정이 필요할 수도 있으므로, 관련 내용은 이후 추가될 수 있습니다.)**

## 3. API 테스트 방법

- **Swagger UI** 제공: `http://localhost:8080/swagger-ui/index.html`
</details>


# 프로젝트 세부 사항
[Wiki 문서](https://github.com/Blue-Housee/Delivery-Backend/wiki)에서 확인하세요!
