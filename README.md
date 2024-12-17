FETE
---
안녕하세요, FETE 백엔드 팀입니다.<br>
저희는 파티 및 페스티벌 행사에 대한 홍보 및 티켓 판매 서비스를 제공합니다.

</br>

App Store
---
10월 초 심사 과정에 들어가, 10월 27일 App Store에 출시되었습니다.</br>
현재는 베타 테스트 버전입니다. </br>
https://apps.apple.com/kr/app/fete/id6720755427

</br>

기술 스택
---
- Spring, Spring Boot, JPA, MySQL
- AWS - EC2, RDS, S3, ElastiCache(Redis)
- Docker, GitHub Actions

</br>

기능 리스트
---
- 회원가입, 로그인
  - JWT Token 방식
  - Spring Security, OAuth 사용 (카카오, 애플)
  - 이메일 인증 메일 발송 (ElastiCache(Redis) 사용)
  - 계정 종류 : 이메일, 카카오, 애플
- 포스터
  - 포스터 등록/수정/삭제
  - 포스터 관심 등록/해제
  - 포스터 다중 필터링 조회 (QueryDSL 사용)
- 티켓 결제
  - 티켓 구매 및 환불 기능
  - 티켓 구매 시, QR 코드 발급
  - 토스 페이먼츠 API를 사용하여 실제 결제 및 환불 시스템 구현
- 마이페이지
  - 구매한 티켓 조회
  - 회원 프로필 조회/수정
  - 회원 탈퇴
- 카테고리
  - 관리자용 : 카테고리 생성/수정/삭제
  - 일반 유저 : 카테고리 전체 조회
- 배너
  - 관리자용 : 배너 생성/수정/삭제
  - 일반 유저 : 배너 전체 조회
  - 배너를 클릭 시, 연결된 포스터로 이동
- 팝업
  - 관리자용 : 팝업 생성/수정/삭제
  - 일반 유저 : 팝업 조회/차단
- 푸시 알림
  - 전체 유저 푸시 알림
  - 이벤트별 푸시 알림

</br>

ERD
---
<img src="https://github.com/user-attachments/assets/4ee1d694-a905-41aa-9299-dfe537af1c15" alt="FETE_ERD" width="700"/>
