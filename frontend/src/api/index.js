/**
 * API接口模块
 * 封装所有与后端的HTTP请求
 */
import axios from 'axios'

// 创建axios实例，配置基础URL和超时时间
const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器 - 自动添加认证token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器 - 统一处理响应和错误
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    console.error('接口请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 认证相关接口
 */
export const authApi = {
  // 用户登录
  login: (data) => api.post('/auth/login', data),
  // 用户注册
  register: (data) => api.post('/auth/register', data)
}

/**
 * 图书相关接口
 */
export const bookApi = {
  // 分页查询图书列表
  getBooks: (params) => api.get('/books', { params }),
  // 获取图书详情
  getBook: (id) => api.get(`/books/${id}`),
  // 添加新图书
  addBook: (data) => api.post('/books', data),
  // 更新图书信息
  updateBook: (id, data) => api.put(`/books/${id}`, data),
  // 删除图书
  deleteBook: (id) => api.delete(`/books/${id}`),
  // 获取所有分类
  getCategories: () => api.get('/books/categories'),
  // 获取最新上架图书
  getLatest: () => api.get('/books/latest')
}

/**
 * 借阅相关接口
 */
export const borrowApi = {
  // 借阅图书（userId从token中获取）
  borrow: (bookId) => api.post('/borrow/borrow', null, { params: { bookId } }),
  // 归还图书（userId从token中获取）
  returnBook: (bookId) => api.post('/borrow/return', null, { params: { bookId } }),
  // 获取当前用户借阅记录
  getMyRecords: () => api.get('/borrow/my'),
  // 获取所有借阅记录（管理员）
  getAllRecords: () => api.get('/borrow/all')
}

/**
 * 分类管理接口
 */
export const categoryApi = {
  // 获取所有分类
  list: () => api.get('/categories'),
  // 添加分类
  add: (data) => api.post('/categories', data),
  // 更新分类
  update: (id, data) => api.put(`/categories/${id}`, data),
  // 删除分类
  delete: (id) => api.delete(`/categories/${id}`)
}

/**
 * 仪表盘相关接口
 */
export const dashboardApi = {
  // 获取统计数据
  getStats: () => api.get('/dashboard/stats')
}

/**
 * 文件上传接口
 */
export const fileApi = {
  // 上传文件
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default api
