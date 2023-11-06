package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class MyTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public  void processTimeoutOrder(){
        log.info("当前时间:{}", LocalDateTime.now());

        LocalDateTime endTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> orders = orderMapper.getByStatusAndTime(Orders.PENDING_PAYMENT, endTime);
        if(orders!=null && orders.size() > 0){
            for(Orders orders1:orders){
                orders1.setStatus(Orders.CANCELLED);
                orders1.setCancelReason("订单超时，自动取消");
                orders1.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders1);
            }
        }
    }

    /**
     * 处理派送中订单
     */
    @Scheduled(cron = "0 0 1 * * *")
    public  void processDeliveryOrder(){
        log.info("定时处理派送中订单");
        LocalDateTime time = LocalDateTime.now().plusHours(-1);
        List<Orders> orders = orderMapper.getByStatusAndTime(Orders.DELIVERY_IN_PROGRESS, time);
        if(orders!=null && orders.size() > 0){
            for(Orders orders1:orders){
                orders1.setStatus(Orders.COMPLETED);
                orderMapper.update(orders1);
            }
        }

    }
}
