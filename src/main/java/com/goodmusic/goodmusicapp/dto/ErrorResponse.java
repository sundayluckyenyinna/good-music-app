package com.goodmusic.goodmusicapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse implements Response
{
    private String responseCode;
    private String responseMessage;
}
