package com.wangguangwu.pipelineexecutor.spi.context;

/**
 * 结果容器接口
 *
 * @param <R> 结果类型
 * @author wangguangwu
 */
public interface ResultHolder<R> {

    /**
     * 设置处理结果
     *
     * @param result 结果对象（可为null）
     */
    void setResult(R result);

    /**
     * 获取处理结果
     *
     * @return 结果对象或null
     */
    R getResult();

    /**
     * 检查是否存在结果
     *
     * @return 存在返回true
     */
    boolean hasResult();

    /**
     * 清空当前结果
     */
    void clearResult();
}
