import request from "@/utils/Request";


/**
 *   发布SD任务
 */
export function pushSdTask(data) {
    return request({
        url: '/drawing-api/sd/push/task',
        method: 'POST',
        data: data
    })
}

/**
 *   获取任务执行状态
 */
export function getSdTask(data) {
    return request({
        url: '/drawing-api/sd/get/task/'+data,
        method: 'GET'
    })
}


/**
 * 获取SD参数
 * @returns {*}
 */
export function getSdParam() {
    return request({
        url: '/drawing-api/sd/get/param',
        method: 'GET'
    })
}

