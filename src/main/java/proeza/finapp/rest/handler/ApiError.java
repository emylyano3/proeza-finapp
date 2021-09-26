package proeza.finapp.rest.handler;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@JsonDeserialize(builder = ApiError.Builder.class)
public class ApiError {

    private final HttpStatus status;
    private final String message;
    private final List<String> errors;

    private ApiError(Builder builder) {
        status = builder.status;
        message = builder.message;
        errors = builder.errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private HttpStatus status;
        private String message;
        private List<String> errors = Collections.emptyList();

        private Builder() {
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public Builder error(String error) {
            this.errors.add(error);
            return this;
        }

        public ApiError build() {
            return new ApiError(this);
        }
    }
}
