<template>
  <div class="books-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button v-if="isAdmin" type="primary" @click="showAddDialog">添加图书</el-button>
        </div>
      </template>

      <!-- 搜索表单 - 支持单条件与多条件查询 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="搜索方式">
          <el-radio-group v-model="searchMode" @change="resetSearch">
            <el-radio-button label="keyword">综合搜索</el-radio-button>
            <el-radio-button label="single">精确搜索</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <template v-if="searchMode === 'keyword'">
          <el-form-item label="关键词">
            <el-input v-model="searchForm.keyword" placeholder="书名/作者/ISBN" clearable style="width: 220px;" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="书名">
            <el-input v-model="searchForm.title" placeholder="按书名搜索" clearable style="width: 160px;" />
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="searchForm.author" placeholder="按作者搜索" clearable style="width: 160px;" />
          </el-form-item>
          <el-form-item label="ISBN">
            <el-input v-model="searchForm.isbn" placeholder="按ISBN搜索" clearable style="width: 160px;" />
          </el-form-item>
        </template>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable style="width: 160px;">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 图书表格 -->
      <el-table :data="books" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="isbn" label="ISBN" width="200" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="80">
          <template #default="{ row }">{{ row.price ? `¥${row.price}` : '-' }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="70" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="isBorrowed(row.id)" size="small" type="info" disabled style="width: 52px;">已借阅</el-button>
            <el-button v-else size="small" type="success" @click="handleBorrow(row)" :disabled="row.stock <= 0" style="width: 52px;">借阅</el-button>
            <el-button v-if="isAdmin" size="small" type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button v-if="isAdmin" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadBooks"
        @current-change="loadBooks"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="100px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="bookForm.category" placeholder="请选择分类" filterable allow-create style="width: 100%;">
            <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <img v-if="bookForm.coverImage" :src="bookForm.coverImage" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div v-if="uploading" class="upload-tip">上传中...</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { bookApi, borrowApi, fileApi, categoryApi } from '../api'

export default {
  name: 'BooksView',
  components: { Plus },
  setup() {
    const loading = ref(false)
    const saving = ref(false)
    const uploading = ref(false)
    const books = ref([])
    const categories = ref([])
    const borrowedBookIds = ref(new Set())
    const dialogVisible = ref(false)
    const dialogTitle = ref('添加图书')
    const bookFormRef = ref(null)
    const isAdmin = computed(() => localStorage.getItem('role') === '1')

    const searchForm = reactive({ keyword: '', category: '', title: '', author: '', isbn: '' })
    const searchMode = ref('keyword')
    const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
    const bookForm = reactive({
      id: null, title: '', author: '', isbn: '', publisher: '',
      category: '', price: null, stock: 0, description: '', coverImage: ''
    })
    const bookRules = {
      title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
      author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
      isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
      category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
      stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
    }

    const loadBooks = async () => {
      loading.value = true
      try {
        const res = await bookApi.getBooks({
          ...searchForm, pageNum: pagination.pageNum, pageSize: pagination.pageSize
        })
        if (res.code === 200) {
          books.value = res.data.records
          pagination.total = res.data.total
        }
      } catch (error) {
        ElMessage.error('加载图书列表失败')
      } finally {
        loading.value = false
      }
    }

    const loadCategories = async () => {
      try {
        const res = await categoryApi.list()
        if (res.code === 200) categories.value = res.data.map(c => c.name)
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    const handleSearch = () => { pagination.pageNum = 1; loadBooks() }
    const resetSearch = () => {
      searchForm.keyword = ''; searchForm.category = '';
      searchForm.title = ''; searchForm.author = ''; searchForm.isbn = '';
      handleSearch()
    }

    const loadUserBorrows = async () => {
      try {
        const res = await borrowApi.getMyRecords()
        if (res.code === 200) {
          borrowedBookIds.value = new Set(
            res.data.filter(r => r.status === 0).map(r => r.bookId)
          )
        }
      } catch (error) {
        console.error('加载借阅记录失败:', error)
      }
    }

    const isBorrowed = (bookId) => borrowedBookIds.value.has(bookId)

    const showAddDialog = () => {
      dialogTitle.value = '添加图书'
      Object.assign(bookForm, { id: null, title: '', author: '', isbn: '', publisher: '', category: '', price: null, stock: 0, description: '', coverImage: '' })
      dialogVisible.value = true
    }

    const showEditDialog = (row) => {
      dialogTitle.value = '编辑图书'
      Object.assign(bookForm, { 
        id: row.id, 
        title: row.title, 
        author: row.author, 
        isbn: row.isbn, 
        publisher: row.publisher || '', 
        category: row.category, 
        price: row.price, 
        stock: row.stock, 
        description: row.description || '', 
        coverImage: row.coverImage || '' 
      })
      dialogVisible.value = true
    }

    const handleSave = async () => {
      const valid = await bookFormRef.value.validate().catch(() => false)
      if (!valid) return
      saving.value = true
      try {
        const res = bookForm.id ? await bookApi.updateBook(bookForm.id, bookForm) : await bookApi.addBook(bookForm)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          dialogVisible.value = false
          loadBooks()
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确定要删除《${row.title}》吗？`, '删除确认', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' })
        const res = await bookApi.deleteBook(row.id)
        if (res.code === 200) { 
          ElMessage.success('删除成功')
          loadBooks() 
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('删除失败')
      }
    }

    const handleBorrow = async (row) => {
      try {
        await ElMessageBox.confirm(`确定要借阅《${row.title}》吗？`, '借阅确认', { confirmButtonText: '确定', cancelButtonText: '取消' })
        const res = await borrowApi.borrow(row.id)
        if (res.code === 200) { 
          ElMessage.success('借阅成功')
          loadBooks()
          loadUserBorrows()
        }
        else ElMessage.error(res.message || '借阅失败')
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('借阅失败')
      }
    }

    const handleCoverUpload = async (options) => {
      // 检查文件大小（限制 1MB）
      if (options.file.size > 1024 * 1024) {
        ElMessage.warning('图片大小不能超过 1MB')
        return
      }
      uploading.value = true
      try {
        const res = await fileApi.upload(options.file)
        if (res.code === 200) {
          bookForm.coverImage = res.data.url
          ElMessage.success('封面上传成功')
        } else {
          ElMessage.error(res.message || '上传失败')
        }
      } catch (error) {
        ElMessage.error('上传失败')
      } finally {
        uploading.value = false
      }
    }

    onMounted(() => { loadBooks(); loadCategories(); loadUserBorrows() })

    return {
      loading, saving, uploading, books, categories, searchForm, searchMode, pagination, dialogVisible, dialogTitle,
      bookForm, bookRules, bookFormRef, isAdmin, loadBooks, handleSearch, resetSearch,
      showAddDialog, showEditDialog, handleSave, handleDelete, handleBorrow, handleCoverUpload, isBorrowed
    }
  }
}
</script>

<style scoped>
.books-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.cover-uploader :deep(.el-upload) { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; width: 120px; height: 160px; display: flex; align-items: center; justify-content: center; overflow: hidden; }
.cover-uploader :deep(.el-upload:hover) { border-color: #409eff; }
.cover-uploader-icon { font-size: 28px; color: #8c939d; }
.cover-preview { width: 120px; height: 160px; object-fit: cover; display: block; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 5px; }
</style>
