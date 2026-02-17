<template>
  <div class="books-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button v-if="isAdmin" type="primary" @click="showAddDialog">添加图书</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="书名/作者/ISBN" clearable style="width: 200px;" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable style="width: 200px;">
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleBorrow(row)" :disabled="row.stock <= 0">借阅</el-button>
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
          <el-input v-model="bookForm.category" />
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
import { bookApi, borrowApi } from '../api'

export default {
  name: 'BooksView',
  setup() {
    const loading = ref(false)
    const saving = ref(false)
    const books = ref([])
    const categories = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('添加图书')
    const bookFormRef = ref(null)
    const isAdmin = computed(() => localStorage.getItem('role') === '1')
    const userId = localStorage.getItem('userId')

    const searchForm = reactive({ keyword: '', category: '' })
    const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
    const bookForm = reactive({
      id: null, title: '', author: '', isbn: '', publisher: '',
      category: '', price: null, stock: 0, description: ''
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
        const res = await bookApi.getCategories()
        if (res.code === 200) categories.value = res.data
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    const handleSearch = () => { pagination.pageNum = 1; loadBooks() }
    const resetSearch = () => { searchForm.keyword = ''; searchForm.category = ''; handleSearch() }

    const showAddDialog = () => {
      dialogTitle.value = '添加图书'
      Object.assign(bookForm, { id: null, title: '', author: '', isbn: '', publisher: '', category: '', price: null, stock: 0, description: '' })
      dialogVisible.value = true
    }

    const showEditDialog = (row) => {
      dialogTitle.value = '编辑图书'
      Object.assign(bookForm, row)
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
        const res = await borrowApi.borrow(userId, row.id)
        if (res.code === 200) { ElMessage.success('借阅成功'); loadBooks() }
        else ElMessage.error(res.message || '借阅失败')
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('借阅失败')
      }
    }

    onMounted(() => { loadBooks(); loadCategories() })

    return {
      loading, saving, books, categories, searchForm, pagination, dialogVisible, dialogTitle,
      bookForm, bookRules, bookFormRef, isAdmin, loadBooks, handleSearch, resetSearch,
      showAddDialog, showEditDialog, handleSave, handleDelete, handleBorrow
    }
  }
}
</script>

<style scoped>
.books-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
</style>
