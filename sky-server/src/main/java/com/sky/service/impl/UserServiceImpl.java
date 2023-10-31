package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.AccountLockedException;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;
    public static final String grantType = "authorization_code";
    public static final String wxLoginUrl = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;

    @Override
    /**
     * 微信登陆
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        log.info("用户微信登陆:{}",userLoginDTO);
       //通过接口获取微信openid
        String code = userLoginDTO.getCode();
        String openid = getOpenid(code);

        // 若openid为空，则报错
        if(openid==null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
       // 通过openid查找user表是否存在
        User user = userMapper.getUserByOpenid(openid);

        //不存在则创建
        if(user==null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户user

        return user;
    }

    /**
     * 调用微信登录借口，获取用户openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        String appid = weChatProperties.getAppid();
        String secret = weChatProperties.getSecret();
        String grant_type = grantType;
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("secret",secret);
        paramMap.put("js_code",code);
        paramMap.put("grant_type",grant_type);
        String json = HttpClientUtil.doGet(wxLoginUrl, paramMap);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
