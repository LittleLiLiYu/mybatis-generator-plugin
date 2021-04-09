package com.kuangyu.core.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper基类
 * @param <T>
 * @param <E>
 * @author kuangyu
 */
public interface BaseMapper<T, E> {

    /**
     * 新增方法
     * @param record
     * @return
     */
    Integer insertSelective(T record);

    /**
     * 按条件修改部分字段
     * @param record
     * @param example
     * @return
     */
    Integer updateByExampleSelective(@Param("record") T record, @Param("example") E example);

    /**
     * 按主键修改部分字段
     * @param record
     * @return
     */
    Integer updateByPrimaryKeySelective(T record);

    /**
     * 按主键删除
     * @param record
     * @return
     */
    Integer deleteByPrimaryKey(T record);

    /**
     * 按条件删除
     * @param example
     * @return
     */
    Integer deleteByExample(E example);

    /**
     * 按条件查询count
     * @param example
     * @return
     */
    Integer countByExample(E example);

    /**
     * 按主键查询记录
     * @param record
     * @return
     */
    T selectByPrimaryKey(T record);

    /**
     * 按条件查询列表
     * @param example
     * @return
     */
    List<T> selectByExample(E example);

}
