package proeza.finapp.exception;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.springframework.http.HttpStatus;

@JsonDeserialize(builder = ApiError.Builder.class)
public class ApiError {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String cause;

    private ApiError(Builder builder) {
        httpStatus = builder.httpStatus;
        code = builder.code;
        message = builder.message;
        cause = builder.cause;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private HttpStatus httpStatus;
        private String code;
        private String message;
        private String cause;

        private Builder() {
        }

        public Builder httpStatus(HttpStatus status) {
            this.httpStatus = status;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder cause(String cause) {
            this.cause = cause;
            return this;
        }

        public ApiError build() {
            return new ApiError(this);
        }
    }
}