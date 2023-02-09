# board-example
Intellij + spring boot + gradle + Thymeleaf + mysql 연동  

## dependencies
spring-web  
thymeleaf  
jpa  
lombok  
mysql  
mybatis  
=> 구현 끝난 뒤 시간 남으면 lombok 없이 해보기 & JPA 사용해서 해보기

## 진행상황
- ~~xss 처리~~  
- ~~화면 출력 속도 개선~~  
- ~~select * 말고 컬럼명으로 바꾸기~~  
- ~~trt-catch 사용해서 예외처리~~  
- ~~회원가입 아이디/비번 정규식 처리~~  
- ~~ModelAndView를 통한 데이터 세팅한 뷰 넘기기~~  
- ~~게시글 파일 첨부 기능 (https://congsong.tistory.com/39)~~  
  ~~-> 게시글 삭제 시, 파일도 삭제 처리 필요~~  
  ~~-> 파일 다운로드 기능 필요~~  
- 관리자 페이지 구축  
  -> 회원 관리 기능 추가  
  -> 게시판 마다 권한 부여 기능
- 게시판 타입에 따라 뷰단 다르게 보이기(갤러리 타입/질문답변 타입)
- xss 인터셉터->필터 사용해서 들어오는 모든 요청 처리하도록 재구축


[부가기능]  
- ~~게시글 검색~~  
- 게시글 정렬  
- 답변 개수 출력  
