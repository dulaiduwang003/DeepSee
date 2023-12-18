package com.cn.controller;


import com.aliyun.oss.OSSException;
import com.cn.dto.UploadUserNickNameDto;
import com.cn.msg.Result;
import com.cn.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Gets current user info.
     *
     * @return the current user info
     */
    @GetMapping(value = "/get/userinfo", name = "获取当前登陆用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getCurrentUserInfo() {
        return Result.data(userService.getCurrentUserInfo());
    }

    /**
     * Gets current user info.
     *
     * @return the current user info
     */
    @PostMapping(value = "/upload/avatar", name = "上传头像", consumes = "multipart/form-data")
    public Result uploadUserAvatar(@Valid @NotNull(message = "上传头像不能为空") final MultipartFile file) {
        try {
            userService.uploadAvatar(file);
            return Result.ok();
        } catch (OSSException e) {
            log.error("上传头像失败 原因:{} 位置:{}", e.getMessage(), e.getClass());
            return Result.error(e.getMessage());
        }
    }


    /**
     * Upload user nick name result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "upload/nickname", name = "更新昵称", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result uploadUserNickName(@Validated @RequestBody UploadUserNickNameDto dto) {
        userService.uploadNickName(dto);
        return Result.ok();
    }
}
