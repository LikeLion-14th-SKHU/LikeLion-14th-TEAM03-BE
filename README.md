# Skin Care BE

Spring Boot 4.0.7 (Java 21) | AWS EC2 | Docker | GitHub Actions Blue-Green

## 1. 기술 스택
- Spring Boot 4.0.7 (Java 21)
- AWS EC2 + Docker + RDS MySQL 8.0
- GitHub Actions CI/CD (Blue-Green 무중단 배포)
- Swagger (springdoc-openapi 2.8.9)
- 세션 기반 인증 (spring-session-jdbc, 로그인 없는 UUID 세션 쿠키)

## 2. 패키지 구조
```
com.skincare
├── config            (JacksonConfig, SwaggerConfig)
├── common
│   ├── response       (ApiResponse)
│   └── exception       (GlobalExceptionHandler, CustomException, ErrorCode)
├── session      (controller/dto/entity/service/repository - 비어있음, 추후 작업)
├── onboarding   (controller/dto/entity/service/repository - 비어있음, 추후 작업)
├── skin         (controller/dto/entity/service/repository - 비어있음, 추후 작업)
├── card         (controller/dto/entity/service/repository - 비어있음, 추후 작업)
├── todo         (controller/dto/entity/service/repository - 비어있음, 추후 작업)
├── notification (entity/service/repository - 비어있음, 추후 작업)
├── plan         (controller/dto/entity/service/repository - 비어있음, 추후 작업)
└── mypage       (controller/dto/service - 비어있음, 추후 작업)
```
각 도메인 폴더는 `.gitkeep`만 있는 빈 폴더입니다. `develop` 브랜치 머지 후 `feature/*` 브랜치에서 도메인별로 채워주시면 됩니다.

## 3. 로컬(IntelliJ) 실행 방법
1. 이 프로젝트 압축을 풀고 IntelliJ에서 `Open` → build.gradle 인식 후 자동 Gradle sync
2. 우측 상단 `SkincareApplication` → **Edit Configurations**
3. Active profiles: `local`
4. Environment variables:
   `DB_HOST=<RDS 엔드포인트 또는 localhost>;DB_USERNAME=admin;DB_PASSWORD=실제비밀번호`
5. 최초 실행 전 로컬/원격 MySQL에 `skincare` 데이터베이스가 존재해야 합니다.
   ```sql
   CREATE DATABASE skincare;
   ```
6. 실행 후 Swagger 확인: `http://localhost:8080/swagger-ui.html`

## 4. AWS 인프라 설정 (관리자가 1회 진행)
WineLab과 동일한 구조로, 아래 항목만 새 프로젝트 값으로 바꿔서 진행하면 됩니다.

### 4-1. EC2
- Ubuntu 22.04 LTS, t2.micro, 20GB, 키페어 `skincare-key.pem`
- 보안그룹 포트: 22(SSH), 80(HTTP), 443(HTTPS), 8081(Blue), 8082(Green)
- Docker, Docker Compose, Nginx 설치 (WineLab 매뉴얼 3-4 절차 동일)

### 4-2. RDS MySQL
- 엔진 MySQL 8.0, 프리티어, 식별자 `skincare-db`, 마스터 사용자 `admin`
- EC2와 동일 VPC, 퍼블릭 액세스 아니요
- 생성 후 EC2에서 접속하여 `CREATE DATABASE skincare;` 실행
- 엔드포인트를 이후 GitHub Secret `DB_HOST` 에 등록

### 4-3. GitHub Secrets 등록 (Settings → Secrets and variables → Actions)
| Secret 이름 | 값 |
|---|---|
| DOCKER_USERNAME | Docker Hub 아이디 |
| DOCKER_TOKEN | Docker Hub Access Token |
| EC2_HOST | EC2 퍼블릭 IP |
| EC2_SSH_KEY | skincare-key.pem 전체 내용 |
| DB_HOST | RDS 엔드포인트 |
| DB_USERNAME | RDS 마스터 사용자 (admin) |
| DB_PASSWORD | RDS 마스터 비밀번호 |

### 4-4. EC2에 배포 파일 배치
`deploy/` 폴더(레포에는 포함되지만 Docker 이미지 빌드에는 사용되지 않음, 서버에 직접 올리는 운영 파일)를 EC2로 옮깁니다.
```bash
mkdir -p /home/ubuntu/app
# deploy/docker-compose.yml, deploy/deploy.sh 를 /home/ubuntu/app 으로 복사
sudo cp deploy/skincare.conf /etc/nginx/conf.d/skincare.conf
chmod +x /home/ubuntu/app/deploy.sh
sudo visudo   # 맨 아래 추가: ubuntu ALL=(ALL) NOPASSWD: /usr/sbin/nginx
```

### 4-5. DuckDNS + HTTPS
- `skincare.duckdns.org` (또는 원하는 서브도메인)로 DuckDNS 등록 후 EC2 퍼블릭 IP 연결
- `deploy/skincare.conf` 의 `server_name` 을 실제 도메인으로 수정
- `sudo certbot --nginx -d skincare.duckdns.org`

## 5. 배포 흐름
1. `main` 브랜치에 push
2. GitHub Actions 자동 실행 (JDK 21 → Gradle bootJar 빌드)
3. Docker 이미지 빌드 후 Docker Hub push
4. EC2 SSH 접속 → `deploy.sh` 실행
5. 현재 실행 중인 컨테이너 확인 (Blue/Green) → 반대 컨테이너 기동
6. `/actuator/health` UP 확인 (10회 재시도)
7. Nginx upstream 포트 전환 후 reload → 이전 컨테이너 종료

## 6. 브랜치 전략
| 브랜치 | 용도 |
|---|---|
| main | 배포용 (직접 push 금지, push 시 자동 배포) |
| develop | 개발 통합 브랜치 |
| feature/기능명 | 각자 기능 개발 |
