# kakaopay 뿌리기 RESTful API

# Development Environment
- JDK 14.0.1
- Java Servlet
- Mysql, Postman
- DB_CREATE_SQL
  - /src/mysql-db-table.sql
  
# 핵심 문제 해결 전략
  - 요구 및 제약사항 분석 -> Database 설계 -> 간소화된 RESTful API 구현 -> 제약 조건 테스트
- 공통
  - Base URL:http://localhost:8080/api
  - HTTP Header X-USER-ID ( 숫자 ), X-ROOM-ID ( 문자 )
- 뿌리기 API  
  - 토큰 랜덤 생성 후 DB 값과 비교하여 중복체크, 요청 항목 유효성검사, 뿌리기 금액&인원에 따른 DB 데이터 생성   
```bash
- Call: POST /spread
- Body
  - request_money: 뿌리기 금액
  - person_count: 뿌리기 받을 인원
- ResponseJSON
{
  "status": "success",
  "token": "CNv"
}
- 금액 체크
{
  "status": "error",
  "message": "뿌리기 금액을 확인해주세요. 최소금액은 1,000 원 입니다."
}    
- 인원 체크
{
  "status": "error",
  "message": "뿌리기 인원을 확인해주세요."
}
```
    
- 받기 API
  - 토큰값 유효성 검사, 유효시간 확인, X-USER-ID, X-ROOM-ID 에 따른 조건 체크, 받기 후 사용처리
```bash
- Call: POST /get
- Body
  - token: 뿌리기시 발급된 token
- ResponseJSON
{
  "status": "success",
  "response_money": "1250"
}
- 유효시간 만료 응답
{
  "status": "error",
  "message": "뿌리기 시간이 만료되었습니다."
}
- 본인이 뿌리기 한 금액 받기 응답
{
  "status": "error",
  "message": "본인이 뿌린건 받을 수 없습니다."
}
- 이미 받았거나 소진된 뿌리기에 대한 응답
{
  "status": "error",
  "message": "뿌리기가 모두 소진되었습니다."
}
```

- 조회 API
  - 토큰값 유효성 검사, 유효시간 확인, X-USER-ID, X-ROOM-ID 에 따른 조건 체크, DB ArrayList to JSON 변환
 ```bash
 - Call: POST /look
 - Body
   - token: 뿌리기시 발급된 token
 - ResponseJSON
{
  "status": "success",
  "created_at": "2020-06-27 04:45:07",
  "bburigi_money": "5000",
  "total_response_money": "1250",
  "response_list": [
      {
        "response_money": "1250",
        "response_user": "4",
      },
      {
        "response_money": "1250",
        "response_user": "5",
      },
  ]
}
- 유효기간이 만료되었거나 조회되지 않는 토큰에 대한 응답
{
  "status": "error",
  "message": "조회 기간이 만료되었습니다."
}
- 다른 사람의 뿌리기나 유효하지 않는 token 에 대한 응답
{
  "status": "error",
  "message": "요청 토큰에 대한 조회를 실패했습니다."
}
 ```

