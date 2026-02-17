<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
/**
 * 应用根组件
 * 作为整个应用的入口，负责渲染路由视图
 */
export default {
  name: 'App'
}
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 根元素样式 */
:root {
  /* 主色调 - 高端深邃蓝 */
  --primary-color: #0a0a0a;
  --primary-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --accent-color: #6366f1;
  --accent-light: #818cf8;
  
  /* 背景色系 */
  --bg-primary: #ffffff;
  --bg-secondary: #fafafa;
  --bg-tertiary: #f5f5f7;
  --bg-dark: #1d1d1f;
  
  /* 文字色系 */
  --text-primary: #1d1d1f;
  --text-secondary: #6e6e73;
  --text-tertiary: #86868b;
  --text-inverse: #f5f5f7;
  
  /* 边框和阴影 */
  --border-color: rgba(0, 0, 0, 0.08);
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 20px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 8px 40px rgba(0, 0, 0, 0.12);
  --shadow-xl: 0 20px 60px rgba(0, 0, 0, 0.15);
  
  /* 圆角 */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 24px;
  
  /* 过渡动画 */
  --transition-fast: 0.15s ease;
  --transition-normal: 0.3s ease;
  --transition-slow: 0.5s ease;
}

/* 全局字体设置 */
body {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'SF Pro Text', 
    'Helvetica Neue', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 
    sans-serif;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 应用容器 */
#app {
  min-height: 100vh;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* 选中文字样式 */
::selection {
  background: var(--accent-color);
  color: white;
}

/* Element Plus 组件样式覆盖 */
.el-button--primary {
  background: var(--accent-color) !important;
  border-color: var(--accent-color) !important;
}

.el-button--primary:hover {
  background: var(--accent-light) !important;
  border-color: var(--accent-light) !important;
}
</style>
