import request from "@/utils/Request";


/**
 *   发布Dall绘图任务
 */
export function pushDallTask(data) {
    return request({
        url: '/drawing-api/drawing/push/dall/task',
        method: 'POST',
        data
    })
}


/**
 *   发布SD生图任务
 */
export function pushSdTask(data) {
    return request({
        url: '/drawing-api/drawing/push/sd/task',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data: data
    })
}


/**
 * 获取SD参数
 * @returns {*}
 */
export function getSdParam() {
    return request({
        url: '/drawing-api/drawing/get/sd/param',
        method: 'GET'
    })
}


/**
 *   获取任务结果
 */
export function getDallTask() {
    return request({
        url: '/drawing-api/drawing/get/task',
        method: 'GET'
    })
}
