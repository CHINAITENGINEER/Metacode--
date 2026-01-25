package com.huakang.admin.controller;

import com.huakang.common.core.PageResult;
import com.huakang.common.core.Result;
import com.huakang.service.annotation.RequireRole;
import com.huakang.service.dto.staff.CreateStaffDTO;
import com.huakang.service.dto.staff.ResetPasswordDTO;
import com.huakang.service.dto.staff.StaffVO;
import com.huakang.service.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 店员账号管理控制器
 *
 * @author huakang
 */
@Tag(name = "店员管理", description = "店员账号创建、启用/禁用、重置密码接口")
@RestController
@RequestMapping("/staffs")
@RequiredArgsConstructor
@RequireRole("admin")  // 整个Controller仅管理员可访问
public class StaffController {

    private final StaffService staffService;

    /**
     * 分页查询店员列表
     */
    @Operation(summary = "店员列表", description = "分页查询店员列表")
    @GetMapping("/list")
    public Result<PageResult<StaffVO>> listStaffs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<StaffVO> result = staffService.listStaffs(page, size);
        return Result.success(result);
    }

    /**
     * 获取店员详情
     */
    @Operation(summary = "店员详情", description = "根据ID获取店员详细信息")
    @GetMapping("/{id}")
    public Result<StaffVO> getStaff(@PathVariable Long id) {
        StaffVO staff = staffService.getStaffById(id);
        return Result.success(staff);
    }

    /**
     * 创建店员账号
     */
    @Operation(summary = "创建店员", description = "创建新的店员账号")
    @PostMapping
    public Result<StaffVO> createStaff(@Valid @RequestBody CreateStaffDTO createDTO) {
        StaffVO staff = staffService.createStaff(createDTO);
        return Result.success("创建成功", staff);
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码", description = "重置店员账号密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetPasswordDTO resetDTO) {
        staffService.resetPassword(id, resetDTO);
        return Result.<Void>success("密码重置成功", null);
    }

    /**
     * 启用/禁用店员账号
     */
    @Operation(summary = "启用/禁用", description = "启用或禁用店员账号")
    @PostMapping("/{id}/toggle-status")
    public Result<Void> toggleStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        staffService.toggleStatus(id, status);
        String message = status == 1 ? "账号已启用" : "账号已禁用";
        return Result.<Void>success(message, null);
    }
}
