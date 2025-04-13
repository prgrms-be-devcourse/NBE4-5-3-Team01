# NBE4-5-3-Team01
프로그래머스 백엔드 데브코스 4기 5회차 1팀 단단한 나뭇가지의 3차 팀 프로젝트입니다.

<br/>

## 단단한 나뭇가지

|<img height="150" style="width: auto;" alt="f3194686510bf918" src="https://github.com/user-attachments/assets/3e0031c5-95f6-4441-a18b-8878b4b357df" />|<img src="https://github.com/user-attachments/assets/1dfa0b9a-c333-40f5-9a7d-24581852465d" height="150" style="width: auto;">|<img src="https://github.com/user-attachments/assets/b446944e-9778-4beb-9733-b721df9278ef" height="150" style="width: auto;">|<img alt="f3194686510bf918" src="https://github.com/user-attachments/assets/0c0ec2fd-865b-456a-b9d5-ab110043ef51" height="150" style="width: auto;" />|<img height="150" style="width: auto;" alt="f3194686510bf918" src="https://github.com/user-attachments/assets/357b8f26-e3cb-4a35-b0ee-eff9da86a967" />|
|:--:|:--:|:--:|:--:|:--:|
|조정인|강웅빈|김지수|신우석|엄현수|
|팀장|팀원|팀원|팀원|팀원|
|[@jxxngin](https://github.com/jxxngin)|[@Woongbin06](https://github.com/Woongbin06)|[@j1suk1m](https://github.com/j1suk1m)|[@shinwoos](https://github.com/shinwoos)|[@sameom1048](https://github.com/sameom1048)|

<br/>
<br/>

# 📆 Project Overview

## 1. 프로젝트 명
### 📆 Music Calendar
<img src="https://github.com/user-attachments/assets/21bccd50-1de3-4739-9057-0636dd9a1aca" width="85%" height="85%"/>

<br/>
<br/>

## 2. 프로젝트 소개
매일 감상한 노래를 기록하고, 공유하며, 재생할 수 있는 음악 캘린더 플랫폼

<br/>

## 3. 주요 기능
- 회원 관리 및 멤버십 구독 👥
- 음악 검색, 기록, 추천 및 재생 🎵
- 음악 기록 분석을 통한 주간, 월간, 장르별, 가수별 통계 시각화 📊
- 이메일 알림 및 푸시 알림 🔔
- 팔로우 및 팔로잉한 회원의 캘린더 조회 💘

<br/>

## 4. 작업 및 역할 분담
|이름|역할|
|:---:|:---|
|조정인|<ul><li>팀 리딩</li><li>음악 검색</li><li>음악 추천</li><li>멤버십 구독 및 결제</li><li>스포티파이 플레이리스트 연동</li><li>관리자 페이지</li></ul>|
|강웅빈|<ul><li>팔로우 요청</li><li>팔로우 수락 및 거절</li><li>유저 검색</li><li>팔로잉, 팔로워 목록 조회</li><li>팔로잉 수, 팔로워 수 조회</li></ul>|
|김지수|<ul><li>캘린더 조회</li><li>캘린더 생성</li><li>음악 기록 및 기록 수정</li><li>캘린더 공개 설정</li><li>캘린더 접근 제한</li></ul>|
|신우석|<ul><li>자체 로그인</li><li>스포티파이 계정 연동</li><li>소셜 로그인</li><li>회원 관리</li></ul>|
|엄현수|<ul><li>이메일 알림 및 푸시 알림</li><li>알림 읽음 처리</li><li>받은 알림 목록 조회</li><li>음악 기록 통계 시각화</li><li>음악 재생</li><li>스포티파이 최근 재생 기록 연동</li></ul>|

<br/>
<br/>

# 🛠️ Tech
## 프로젝트 설정 및 실행
### 리포지토리 클론  
```bash
git clone https://github.com/prgrms-be-devcourse/NBE4-5-3-Team01.git
cd NBE4-5-3-Team01
```

### 프론트엔드 설정 및 실행
```bash
cd frontend
npm install
npm run dev
```

### 백엔드 설정
`application.yml` 파일에 맞게 환경 변수 설정

### 푸시 알림 허용
`chrome://settings/content/siteDetails?site=http://localhost:3000/`에서 알림 허용

<br/>

## 기술 스택
### 언어
<img src="https://img.shields.io/badge/java 21-007396?style=for-the-badge&logo=java&logoColor=white"><img src="https://img.shields.io/badge/kotlin 1.9.25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"><img src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=typescript&logoColor=white">

### 프레임워크 및 라이브러리
<img src="https://img.shields.io/badge/springboot 3.4.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white"><img src="https://img.shields.io/badge/Spring Data JPA-67C52A?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/Spring WebFlux-9FE870?style=for-the-badge&logo=springboot&logoColor=white"><br/><img src="https://img.shields.io/badge/Spring OAuth2-569A31?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/Spring Mail-6DA252?style=for-the-badge&logo=springboot&logoColor=white"><img src="https://img.shields.io/badge/next.js 15.2.4-000000?style=for-the-badge&logo=next.js&logoColor=white"><img src="https://img.shields.io/badge/react 19.0.0-61DAFB?style=for-the-badge&logo=react&logoColor=black"><img src="https://img.shields.io/badge/shadcn/ui-000000?style=for-the-badge&logo=shadcnui&logoColor=white">

### 데이터베이스
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><img src="https://img.shields.io/badge/h2-09476B?style=for-the-badge&logo=h2database&logoColor=white">

### IED 및 개발 도구
<img src="https://img.shields.io/badge/intellij idea-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
- Visual Studio Code

### 버전 관리 및 협업 도구
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"><img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"><img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"><img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">

<br/>

## Usecase Diagram
![Usecase Diagram](https://github.com/user-attachments/assets/bb40ca6e-387e-4ec0-a72c-f0251057f4b2)

<br/>

## ERD
![ERD](https://github.com/user-attachments/assets/974cfcbb-29f8-411e-be30-5ee22d49a5bb)

<br/>

## System Architecture
![System Architecture](https://github.com/user-attachments/assets/117881b9-3f0e-49c9-b4a7-1b0c94fed91b)

<br/>

## Flow Chart
[🗃️ Flow Chart](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%97%83%EF%B8%8F-Flow-Chart)

<br/>

## 브랜치 전략
**GitHub Flow** 전략 사용
- **Main Branch**
  - 배포 가능한 상태의 코드 유지
  - 모든 배포는 이 브랜치에서 수행
- **{name} Branch**
  - 각 팀원의 개발 브랜치
  - 모든 기능 개발은 이 브랜치에서 수행
- 테스트가 완료되면, ```main``` 브랜치를 타겟으로 Pull Request를 생성하여 Review를 요청
- Review가 완료되고, 피드백이 모두 반영되면 해당 ```feature``` 브랜치를 ```main```브랜치로 **Merge**

<br/>

## API 명세서
[📝 API 명세서](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%9D-API-%EB%AA%85%EC%84%B8%EC%84%9C)
<br/>
<br/>

## 컨벤션
[🎯 Commit Convention](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%8C-Git-Commit-Message-Convention)
<br/>
[📌 Code Convention](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%8C-Code-Convention)
