<template>
  <!-- 主页布局 - 采用现代化侧边栏设计 -->
  <div class="home-container">
    <el-container>
      <!-- 侧边导航栏 -->
      <el-aside width="260px" class="sidebar">
        <!-- 品牌标识 -->
        <div class="sidebar-header">
          <div class="logo-wrapper">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="logo-text">
              <h1>智慧图书馆</h1>
              <span>Smart Library</span>
            </div>
          </div>
        </div>

        <!-- 导航菜单 -->
        <nav class="sidebar-nav">
          <router-link 
            v-for="item in menuItems" 
            :key="item.path"
            :to="item.path" 
            class="nav-item"
            :class="{ active: activeMenu === item.path }"
          >
            <div class="nav-icon">
              <component :is="item.icon" />
            </div>
            <span class="nav-label">{{ item.label }}</span>
            <div class="nav-indicator"></div>
          </router-link>
        </nav>

        <!-- 侧边栏底部 -->
        <div class="sidebar-footer">
          <div class="user-card">
            <div class="user-avatar">
              {{ nickname.charAt(0).toUpperCase() }}
            </div>
            <div class="user-info">
              <span class="user-name">{{ nickname }}</span>
              <span class="user-role">{{ roleText }}</span>
            </div>
          </div>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="main-header">
          <div class="header-left">
            <!-- 面包屑导航 -->
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/home/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <!-- 用户下拉菜单 -->
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-dropdown">
                <div class="dropdown-avatar">
                  {{ nickname.charAt(0).toUpperCase() }}
                </div>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-menu">
                  <el-dropdown-item disabled>
                    <div class="menu-user-info">
                      <span class="menu-username">{{ nickname }}</span>
                      <span class="menu-email">@{{ username }}</span>
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 页面内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  DataAnalysis, 
  Reading, 
  Collection, 
  ArrowDown,
  SwitchButton,
  Menu as IconMenu
} from '@element-plus/icons-vue'

/**
 * 主页布局组件
 * 包含侧边导航栏、顶部栏和主内容区域
 */
export default {
  name: 'HomeView',
  components: { 
    DataAnalysis, 
    Reading, 
    Collection, 
    ArrowDown,
    SwitchButton,
    IconMenu
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    // 用户信息
    const username = ref(localStorage.getItem('username') || '用户')
    const nickname = ref(localStorage.getItem('nickname') || '用户')
    const role = ref(localStorage.getItem('role') || '0')
    
    // 当前激活的菜单
    const activeMenu = computed(() => route.path)
    
    // 角色文本
    const roleText = computed(() => role.value === '1' ? '管理员' : '普通用户')
    
    // 是否管理员
    const isAdmin = computed(() => role.value === '1')
    
    // 导航菜单配置 - 普通用户看到"图书列表"，管理员多一个"分类管理"
    const menuItems = computed(() => {
      const items = [
        { path: '/home/dashboard', label: '数据概览', icon: DataAnalysis },
        { path: '/home/books', label: isAdmin.value ? '图书管理' : '图书列表', icon: Reading }
      ]
      if (isAdmin.value) {
        items.push({ path: '/home/categories', label: '分类管理', icon: IconMenu })
      }
      items.push({ path: '/home/borrow', label: '借阅记录', icon: Collection })
      return items
    })
    
    // 当前页面标题
    const currentPageTitle = computed(() => {
      const item = menuItems.value.find(m => m.path === activeMenu.value)
      return item ? item.label : '智慧图书馆'
    })

    /**
     * 处理下拉菜单命令
     * @param {string} command - 命令类型
     */
    const handleCommand = (command) => {
      if (command === 'logout') {
        localStorage.clear()
        router.push('/login')
      }
    }

    return { 
      username, 
      nickname, 
      role,
      roleText,
      activeMenu, 
      menuItems,
      currentPageTitle,
      handleCommand 
    }
  }
}
</script>

<style scoped>
/* 主容器 */
.home-container {
  min-height: 100vh;
  background: #f5f5f7;
}

/* 侧边栏 */
.sidebar {
  background: linear-gradient(180deg, #1d1d1f 0%, #2d2d30 100%);
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
}

.logo-text h1 {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: -0.3px;
}

.logo-text span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.6);
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%);
  color: #ffffff;
}

.nav-item.active .nav-indicator {
  opacity: 1;
}

.nav-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-label {
  font-size: 14px;
  font-weight: 500;
}

.nav-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 0 3px 3px 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 16px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
}

.user-role {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

/* 主内容区容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 260px;
}

/* 顶部导航栏 */
.main-header {
  height: 72px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

/* 面包屑样式 */
.header-left :deep(.el-breadcrumb) {
  font-size: 14px;
}

.header-left :deep(.el-breadcrumb__item) {
  .el-breadcrumb__inner {
    color: #86868b;
    font-weight: 400;
  }
  
  &:last-child .el-breadcrumb__inner {
    color: #1d1d1f;
    font-weight: 500;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 用户下拉 */
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px;
  border-radius: 10px;
  transition: background 0.3s ease;
}

.user-dropdown:hover {
  background: #f5f5f7;
}

.dropdown-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.dropdown-arrow {
  color: #86868b;
  font-size: 12px;
}

/* 用户菜单 */
.menu-user-info {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
}

.menu-username {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

.menu-email {
  font-size: 12px;
  color: #86868b;
}

/* 主内容区 */
.main-content {
  flex: 1;
  padding: 24px 32px;
  background: #f5f5f7;
  overflow-y: auto;
}

/* 移动端响应式 - 侧边栏缩小只显示图标 */
@media (max-width: 768px) {
  .sidebar {
    width: 72px;
  }
  
  .sidebar-header {
    padding: 16px 14px;
  }
  
  .logo-text {
    display: none;
  }
  
  .logo-wrapper {
    justify-content: center;
  }
  
  .sidebar-nav {
    padding: 16px 10px;
  }
  
  .nav-item {
    justify-content: center;
    padding: 14px;
  }
  
  .nav-label {
    display: none;
  }
  
  .nav-icon {
    width: 24px;
    height: 24px;
  }
  
  .sidebar-footer {
    padding: 12px 10px;
  }
  
  .user-card {
    justify-content: center;
    padding: 10px;
  }
  
  .user-info {
    display: none;
  }
  
  .main-container {
    margin-left: 72px;
  }
  
  .main-header {
    padding: 0 16px;
  }
  
  .main-content {
    padding: 16px;
  }
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
