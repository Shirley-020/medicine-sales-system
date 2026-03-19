// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './assets/css/main.css'

import App from './App.vue'
import router from './router'
import { registerIcons } from './utils/icons'

const app = createApp(App)

// 注册所有Element Plus图标
registerIcons(app)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')