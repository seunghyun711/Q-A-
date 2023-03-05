package com.example.QABulletinBoard.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"MEMBER NOT FOUND"),
    MEMBER_EXIXTS(404,"MEMBER EXISTS");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
