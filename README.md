# board-example
Intellij + spring boot(2.5.8) + gradle + Thymeleaf + mysql 연동  
Java 11  

## dependencies
spring-web  
spring-security  
thymeleaf  
jpa  
lombok  
mysql  
mybatis  
=> 구현 끝난 뒤 시간 남으면 JPA 사용해서 해보기

---

## 진행상황
#### 2023-02-03
- xss 처리  
- 화면 출력 속도 개선  
- select * 말고 컬럼명으로 바꾸기  
- try-catch 사용해서 예외처리  
- 회원가입 아이디/비번 정규식 처리  
- ModelAndView를 통한 데이터 세팅한 뷰 넘기기  

#### 2023-02-07 ~ 2023-02-08
- 게시글 파일 첨부 기능 (https://congsong.tistory.com/39)  
  -> 게시글 삭제 시, 파일도 삭제 처리  
  -> 파일 다운로드 기능 구축  

#### 2023-02-09
- 관리자 페이지 구축
  -> 게시판 비활성화 시, 게시글도 비활성화 처리  
  -> 관리자 페이지 권한 체크 부분 보완

#### 2023-02-10
- xss 필터 사용해서 들어오는 모든 요청 처리하도록 재구축  
- 관리자 페이지 내 회원 관리 기능 추가  
- 게시판 별 회원 권한 부여 기능  

#### 2023-02-13
- 게시판 타입에 따라 뷰단 다르게 보이기(갤러리 타입 / 질문&답변 타입)  
- 매니저 권한은 게시글 수정 및 삭제 가능

#### 2023-02-14
- 관리자 공지사항 작성 기능 및 공지사항 게시글 상단 출력  
- 목록 버튼을 통해 페이지 정보 유지(페이징, 게시판 타입, 검색 값)

#### 2023-02-15
- 갤러리 게시판  
  -> 게시글 목록에서도 사진이 보이도록 처리  
  -> 갤러리 게시판은 사진 첨부 필수 처리  
- 전체 게시판 삭제  
- 게시판을 선택 후, 게시글 등록하는 형식으로 변경  
- 게시판 매니저도 공지사항 작성 가능하도록 변경  
- 게시판 번호 auto_increment PK 출력 -> 내림차순 순번 출력으로 변경  
- mysql select 속도 개선(index 지정) 4s -> 0.5s  

---
## 부가기능  
- ~~게시글 검색~~  
- 게시글 정렬  
- 답변 개수 출력  
