package com.goodmusic.goodmusicapp.constants;

public enum ResponseCodes
{

    SUCCESS("00"),
    BAD_REQUEST("01"),
    FAILED_MODEL("02"),
    RECORD_ALREADY_EXIST("03"),
    SERVER_ERROR("99"),
    THIRD_PARTY_ERROR("91");

    private String code;
    ResponseCodes(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}
