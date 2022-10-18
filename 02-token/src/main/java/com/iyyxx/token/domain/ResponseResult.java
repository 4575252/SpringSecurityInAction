package com.iyyxx.token.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: Result
 * @description: TODO 类描述
 * @author: eric 4575252@gmail.com
 * @date: 2022/10/12/0012 17:23:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {

    private Integer code;

    private String message;

    private T data;

    public ResponseResult(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, T data){
        this.code = code;
        this.data = data;
    }
}