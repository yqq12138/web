<!--<template>
  <div class="upload-page">
    <section class="upload-section">
      <h1>上传检测图片</h1>

      <input
          type="file"
          ref="fileInput"
          accept="image/*"
          @change="handleFileChange"
      />

      <button
          class="upload-button"
          :disabled="!selectedFile || uploading"
          @click="uploadImage"
      >
        {{ uploading ? '上传中...' : '开始上传' }}
      </button>
    </section>

    <div class="background-decor"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UploadPage',
  data() {
    return {
      selectedFile: null,
      uploading: false,
    }
  },
  methods: {
    handleFileChange(event) {
      const file = event.target.files[0];
      if (!file) return;

      // 验证文件类型
      if (!file.type.startsWith('image/')) {
        alert('请上传图片文件');
        return;
      }

      // 验证文件大小 (5MB限制)
      if (file.size > 5 * 1024 * 1024) {
        alert('图片大小不能超过5MB');
        return;
      }

      this.selectedFile = file;
    },
    uploadImage() {
      if (!this.selectedFile) {
        alert('请先选择图片');
        return;
      }

      this.uploading = true;
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      axios.post('/api/detect/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
          .then(response => {
            alert(`上传成功！文件路径: ${response.data.path}`);
            this.resetForm();
          })
          .catch(error => {
            console.error('上传失败:', error);
            alert(error.response?.data?.error || '上传失败');
          })
          .finally(() => {
            this.uploading = false;
          });
    },
    resetForm() {
      this.selectedFile = null;
      this.$refs.fileInput.value = null;
    }
  }
}
</script>

<style scoped>
.upload-page {
  position: absolute;
  top: 60px;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to right, #232526, #414345);
  color: #fff;
  overflow: auto;
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-section {
  max-width: 600px;
  width: 100%;
  text-align: center;
  background: rgba(0,0,0,0.3);
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 234, 255, 0.1);
}

.upload-section h1 {
  font-size: 40px;
  margin-bottom: 30px;
  text-shadow: 1px 1px 4px #000;
}

input[type="file"] {
  margin-bottom: 30px;
  color: #e0f7ff;
  cursor: pointer;
}

.upload-button {
  padding: 12px 30px;
  background: linear-gradient(135deg, #00eaff, #0072ff);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: bold;
  font-size: 18px;
  cursor: pointer;
  transition: 0.3s ease;
}

.upload-button:disabled {
  background: #555;
  cursor: not-allowed;
}

.upload-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 0 10px #00eaffaa;
}

.background-decor {
  position: absolute;
  bottom: -100px;
  right: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle at center, rgba(0, 234, 255, 0.3), transparent);
  filter: blur(120px);
  pointer-events: none;
  user-select: none;
}
</style>-->
