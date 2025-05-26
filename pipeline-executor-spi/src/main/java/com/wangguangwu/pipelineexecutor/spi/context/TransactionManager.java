package com.wangguangwu.pipelineexecutor.spi.context;

/**
 * 事务控制接口
 *
 * @author wangguangwu
 */
public interface TransactionManager {

    /**
     * 开启新事务
     *
     * @throws IllegalStateException 当已有活跃事务时抛出
     */
    void beginTransaction();

    /**
     * 提交当前事务
     *
     * @throws IllegalStateException 当没有活跃事务时抛出
     */
    void commit();

    /**
     * 回滚当前事务
     *
     * @throws IllegalStateException 当没有活跃事务时抛出
     */
    void rollback();

    /**
     * 检查是否在事务中
     *
     * @return 是返回true
     */
    boolean isInTransaction();

    /**
     * 创建事务保存点
     *
     * @param name 保存点名称（非空）
     * @return 保存点ID
     */
    String createSavepoint(String name);

    /**
     * 回滚到指定保存点
     *
     * @param savepointId 保存点ID（非空）
     */
    void rollbackToSavepoint(String savepointId);
}
