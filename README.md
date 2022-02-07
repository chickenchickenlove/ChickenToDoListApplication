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


# 22.02.02
- @Aspect를 이용한 사용 시간 추적기 구현 및 적용 완료
- MemberController Validation 기능 추가(비밀번호 반복 Validation 기능 추가) 
- MemberController Validation 기능 추가(중복 E-Mail, 중복 닉네임, 중복 ID 가입 불가능 기능 개발)
- Spring Interceptor 이용한 로그인 인터셉터 기능 개발(사용 로그인 세션은 JSessionId) 
- MemberArticleController 기능 구현(ToDo 리스트 작성 기능 추가 및 저장, ToDo 리스트 전체 조회 기능 추가, ToDoList 기능 자세한 내용 확인 기능 개발, TodoList 수정 기능 개발 구현)
- String → LocalDate 타입 컨버터 구현하여 ThymeLeaf String 값을 LocalDate로 변경하여 저장 가능 기능 구현
- MemberArticle의 Compartor 구현하여, ToDoList 긴급 순서대로 정렬하는 기능 추가 개발


# 22.02.03
- BootStrap 적용한 ThymeLeaf의 View Template 전체 변경 실행 (V2 Version으로 변경 및 V1 삭제)
- To Do List 공유 기능 구현(회원 기능 검색 + Validation + MemberArticle Entity 생성으로 구현)
- LocalDateTime Type Converter 기능 개발 및 ThymeLeaf View Template 적용
- HttpServletRequest Session을 이용해, 현재 로그인 정보 제공하여 Thymeleaft View Template에 렌더링 가능하도록 구현.
- To Do List EditForm Validation 로직 개발
- MemberArticleController 추가 개발(ToDoList 삭제, ToDoList 완료 처리, ToDoList 공유처리)
- HashMap을 이용한 회원 가입 동시성 문제 개선위한 Validation 테스트 코드 작성. 
- (정리) 타임리프 상대경로 / 절대경로 내용 정리

# 22.02.04
- 회원 ID, 별명 HashMap을 동시성 해결 코드 문제 확인 → 삭제 처리
- 회원 ID, 별명 Unique Value 적용 및 동시성 해결 코드 위에 JPA 낙관적 Lock 설정(@Version)

# 22.02.05
- 회원, 게시글 테이블 수동 createdTime, UpdatedTime 삭제 → Base Entity 도입 + @PrePersist, @PreUpdate 기능 도입으로 자동화
- 테스트 데이터 추가(회원 데이터 및, 게시글 데이터 대용량 추가)


# 22.02.06
- 게시글 수정, 조회, 공유 기능에 각 컨트롤러에서 보안 확인해서 처리.
→ 이 때, 동일한 로직이 너무 많이 반복되는 것이 확인됨. 게시글 - 회원의 보안 처리 기능을 AOP로 빼서 공통 처리할 수 있는지 검토 중. 

# 22.02.07
- @MySecurity 어노테이션 개발 + ArticleControllerAop 클래스 개발
- MemberArticleController → MemberArticleNoSecurityController / MemberArticleSecurityController로 분리 (복잡성 감소)
- MemberArticleSecurityController로 @MySecurity 기반으로 ArticleControllerAop 보안 적용