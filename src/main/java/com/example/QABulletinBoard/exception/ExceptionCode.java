package com.example.QABulletinBoard.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"MEMBER NOT FOUND"),
    MEMBER_EXISTS(404,"MEMBER EXISTS"),
    CANNOT_REGISTRATION_QUESTION(404, "MANAGER CANNOT REGISTRATION QUESTION"),
    QUESTION_NOT_FOUND(404, "CANNOT FIND QUESTION"), // 해당 질문을 찾을 수 없음
    MEMBER_NOT_MATCHED(404, "THIS MEMBER CANNOT UPDATE QUESTION"), // 게시글 작성한 회원과 수정하려는 회원이 다른 경우
    QUESTION_DELETED(404, "THIS QUESTION HAS ALREADY BEEN DELETED"), // 삭제된 질문 게시글임을 아림
    ACCESS_DENIED(404, "THIS MEMBER CANNOT ACCESS QUESTION"); // 회원이 게시글에 접근할 수 없는 경우

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
