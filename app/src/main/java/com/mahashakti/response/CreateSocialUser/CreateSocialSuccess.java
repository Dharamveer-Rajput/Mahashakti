package com.mahashakti.response.CreateSocialUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dharamveer on 15/2/18.
 */

public class CreateSocialSuccess {

    @SerializedName("Success")
    @Expose
    private Boolean success;
    @SerializedName("Error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("payload")
    @Expose
    private CreatePayloadSocial payload;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreatePayloadSocial getPayload() {
        return payload;
    }

    public void setPayload(CreatePayloadSocial payload) {
        this.payload = payload;
    }

}