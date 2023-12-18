package com.cn.service;

import com.cn.dto.UploadUserNickNameDto;
import com.cn.vo.UserInfoVo;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {


    /**
     * Gets current user info.
     *
     * @return the current user info
     */
    UserInfoVo getCurrentUserInfo();


    /**
     * Upload avatar.
     *
     * @param file the file
     */
    void uploadAvatar(MultipartFile file);


    void uploadNickName(final UploadUserNickNameDto dto);
}
