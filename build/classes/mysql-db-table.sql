CREATE SCHEMA `kakaopay` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `kakaopay`;

DROP TABLE `bburigi`;
CREATE TABLE `bburigi` (
	bburigi_id int not null AUTO_INCREMENT,
    request_user_id int  not null  COMMENT '뿌리기요청 사용자 식별값', 
    response_user_id int null      COMMENT '뿌리기 받은 사용자 식별값', 
    token char(3) not null                  COMMENT '토큰값', 
    request_room_id varchar(255) not null   COMMENT '대화방 식별값',
    request_money int not null              COMMENT '뿌린 금액',
    response_money int null                 COMMENT '받은 금액',
    allocate_status boolean null            COMMENT '할당 상태 체크',
    created_at datetime not null            COMMENT '뿌린 시각', 
    updated_at datetime null                COMMENT '받은 시각',
    PRIMARY KEY (bburigi_id)
)