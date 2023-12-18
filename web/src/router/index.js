import {createRouter, createWebHashHistory} from 'vue-router'
import ChatFunction from "@/views/chat/ChatFunction.vue";
import DrawingFunction from "@/views/drawing/DrawingFunction.vue";
import AppFunction from "@/views/app/AppFunction.vue";
import SettingFunction from "@/views/setting/SettingFunction.vue";
import MiniProgramsFunction from "@/views/miniPrograms/MiniProgramsFunction.vue";


const routes = [
    {
        path: '/',
        name: 'chat-function',
        component: ChatFunction,
        meta: {title: 'CHAT', keepAlive: true}
    },
    {
        path: '/drawing',
        name: 'drawing-function',
        component: DrawingFunction,
        meta: {title: 'DRAWING', keepAlive: true}
    },
    {
        path: '/app',
        name: 'app-function',
        component: AppFunction,
        meta: {title: 'APP', keepAlive: true}
    },
    {
        path: '/miniPrograms',
        name: 'mini-function',
        component: MiniProgramsFunction,
        meta: {title: 'MINI PROGRAMS', keepAlive: true}
    },
    {
        path: '/setting',
        name: 'setting-function',
        component: SettingFunction,
        meta: {title: 'SETTING', keepAlive: false}
    }

]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})


// TODO 全局前置守卫
router.beforeEach(async (to) => {
    let {title} = to.meta
    // TODO 设置浏览器Title
    document.title = (title ? title : '') + '- TS GPT'
})

export default router
