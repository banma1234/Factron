package com.itwillbs.factron.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {

    private Integer status;
    private String message;
    private T data;

    // 성공
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(200, "정보를 가져오는 데 성공했습니다.", data);
    }

    // 실패
    public static <T> ResponseDTO<T> fail(int status, String message, T data) {
        return new ResponseDTO<>(status, message, data);
    }
}
