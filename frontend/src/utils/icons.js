// src/utils/icons.js
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

export function registerIcons(app) {
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }
}

// 导出所有图标，以便在组件中直接导入使用
export * from '@element-plus/icons-vue'