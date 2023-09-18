package com.simon.smile.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result {
    private boolean flag;
    private String message;
    private Object data;

    public static Result success(String message) {
        return new Result().setFlag(true).setMessage(message);
    }

    public static Result fail(String message) {
        return new Result().setFlag(false).setMessage(message);
    }
}