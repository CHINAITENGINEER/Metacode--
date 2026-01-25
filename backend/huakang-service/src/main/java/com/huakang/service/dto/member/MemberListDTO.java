package com.huakang.service.dto.member;

import lombok.Data;

import java.io.Serializable;

/**
 * 会员列表查询DTO
 *
 * @author huakang
 */
@Data
public class MemberListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 手机号（精确查询）
     */
    private String phone;
}
