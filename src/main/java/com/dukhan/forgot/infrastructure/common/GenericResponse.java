package com.dukhan.forgot.infrastructure.common;

import lombok.Data;

@Data
public class GenericResponse<T> {
    
    private T data;
    private ResultUtilVO status;
    
    public GenericResponse() {
    }
    
    public GenericResponse(T data, ResultUtilVO status) {
        this.data = data;
        this.status = status;
    }
    
    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponse<>(data, new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.SUCCESS));
    }
    public static <T> GenericResponse<T> successNoData(T data) {
        return new GenericResponse<>(data, new ResultUtilVO(AppConstant.NO_DATA_CODE, AppConstant.NODATA));
    }
    
    public static <T> GenericResponse<T> error(String errorCode, String errorMessage) {
        return new GenericResponse<>(null, new ResultUtilVO(errorCode, errorMessage));
    }
}
