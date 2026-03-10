<template>
  <div class="categories-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="showAddDialog">添加分类</el-button>
        </div>
      </template>

      <el-table :data="categories" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">{{ row.description || '-' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="bookCount" label="图书数量" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ getBookCount(row.name) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入分类描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi, bookApi } from '../api'

export default {
  name: 'CategoriesView',
  setup() {
    const loading = ref(false)
    const saving = ref(false)
    const categories = ref([])
    const bookCounts = ref({})
    const dialogVisible = ref(false)
    const dialogTitle = ref('添加分类')
    const formRef = ref(null)

    const form = reactive({ id: null, name: '', description: '', sort: 0 })
    const rules = {
      name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
    }

    const loadCategories = async () => {
      loading.value = true
      try {
        const [catRes, booksRes] = await Promise.all([
          categoryApi.list(),
          bookApi.getBooks({ pageSize: 9999 })
        ])
        if (catRes.code === 200) categories.value = catRes.data
        if (booksRes.code === 200) {
          const counts = {}
          booksRes.data.records.forEach(b => {
            if (b.category) counts[b.category] = (counts[b.category] || 0) + 1
          })
          bookCounts.value = counts
        }
      } catch (error) {
        ElMessage.error('加载分类失败')
      } finally {
        loading.value = false
      }
    }

    const getBookCount = (name) => bookCounts.value[name] || 0

    const showAddDialog = () => {
      dialogTitle.value = '添加分类'
      Object.assign(form, { id: null, name: '', description: '', sort: 0 })
      dialogVisible.value = true
    }

    const showEditDialog = (row) => {
      dialogTitle.value = '编辑分类'
      Object.assign(form, { id: row.id, name: row.name, description: row.description || '', sort: row.sort || 0 })
      dialogVisible.value = true
    }

    const handleSave = async () => {
      const valid = await formRef.value.validate().catch(() => false)
      if (!valid) return
      saving.value = true
      try {
        const res = form.id
          ? await categoryApi.update(form.id, form)
          : await categoryApi.add(form)
        if (res.code === 200) {
          ElMessage.success('保存成功')
          dialogVisible.value = false
          loadCategories()
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
        await ElMessageBox.confirm(`确定要删除分类「${row.name}」吗？`, '删除确认', {
          type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消'
        })
        const res = await categoryApi.delete(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          loadCategories()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') ElMessage.error('删除失败')
      }
    }

    onMounted(loadCategories)

    return {
      loading, saving, categories, dialogVisible, dialogTitle,
      form, rules, formRef, getBookCount,
      showAddDialog, showEditDialog, handleSave, handleDelete
    }
  }
}
</script>

<style scoped>
.categories-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
