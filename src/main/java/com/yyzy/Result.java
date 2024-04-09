package com.yyzy;

import lombok.Data;

/**
 * 返回结果格式封装
 */
@Data
public class Result {

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILURE = 1;
    public static final int RESULT_ABNORMAL = -1;
    private int status;

    private String message = "";


    private Object data;


    public Result(String message) {
        this.message = message;
        this.status = 1;
    }

    public Result() {
    }

    /**
     * 设置成功结果
     */
    public Result setSuccessResult() {
        this.status = RESULT_SUCCESS;
        this.message = "操作成功";
        return this;
    }

    /**
     * 设置成功结果并添加数据
     */
    public Result setSuccessResult(Object data) {
        this.status = RESULT_SUCCESS;
        this.message = "操作成功";
        this.data = data;
        return this;
    }

    /**
     * 设置失败结果
     */
    public Result setFailureResult() {
        this.status = RESULT_FAILURE;
        this.message = "操作失败";
        return this;
    }

    /**
     * 设置失败结果并添加信息
     */
    public Result setFailureResult(String failureMsg) {
        this.status = RESULT_FAILURE;
        this.message = failureMsg;
        return this;
    }


    /**
     * 设置异常结果并添加信息
     */
    public Result setAbnormalResult(String message) {
        this.status = RESULT_ABNORMAL;
        this.message = message;
        return this;
    }


    /**
     * 判断结果是否是成功
     */
    public boolean judgeIsSuccess() {
        return this.status == RESULT_SUCCESS;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

