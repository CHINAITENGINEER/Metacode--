package com.huakang.service.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员列表查询DTO
 *
 * @author huakang
 */
@Data
@Schema(description = "会员列表查询条件")
public class MemberListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    /**
     * 昵称（模糊查询）
     */
    @Schema(description = "昵称（支持模糊搜索）", example = "张三")
    private String nickname;

    /**
     * 手机号（精确查询）
     */
    @Schema(description = "手机号（精确匹配）", example = "13800138000")
    private String phone;
}
