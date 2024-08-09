package com.adele.common;

public class ApiResult<T> {
    private String code;
    private String message;
    private T data;

    // private constructor for builder
    private ApiResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Static Builder class
    public static class Builder<T> {
        private String code;
        private String message;
        private T data;

        // Method to set the code
        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }

        // Method to set the message
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        // Method to set the data
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        // Build method to create ApiResult instance
        public ApiResult<T> build() {
            return new ApiResult<>(code, message, data);
        }

    }

    // Static builder() method to create a new Builder instance
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
