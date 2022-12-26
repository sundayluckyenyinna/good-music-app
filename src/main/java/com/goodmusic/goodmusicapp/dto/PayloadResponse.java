package com.goodmusic.goodmusicapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayloadResponse implements Response
{
    private String responseCode;
    private String responseMessage;
    private Object data;
}
