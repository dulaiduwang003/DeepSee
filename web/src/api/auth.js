import request from "@/utils/Request";

/**
 *   邮箱登录
 */
export function emailLogin(data) {
    return request({
        url: '/auth-api/auth/email/login', method: 'POST', data
    })
}


/**
 *   获取邮箱验证码
 */
export function sendEmailCode(data) {
    return request({
        url: '/auth-api/email/send/code', method: 'POST', data
    })
}


/**
 *   注册账号
 */
export function emailEnroll(data) {
    return request({
        url: '/auth-api/auth/email/enroll', method: 'POST', data
    })
}


/**
 *   找回密码
 */
export function emailForgot(data) {
    return request({
        url: '/auth-api/auth/email/forgot', method: 'POST', data
    })
}


/**
 *   获取当前登录用户信息
 */
export function getCurrentUserInfo() {
    return request({
        url: '/auth-api/user/get/userinfo', method: 'GET'
    })
}

