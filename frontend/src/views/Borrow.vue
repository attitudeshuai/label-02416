<template>
  <!-- 借阅记录页面 - 采用现代化表格设计 -->
  <div class="borrow-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-info">
        <h1>借阅记录</h1>
        <p>查看和管理您的图书借阅历史</p>
      </div>
      <div class="header-stats">
        <div class="stat-item">
          <span class="stat-value">{{ borrowedCount }}</span>
          <span class="stat-label">借阅中</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ returnedCount }}</span>
          <span class="stat-label">已归还</span>
        </div>
      </div>
    </div>

    <!-- 记录列表 -->
    <div class="records-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <!-- 空状态 -->
      <div v-else-if="records.length === 0" class="empty-state">
        <div class="empty-icon">📖</div>
        <h3>暂无借阅记录</h3>
        <p>您还没有借阅过任何图书</p>
        <router-link to="/home/books" class="browse-btn">
          去浏览图书
        </router-link>
      </div>

      <!-- 记录卡片列表 -->
      <div v-else class="records-list">
        <div 
          v-for="(record, index) in records" 
          :key="index" 
          class="record-card"
          :class="{ 'is-overdue': record.status === 2 }"
        >
          <!-- 图书封面 -->
          <div class="record-cover" :style="{ background: getBookGradient(index) }">
            <span>{{ (record.bookTitle || '书').charAt(0) }}</span>
          </div>

          <!-- 记录信息 -->
          <div class="record-info">
            <div class="record-main">
              <h3 class="record-title">{{ record.bookTitle || '未知图书' }}</h3>
              <p v-if="isAdmin" class="record-user">
                <el-icon><User /></el-icon>
                {{ record.username || '未知用户' }}
              </p>
            </div>

            <div class="record-dates">
              <div class="date-item">
                <span class="date-label">借阅日期</span>
                <span class="date-value">{{ formatDate(record.borrowDate) }}</span>
              </div>
              <div class="date-item">
                <span class="date-label">应还日期</span>
                <span class="date-value" :class="{ 'is-warning': isNearDue(record) }">
                  {{ formatDate(record.dueDate) }}
                </span>
              </div>
              <div v-if="record.returnDate" class="date-item">
                <span class="date-label">归还日期</span>
                <span class="date-value">{{ formatDate(record.returnDate) }}</span>
              </div>
            </div>
          </div>

          <!-- 状态和操作 -->
          <div class="record-actions">
            <el-tag 
              :type="getStatusType(record.status)" 
              size="large"
              class="status-tag"
            >
              {{ getStatusText(record.status) }}
            </el-tag>
            
            <el-button 
              v-if="record.status === 0" 
              type="primary"
              @click="handleReturn(record)"
              class="return-btn"
            >
              归还图书
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, User } from '@element-plus/icons-vue'
import { borrowApi } from '../api'

/**
 * 借阅记录组件
 * 展示用户的借阅历史和管理归还操作
 */
export default {
  name: 'BorrowView',
  components: { Loading, User },
  setup() {
    const loading = ref(false)
    const records = ref([])
    const isAdmin = computed(() => localStorage.getItem('role') === '1')
    const userId = localStorage.getItem('userId')

    // 统计数据
    const borrowedCount = computed(() => records.value.filter(r => r.status === 0).length)
    const returnedCount = computed(() => records.value.filter(r => r.status === 1).length)

    // 图书封面渐变色
    const gradients = [
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
    ]

    const getBookGradient = (index) => gradients[index % gradients.length]

    /**
     * 加载借阅记录
     */
    const loadRecords = async () => {
      loading.value = true
      try {
        const res = isAdmin.value
          ? await borrowApi.getAllRecords()
          : await borrowApi.getUserRecords(userId)
        if (res.code === 200) {
          records.value = res.data
        }
      } catch (error) {
        ElMessage.error('加载借阅记录失败')
      } finally {
        loading.value = false
      }
    }

    /**
     * 归还图书
     * @param {Object} row - 借阅记录
     */
    const handleReturn = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确定要归还《${row.bookTitle}》吗？`,
          '归还确认',
          {
            confirmButtonText: '确定归还',
            cancelButtonText: '取消'
          }
        )
        
        const res = await borrowApi.returnBook(row.userId, row.bookId)
        if (res.code === 200) {
          ElMessage.success('归还成功')
          loadRecords()
        } else {
          ElMessage.error(res.message || '归还失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('归还失败')
        }
      }
    }

    /**
     * 格式化日期
     * @param {string} dateStr - 日期字符串
     */
    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }

    /**
     * 判断是否临近到期
     * @param {Object} record - 借阅记录
     */
    const isNearDue = (record) => {
      if (record.status !== 0 || !record.dueDate) return false
      const dueDate = new Date(record.dueDate)
      const now = new Date()
      const diffDays = Math.ceil((dueDate - now) / (1000 * 60 * 60 * 24))
      return diffDays <= 3 && diffDays >= 0
    }

    /**
     * 获取状态标签类型
     * @param {number} status - 状态码
     */
    const getStatusType = (status) => {
      const types = { 0: 'warning', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    }

    /**
     * 获取状态文本
     * @param {number} status - 状态码
     */
    const getStatusText = (status) => {
      const texts = { 0: '借阅中', 1: '已归还', 2: '已逾期' }
      return texts[status] || '未知'
    }

    onMounted(loadRecords)

    return {
      loading,
      records,
      isAdmin,
      borrowedCount,
      returnedCount,
      getBookGradient,
      loadRecords,
      handleReturn,
      formatDate,
      isNearDue,
      getStatusType,
      getStatusText
    }
  }
}
</script>

<style scoped>
/* 页面容器 */
.borrow-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-info h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.header-info p {
  font-size: 14px;
  color: #86868b;
  margin: 0;
}

.header-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
}

.stat-label {
  font-size: 13px;
  color: #86868b;
  margin-top: 4px;
}

/* 记录容器 */
.records-container {
  min-height: 400px;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px;
  color: #86868b;
  gap: 16px;
}

.loading-icon {
  font-size: 48px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: #86868b;
  margin: 0 0 24px 0;
}

.browse-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.browse-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 记录列表 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 记录卡片 */
.record-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.record-card.is-overdue {
  border-left: 4px solid #ff3b30;
}

/* 图书封面 */
.record-cover {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.record-cover span {
  font-size: 24px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
}

/* 记录信息 */
.record-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.record-main {
  min-width: 200px;
}

.record-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.record-user {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #86868b;
  margin: 0;
}

/* 日期信息 */
.record-dates {
  display: flex;
  gap: 32px;
}

.date-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date-label {
  font-size: 12px;
  color: #86868b;
}

.date-value {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

.date-value.is-warning {
  color: #ff9500;
}

/* 状态和操作 */
.record-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.status-tag {
  border-radius: 8px;
  font-weight: 500;
}

.return-btn {
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.return-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 响应式适配 */
@media (max-width: 900px) {
  .record-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .record-info {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }
  
  .record-dates {
    flex-wrap: wrap;
    gap: 16px;
  }
  
  .record-actions {
    width: 100%;
    justify-content: space-between;
    margin-top: 12px;
    padding-top: 16px;
    border-top: 1px solid #f5f5f7;
  }
}
</style>
