<template>
  <!-- 数据概览页面 - 采用卡片式布局设计 -->
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1>欢迎回来，{{ nickname }} 👋</h1>
          <p>今天是个阅读的好日子，来看看图书馆的最新动态吧</p>
        </div>
        <div class="banner-illustration">
          <svg viewBox="0 0 200 150" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="20" y="40" width="60" height="80" rx="4" fill="rgba(255,255,255,0.2)"/>
            <rect x="30" y="50" width="40" height="4" rx="2" fill="rgba(255,255,255,0.4)"/>
            <rect x="30" y="60" width="35" height="4" rx="2" fill="rgba(255,255,255,0.3)"/>
            <rect x="30" y="70" width="40" height="4" rx="2" fill="rgba(255,255,255,0.3)"/>
            <rect x="90" y="30" width="60" height="90" rx="4" fill="rgba(255,255,255,0.25)"/>
            <rect x="100" y="40" width="40" height="4" rx="2" fill="rgba(255,255,255,0.4)"/>
            <rect x="100" y="50" width="35" height="4" rx="2" fill="rgba(255,255,255,0.3)"/>
            <rect x="140" y="60" width="40" height="60" rx="4" fill="rgba(255,255,255,0.15)"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" v-for="(stat, index) in statCards" :key="index">
        <div class="stat-icon" :style="{ background: stat.gradient }">
          <component :is="stat.icon" />
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
        <div class="stat-trend" :class="stat.trendType">
          <span>{{ stat.trend }}</span>
        </div>
      </div>
    </div>

    <!-- 图表和列表区域 -->
    <div class="content-grid">
      <!-- 分类统计图表 -->
      <div class="chart-card">
        <div class="card-header">
          <h3>图书分类统计</h3>
        </div>
        <div class="chart-container" ref="chartRef" v-show="hasCategoryData"></div>
        <div class="chart-empty" v-show="!hasCategoryData">
          <el-icon><PieChart /></el-icon>
          <span>暂无分类数据</span>
        </div>
      </div>

      <!-- 最新图书列表 -->
      <div class="list-card">
        <div class="card-header">
          <h3>最新上架</h3>
          <router-link to="/home/books" class="view-all">查看全部 →</router-link>
        </div>
        <div class="book-list">
          <div 
            class="book-item" 
            v-for="(book, index) in latestBooks" 
            :key="index"
          >
            <div class="book-cover" :style="{ background: getBookColor(index) }">
              {{ book.title.charAt(0) }}
            </div>
            <div class="book-info">
              <span class="book-title">{{ book.title }}</span>
              <span class="book-author">{{ book.author }}</span>
            </div>
            <div class="book-category">
              <el-tag size="small" effect="plain">{{ book.category || '未分类' }}</el-tag>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="latestBooks.length === 0" class="empty-state">
            <el-icon><Reading /></el-icon>
            <span>暂无图书数据</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div class="actions-grid">
        <router-link to="/home/books" class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <Reading />
          </div>
          <span>浏览图书</span>
        </router-link>
        <router-link to="/home/borrow" class="action-card">
          <div class="action-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <Collection />
          </div>
          <span>借阅记录</span>
        </router-link>
        <div class="action-card" @click="refreshData">
          <div class="action-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <Refresh />
          </div>
          <span>刷新数据</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { Reading, User, Collection, Refresh } from '@element-plus/icons-vue'
import { PieChart } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { dashboardApi, bookApi } from '../api'

/**
 * 数据概览组件
 * 展示图书馆的核心统计数据和图表
 */
export default {
  name: 'DashboardView',
  components: { Reading, User, Collection, Refresh, PieChart },
  setup() {
    // 用户昵称
    const nickname = ref(localStorage.getItem('nickname') || '用户')
    
    // 统计数据
    const stats = ref({})
    const latestBooks = ref([])
    const chartRef = ref(null)
    const hasCategoryData = ref(false)
    let chartInstance = null

    // 统计卡片配置
    const statCards = computed(() => [
      {
        icon: Reading,
        value: stats.value.totalBooks || 0,
        label: '图书总数',
        trend: '+12%',
        trendType: 'up',
        gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      },
      {
        icon: User,
        value: stats.value.totalUsers || 0,
        label: '注册用户',
        trend: '+8%',
        trendType: 'up',
        gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
      },
      {
        icon: Collection,
        value: stats.value.borrowedBooks || 0,
        label: '借阅中',
        trend: '-3%',
        trendType: 'down',
        gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
      }
    ])

    // 图书封面颜色
    const bookColors = [
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
    ]

    /**
     * 获取图书封面颜色
     * @param {number} index - 索引
     */
    const getBookColor = (index) => bookColors[index % bookColors.length]

    /**
     * 加载仪表盘数据
     */
    const loadData = async () => {
      try {
        const [statsRes, booksRes] = await Promise.all([
          dashboardApi.getStats(),
          bookApi.getLatest()
        ])
        
        if (statsRes.code === 200) {
          stats.value = statsRes.data
          const categoryData = statsRes.data.categoryStats || []
          hasCategoryData.value = categoryData.length > 0
          if (hasCategoryData.value) {
            initChart(categoryData)
          }
        }
        
        if (booksRes.code === 200) {
          latestBooks.value = booksRes.data.slice(0, 5)
        }
      } catch (error) {
        console.error('加载仪表盘数据失败:', error)
      }
    }

    /**
     * 初始化图表
     * @param {Array} data - 分类统计数据
     */
    const initChart = (data) => {
      if (!chartRef.value) return
      
      if (chartInstance) {
        chartInstance.dispose()
      }
      
      chartInstance = echarts.init(chartRef.value)
      
      const option = {
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: 'rgba(0, 0, 0, 0.08)',
          borderWidth: 1,
          padding: [12, 16],
          textStyle: {
            color: '#1d1d1f',
            fontSize: 13
          },
          formatter: '{b}: {c} 本 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center',
          itemWidth: 12,
          itemHeight: 12,
          itemGap: 16,
          textStyle: {
            color: '#6e6e73',
            fontSize: 13
          }
        },
        series: [{
          type: 'pie',
          radius: ['50%', '75%'],
          center: ['35%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff',
            borderWidth: 3
          },
          label: {
            show: false
          },
          emphasis: {
            label: {
              show: false
            },
            itemStyle: {
              shadowBlur: 20,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.2)'
            }
          },
          data: data.map((item, index) => ({
            name: item.category || '未分类',
            value: item.count,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 1, [
                { offset: 0, color: ['#667eea', '#f093fb', '#4facfe', '#43e97b', '#fa709a'][index % 5] },
                { offset: 1, color: ['#764ba2', '#f5576c', '#00f2fe', '#38f9d7', '#fee140'][index % 5] }
              ])
            }
          }))
        }]
      }
      
      chartInstance.setOption(option)
      
      // 响应式调整
      window.addEventListener('resize', () => {
        chartInstance?.resize()
      })
    }

    /**
     * 刷新数据
     */
    const refreshData = () => {
      loadData()
    }

    onMounted(loadData)

    return { 
      nickname,
      stats, 
      latestBooks, 
      chartRef,
      statCards,
      getBookColor,
      refreshData,
      hasCategoryData
    }
  }
}
</script>

<style scoped>
/* 仪表盘容器 */
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 32px 40px;
  margin-bottom: 24px;
  overflow: hidden;
  position: relative;
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-text h1 {
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px 0;
}

.banner-text p {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.banner-illustration {
  width: 200px;
  height: 150px;
  opacity: 0.8;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #86868b;
  margin-top: 4px;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-trend.up {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.stat-trend.down {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

/* 图表卡片 */
.chart-card,
.list-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.time-filter {
  font-size: 13px;
  color: #86868b;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.time-filter:hover,
.time-filter.active {
  background: #f5f5f7;
  color: #1d1d1f;
}

.view-all {
  font-size: 13px;
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.view-all:hover {
  color: #764ba2;
}

.chart-container {
  height: 280px;
}

/* 图表空状态 */
.chart-empty {
  height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #86868b;
  gap: 16px;
}

.chart-empty .el-icon {
  font-size: 64px;
  opacity: 0.3;
}

.chart-empty span {
  font-size: 14px;
}

/* 图书列表 */
.book-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.book-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 12px;
  transition: background 0.3s ease;
}

.book-item:hover {
  background: #f5f5f7;
}

.book-cover {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 18px;
  flex-shrink: 0;
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.book-title {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 12px;
  color: #86868b;
  margin-top: 2px;
}

.book-category :deep(.el-tag) {
  border-radius: 6px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #86868b;
  gap: 12px;
}

.empty-state .el-icon {
  font-size: 48px;
  opacity: 0.5;
}

/* 快捷操作 */
.quick-actions h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.action-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.action-card span {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .banner-illustration {
    display: none;
  }
}
</style>
