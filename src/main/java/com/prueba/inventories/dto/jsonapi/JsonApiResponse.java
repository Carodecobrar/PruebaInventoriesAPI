package com.prueba.inventories.dto.jsonapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiResponse<T> {
    private List<Resource<T>> data;
    private Meta meta;

    @Data
    @Builder
    public static class Resource<T> {
        private String type;
        private String id;
        private T attributes;
    }

    @Data
    @Builder
    public static class Meta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
