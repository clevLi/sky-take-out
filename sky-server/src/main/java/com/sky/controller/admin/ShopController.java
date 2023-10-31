package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    public static final String KEYS = "SHOP_STATUS";
    @Autowired
    public RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态为:{}",status);
        redisTemplate.opsForValue().set("SHOP_STATUS",status);
        return  Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getStatus(){
        Integer sta = (Integer)redisTemplate.opsForValue().get(KEYS);
        log.info("获取店铺营业状态:{}",sta);
        return  Result.success(sta);
    }
}
