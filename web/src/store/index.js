import {createStore} from 'vuex'

export default createStore({
    state: {
        //用户数据
        userInfo: undefined,
        //用户令牌
    },
    getters: {
        userInfo: (state) => state.userInfo,
    },
    mutations: {
        logout(state) {
            state.userInfo = undefined;
        },
        setUserInfo(state, info) {
            state.userInfo = info;
            localStorage.setItem("userInfo", JSON.stringify(info));
        },
        initState(state) {
            let token = localStorage.getItem("token");
            if (token) {
                let user = localStorage.getItem("userInfo");
                state.userInfo = JSON.parse(user);
            }
        },
    },
    actions: {},
    modules: {}
})
