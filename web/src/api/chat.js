import request from "@/utils/Request";


/**
 *   获取模型列表
 */
export function getAiModelList() {
    return request({
        url: '/chat-api/model/get/all', method: 'GET'
    })
}
