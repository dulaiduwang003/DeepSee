import {createRouter, createWebHashHistory} from 'vue-router'
import IndexView from '../views/index/IndexView.vue'



const routes = [
    {
        path: '/',
        name: 'home',
        component: IndexView,
        meta: {title: '首页',hideSearch: true}
    },

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
