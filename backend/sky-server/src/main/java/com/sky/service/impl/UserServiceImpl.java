package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UseMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UseMapper useMapper;
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 微信用户的登录功能
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前微信用户的openid
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);

        String openid = jsonObject.getString("openid");
        //判断openid是否为空，如果为空表示登陆失败，抛出业务异常

        if(openid == null){
            log.error("微信登陆失败{}",json);
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = useMapper.getByOpenid(openid);
        //判断当前用户是否为新用户
        //如果是新用户，自动完成注册
        if (user == null) {

            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            useMapper.insert(user);
        }

        //返回用户对象
        return user;

    }
}
