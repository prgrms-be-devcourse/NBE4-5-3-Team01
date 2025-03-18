# NBE4-5-2-Team01
프로그래머스 백엔드 데브코스 4기 5회차 1팀 단단한 나뭇가지의 2차 팀 프로젝트입니다.

<br/>
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

<br/>

## 2. 프로젝트 소개
매일 들은 노래를 기록하고, 공유하고, 확인할 수 있는 음악 캘린더 플랫폼

<br/>

## 3. 주요 기능
- 회원 관리 👥
- 음악 검색, 기록 및 추천 🎵
- 음악 기록 분석을 통한 주간, 월간, 장르별, 가수별 통계 시각화 📊
- 이메일 알림 및 푸시 알림 🔔
- 팔로우 기능 및 친구 캘린더 조회 💘

<br/>

## 4. 작업 및 역할 분담
|이름|역할|
|:---:|:---|
|조정인|<ul><li>팀 리딩</li><li>음악 검색</li><li>음악 추천</li></ul>|
|강웅빈|<ul><li>팔로우</li><li>유저 검색</li><li>팔로잉, 팔로워 목록 조회</li></ul>|
|김지수|<ul><li>캘린더 조회</li><li>캘린더 생성</li><li>음악 기록 및 기록 수정</li></ul>|
|신우석|<ul><li>소셜 로그인</li><li>회원 관리</li></ul>|
|엄현수|<ul><li>이메일 알림 및 푸시 알림</li><li>알림 읽음 처리</li><li>받은 알림 목록 조회</li><li>음악 기록 통계 시각화</li></ul>|

<br/>
<br/>

# 🛠️ Tech
## 프로젝트 설정 및 실행
### 리포지토리 클론  
```bash
git clone https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01.git
cd NBE4-5-2-Team01
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

## 기술 스택
### 언어
- JAVA   23
- TypeScript

### 프레임워크 및 라이브러리
- Spring   3.4.2
- Spring  Security
- React   19.0.0
- Next.js   15.1.7
- ShadCN/UI
  
### IED 및 개발 도구
- IntelliJ IDEA
- Visual Studio Code

### 버전 관리 및 협업 도구
- Git
- GitHub
- Slack
- Notion
- Discord

## Usecase Diagram
![Usecase Diagram](https://github.com/user-attachments/assets/bb40ca6e-387e-4ec0-a72c-f0251057f4b2)

## ERD
![ERD](https://github.com/user-attachments/assets/c113aef1-aa1c-43d6-a7ce-3e0bb696d01c)

## System Architecture
![System Architecture](https://github.com/user-attachments/assets/117881b9-3f0e-49c9-b4a7-1b0c94fed91b)

## Flow Chart
[🗃️ Flow Chart](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%97%83%EF%B8%8F-Flow-Chart)

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

## API 명세서
[📝 API 명세서](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%9D-API-%EB%AA%85%EC%84%B8%EC%84%9C)
<br/>
<br/>

## 컨벤션
[🎯 Commit Convention](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%8C-Git-Commit-Message-Convention)
<br/>
[📌 Code Convention](https://github.com/prgrms-be-devcourse/NBE4-5-2-Team01/wiki/%F0%9F%93%8C-Code-Convention)
