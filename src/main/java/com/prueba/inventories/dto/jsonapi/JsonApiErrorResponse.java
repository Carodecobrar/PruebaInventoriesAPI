package com.prueba.inventories.dto.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiErrorResponse {
    private List<Error> errors;

    @Data
    @Builder
    public static class Error {
        private String status;
        private String title;
        private String detail;
        private Source source;

        @Data
        @Builder
        public static class Source {
            private String pointer;
            private Object rejectedValue;
            private String parameter;
        }
    }
}
