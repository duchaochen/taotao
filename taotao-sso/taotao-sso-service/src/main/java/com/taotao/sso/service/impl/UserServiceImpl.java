package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    /**
     * 设置redis的key
     */
    @Value("${USER_SESSION}")
    private String USER_SESSION;
    /**
     * 过期时间
     */
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;

    /**
     * 检查用户名是否可用
     * @param data
     * @param type
     * @return
     */
    @Override
    public TaotaoResult checkData(String data, int type) {
        TbUserExample tbUserExample = new TbUserExample();
        Criteria criteria = tbUserExample.createCriteria();
        //1表示用户名
        if (1 == type) {
            criteria.andUsernameEqualTo(data);
         //2表示手机号
        } else if (2 == type) {
            criteria.andPhoneEqualTo(data);
        //3.表示邮箱
        } else if (3 == type) {
            criteria.andEmailEqualTo(data);
        } else {
            return TaotaoResult.build(400,"参数里包含非法值");
        }

        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        if (tbUsers != null && tbUsers.size() > 0) {
            //表示用户名或者手机号码或者邮箱地址已存在
            return TaotaoResult.ok(false);
        }
        //表示用户名或者手机号码或者邮箱地址可用。
        return TaotaoResult.ok(true);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public TaotaoResult register(TbUser user) {
        //检查数据的有效性
        if (StringUtils.isBlank(user.getUsername())) {
            return TaotaoResult.build(400, "用户名不能为空");
        }
        //判断用户名是否重复
        TaotaoResult taotaoResult = checkData(user.getUsername(), 1);
        if (!(boolean) taotaoResult.getData()) {
            return TaotaoResult.build(400, "用户名重复");
        }
        //判断密码是否为空
        if (StringUtils.isBlank(user.getPassword())) {
            return TaotaoResult.build(400, "密码不能为空");
        }
        if (StringUtils.isNotBlank(user.getPhone())) {
            //是否重复校验
            taotaoResult = checkData(user.getPhone(), 2);
            if (!(boolean) taotaoResult.getData()) {
                return TaotaoResult.build(400, "电话号码重复");
            }
        }
        //如果email不为空的话进行是否重复校验
        if (StringUtils.isNotBlank(user.getEmail())) {
            //是否重复校验
            taotaoResult = checkData(user.getEmail(), 3);
            if (!(boolean) taotaoResult.getData()) {
                return TaotaoResult.build(400, "email重复");
            }
        }
        //补全pojo的属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //密码要进行md5加密
        String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Pass);
        //插入数据
        tbUserMapper.insert(user);
        //返回注册成功
        return TaotaoResult.ok();
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TaotaoResult login(String username, String password) {

        //查询用户信息
        TbUserExample tbUserExample = new TbUserExample();
        Criteria criteria = tbUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> tbUsers = tbUserMapper.selectByExample(tbUserExample);
        //验证用户是否存在
        if (tbUsers == null && tbUsers.size() == 0) {
            //返回登录失败
            return TaotaoResult.build(400, "用户名或密码不正确");
        }
        TbUser tbUser = tbUsers.get(0);
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUser.getPassword())) {
            //返回登录失败
            return TaotaoResult.build(400, "用户名或密码不正确");
        }

        //生成token，使用uuid
        String token = UUID.randomUUID().toString();
        //清空密码,保证安全
        tbUser.setPassword(null);

        //将用户信息存入redis
        jedisClient.set(USER_SESSION + ":" + token, JsonUtils.objectToJson(tbUser));
        //设置redis过期时间
        jedisClient.expire(USER_SESSION + ":" + token,SESSION_EXPIRE);

        return TaotaoResult.ok(token);
    }

    /**
     * 获取token
     * @param token
     * @return
     */
    @Override
    public TaotaoResult getUserByToken(String token) {
        String json = jedisClient.get(USER_SESSION + ":" + token);
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "用户登录已经过期");
        }
        //重置Session的过期时间
        jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
        //把json转换成User对象
        TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
        return TaotaoResult.ok(user);
        //return TaotaoResult.ok(json);
    }
}
