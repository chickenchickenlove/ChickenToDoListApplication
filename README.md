# ChickenToDoListApplication
- 개인 프로젝트
- 본인의 ToDo List를 작성하고, 오늘 무엇을 해야할지 알려주는 프로젝트입니다.
- 본인의 ToDo List를 다른 사람과 공유해서 일정을 나누는 것도 가능합니다.  


# ChickenToDoListApplication
- ThymeLeaf
- Lombok
- Spring Web
- Validation
- Spring Data JPA
- H2 Database
- QueryDSL
- p6spy


# 22.01.31
- Member - Article Table 관계 개발(Many To Many → 중간 테이블 Member Article Table 생성)
. 회원이 본인의 ToDoList만 보는 기능이라면 두 테이블의 관계는 One To Many
. 회원 A가 원한다면 회원 B에게 자신의 To Do List를 공유할 수 있기 때문에 Member - Article은 Many To Many 관계로 설정
. 중간 테이블 Member Article Table 생성해서 회원끼리 To Do List 공유할 예정

- MemberRepository / MemberService 개발
. 회원 전체 조회 및 가입 기능 개발
. 특정 회원이 가지고 있는 전체 To Do List 조회 기능 개발
. ★★★★★ ToDo : 회원 가입 시, 중복 ID 가입 못하게 해야함 

- ArticleRepository / ArticleService 개발
. To Do List 저장 개발
. To Do List 수정 개발(Dirty Check 이용)

- Home Controller / Member Controller 개발
. Home Controller 개발
. Member Controller 개발 (회원 가입 GetMapping + PostMapping)으로 가입 가능하도록 설정. 


# 22.02.01
- @Aspect 구현
- 로그 추적기, 사용 시간 추적기, 조회수 추적기용 
- 
