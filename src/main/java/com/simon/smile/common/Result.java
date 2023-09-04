package com.simon.smile.common;


public class Result {
    private boolean flag;
    private Integer code;
    private String message;
    private Object data;

    public Result() {
    }

    public static Result success() {
        return new Result().setFlag(true);
    }

    public static Result fail() {
        return new Result().setFlag(false);
    }

    public boolean isFlag() {
        return flag;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Result setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "flag=" + flag +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}