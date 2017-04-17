package com.taotao.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.druid.support.json.JSONUtils;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.service.UserService;
import com.taotao.utils.JsonUtils;

/**
 * 用户查询service
 * 
 * @author hty
 *
 */

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${USER_INFO}")
	private String USER_INFO;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;

	@Override
	public TaotaoResult checkData(String param, int type) {
		// 1.从tb_user表中查询数据
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 2.查询条件根据参数动态生成
		// 1，2，3 分别代表username，phone，email
		if (type == 1) {
			criteria.andUsernameEqualTo(param);
		} else if (type == 2) {
			criteria.andPhoneEqualTo(param);
		} else if (type == 3) {
			criteria.andEmailEqualTo(param);
		} else {
			return TaotaoResult.build(400, "非法的参数");
		}
		// 执行查询
		java.util.List<TbUser> list = userMapper.selectByExample(example);
		// 3.判断查询结果，如果查询到数据返回false
		if (list == null || list.size() == 0) {
			// 4.如果没有返回true
			return TaotaoResult.ok(true);
		}
		// 5.使用TaotaoResult包装，并返回
		return TaotaoResult.ok(false);
	}

	@Override
	public TaotaoResult register(TbUser user) {
		// 1.使用TbUser提交请求
		if (StringUtils.isBlank(user.getUsername())) {
			return TaotaoResult.build(400, "用户名不能为空");
		}
		if (StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		// 校验数据是否可用
		// 校验用户名
		TaotaoResult result = checkData(user.getUsername(), 1);
		if (!(boolean) result.getData()) {
			return TaotaoResult.build(400, "此用户名已经被占用");
		}
		// 校验手机号
		if (StringUtils.isNotBlank(user.getPhone())) {
			result = checkData(user.getPhone(), 2);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "此手机号已经被占用");
			}
		}
		// 校验邮箱
		if (StringUtils.isNotBlank(user.getEmail())) {
			result = checkData(user.getEmail(), 3);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "此邮箱已经被占用");
			}
		}
		// 2.补全user的其他属性
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 3.密码进行MD5加密
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		// 4.把用户信息添加到数据库中
		userMapper.insert(user);
		// 5.返回taotaoResult
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(String username, String password) {
		// 1.判断用户名密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		// 查询用户信息
		List<TbUser> list = userMapper.selectByExample(example);
		if (list == null || list.size() == 0) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		TbUser user = list.get(0);
		// 校验密码
		if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		// 2.登陆成功后生成token。可以使用uuid
		String token = UUID.randomUUID().toString();
		// 3.把用户信息保存到redis。key就是token，value就是TbUser对象转换成json
		// 4.使用String类型保存Session信息。可以使用"前缀：token"为key
		user.setPassword(null);
		jedisClient.set(USER_INFO + ":" + token, JsonUtils.objectToJson(user));
		// 5.设置key的过期时间。模拟Session的过期时间。一般半个小时
		jedisClient.expire(USER_INFO + ":" + token, SESSION_EXPIRE);
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		// 根据token查询redis
		String json = jedisClient.get(USER_INFO + ":" + token);
		if (StringUtils.isBlank(json)) {
			// 如果查询不到，返回用户已经过期，提示重新登录
			return TaotaoResult.build(400, "用户登录已经过期，请重新登录");
		}
		// 如果查询到数据，说明用户已经登陆
		// 需要重置key的过期时间
		jedisClient.expire(USER_INFO + ":" + token, SESSION_EXPIRE);
		// 把json数据转换成TbUser对象，然后用TaotaoResult包装并返回
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
	}

	@Override
	public TaotaoResult removeToken(String token) {
		// 根据token查询redis
		String json = jedisClient.get(USER_INFO + ":" + token);
		if (StringUtils.isBlank(json)) {
			// 如果查询不到，返回用户已经过期，提示重新登录
			return TaotaoResult.build(400, "暂无用户登录");
		}
		// 如果查询到数据，说明用户已经登陆
		// 需要重置key的过期时间
		jedisClient.expire(USER_INFO + ":" + token, 0);
		return TaotaoResult.ok();
	}

}
