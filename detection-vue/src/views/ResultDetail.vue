<template>
  <div class="detail-page">
    <div v-if="result" class="result-box">
      <h2>📋 检测详情：</h2>
      <p><strong>部件名称：</strong>{{ result.partName }}</p>
      <p><strong>检测时间：</strong>{{ result.detectTime }}</p>
      <p><strong>检测结果：</strong>{{ result.result }}</p>
      <div class="image-container" v-if="result.imagePath || result.markedImage">
        <div class="image-section" v-if="result.imagePath">
          <p><strong>原图：</strong></p>
          <img
              :src="result.imagePath"
              alt="原图"
              class="image-side"
          />
        </div>
        <div class="image-section" v-if="result.markedImage">
          <p><strong>标注对比图：</strong></p>
          <img
              :src="result.markedImage"
              alt="标注图"
              class="image-side"
          />
        </div>
      </div>


      <button class="back-btn" @click="goBack">⬅ 返回历史记录</button>
    </div>
    <div v-else class="waiting">
      🔍 正在加载检测详情...
    </div>
    <div class="background-decor"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ResultDetail',
  data() {
    return {
      result: null
    }
  },
  methods: {
    fetchDetail(id) {
      axios.get(`/api/detect/history/${id}`)
          .then(res => {
            this.result = res.data
          })
          .catch(err => {
            console.error('获取检测详情失败:', err)
          })
    },
    goBack() {
      this.$router.push({name: 'History'})
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      this.fetchDetail(id)
    } else {
      console.warn('未传入检测记录ID，无法获取详情')
    }
  }
}
</script>

<style scoped>
.detail-page {
  position: absolute;
  top: 60px; /* 避开导航栏 */
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, #232526, #414345);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  box-sizing: border-box;
  overflow: auto;
}

.result-box {
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid #00eaff55;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(0, 234, 255, 0.1);
  text-align: left;
  max-width: 600px;
  width: 100%;
}

.result-box h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #00eaff;
}

.result-box p {
  font-size: 18px;
  margin: 10px 0;
  line-height: 1.5;
  color: #e0f7ff;
}

.back-btn {
  margin-top: 30px;
  padding: 10px 20px;
  background-color: #00eaffaa;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  color: #000;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.back-btn:hover {
  background-color: #00d4ff;
}

.waiting {
  font-size: 20px;
  color: #ccc;
  font-style: italic;
  text-align: center;
}

.background-decor {
  position: absolute;
  bottom: -100px;
  left: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle at center, rgba(0, 234, 255, 0.3), transparent);
  filter: blur(120px);
  pointer-events: none;
  user-select: none;
}
.image-container {
  display: flex;
  gap: 20px; /* 图片间距 */
  margin-top: 20px;
  justify-content: center;
}

.image-section {
  flex: 1; /* 两边等分 */
  text-align: center;
}

.image-section p {
  color: #00eaff;
  font-weight: 600;
  margin-bottom: 8px;
}

.image-side {
  max-width: 100%;
  height: auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 0 8px rgba(0, 234, 255, 0.3);
}

</style>
