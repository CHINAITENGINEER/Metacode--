package com.huakang.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huakang.common.exception.BusinessException;
import com.huakang.mapper.entity.Member;
import com.huakang.service.dto.auth.WechatLoginDTO;
import com.huakang.service.dto.auth.WechatLoginVO;
import com.huakang.service.dto.member.CreateMemberDTO;
import com.huakang.service.dto.member.MemberVO;
import com.huakang.service.service.MemberService;
import com.huakang.service.service.WechatAuthService;
import com.huakang.service.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录服务实现类
 *
 * @author huakang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatAuthServiceImpl implements WechatAuthService {

    private final MemberService memberService;
    private final JwtUtils jwtUtils;

    @Value("${wechat.appid:your-appid}")
    private String appid;

    @Value("${wechat.secret:your-secret}")
    private String secret;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatLoginVO wechatLogin(WechatLoginDTO loginDTO) {
        // 1. 通过微信授权码换取用户OpenID
        String openid = getOpenidFromCode(loginDTO.getCode());
        if (openid == null || openid.isEmpty()) {
            throw new BusinessException("微信登录失败：无法获取用户OpenID");
        }

        // 2. 根据OpenID查询会员信息
        MemberVO member = memberService.getMemberByOpenid(openid);

        boolean isNewUser = false;
        if (member == null) {
            // 3. 如果会员不存在，创建新会员
            member = createNewMember(openid);
            isNewUser = true;
        }

        // 4. 生成JWT Token
        String token = jwtUtils.generateToken(member.getId(), member.getNickname(), "member");

        // 5. 返回登录结果
        return WechatLoginVO.builder()
                .token(token)
                .memberInfo(member)
                .isNewUser(isNewUser)
                .build();
    }

    /**
     * 通过微信授权码获取OpenID
     *
     * @param code 微信授权码
     * @return OpenID
     */
    private String getOpenidFromCode(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        try {
            String response = HttpUtil.get(url, params);
            JSONObject json = JSONUtil.parseObj(response);
            String openid = json.getStr("openid");
            String errcode = json.getStr("errcode");

            if (errcode != null) {
                log.error("微信登录错误：{}", json.getStr("errmsg"));
                return null;
            }

            return openid;
        } catch (Exception e) {
            log.error("调用微信接口异常：", e);
            return null;
        }
    }

    /**
     * 创建新会员
     *
     * @param openid 微信OpenID
     * @return 会员信息
     */
    private MemberVO createNewMember(String openid) {
        // 获取系统配置中的注册赠送积分
        Integer registerPoints = getRegisterPointsConfig();

        CreateMemberDTO createDTO = new CreateMemberDTO();
        createDTO.setOpenid(openid);
        createDTO.setInitialPoints(registerPoints != null ? registerPoints : 0);
        createDTO.setNickname("微信用户");

        // 创建会员（操作人类型为system，操作人ID和姓名为null）
        return memberService.createMember(createDTO, null, "system", "system");
    }

    /**
     * 获取系统配置中的注册赠送积分
     *
     * @return 注册赠送积分
     */
    private Integer getRegisterPointsConfig() {
        // 这里需要获取系统配置中的注册赠送积分值
        // 暂时返回默认值，后续需要完善
        return 0; // 默认新会员注册不赠送积分
    }
}