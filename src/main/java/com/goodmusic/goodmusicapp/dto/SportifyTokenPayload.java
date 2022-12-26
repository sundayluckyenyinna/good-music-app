package com.goodmusic.goodmusicapp.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class SportifyTokenPayload
{
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("scope")
    private String scope;

    @SerializedName("expires_in")
    private String expiresIn;
}
