package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author worlt
 * @Date 2025/4/28 下午8:49
 */
@Mapper
public interface OrderMapper {


    /**
     * 插入订单数据
     * @param order
     */
    public void insert(Orders order);
}
