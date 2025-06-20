<template>
  <div class="detail-page">
    <div v-if="result" class="result-box">
      <h2>📋 最新检测信息：</h2>
      <p><strong>部件名称：</strong>{{ result.partName }}</p>
      <p><strong>检测时间：</strong>{{ result.detectTime }}</p>
      <p><strong>检测结果：</strong>{{ result.result }}</p>
      <div class="image-section" v-if="result.markedImage">
        <p><strong>标注对比图：</strong></p>
        <img :src="result.markedImage" alt="标注图" style="max-width: 100%; border: 1px solid #ccc; border-radius: 8px;" />
      </div>
    </div>
    <div v-else class="waiting">
      🔍 正在获取最新检测数据...
    </div>
    <div class="background-decor"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'DetectPage',
  data() {
    return {
      result: null,
      timer: null,
      lastDetectTime: null // ⏰ 缓存上一次的 detect_time
    }
  },
  methods: {
    fetchLatestResult() {
      axios.get('/api/detect/latest')
          .then(res => {
            const newResult = res.data
            const newTime = new Date(newResult.detectTime || newResult.time)

            // 🧠 只在 detect_time 更晚时才更新
            if (!this.lastDetectTime || newTime > new Date(this.lastDetectTime)) {
              this.result = newResult
              this.lastDetectTime = newResult.detectTime || newResult.time
            }
          })
          .catch(err => {
            console.error('获取最新检测数据失败：', err)
          })
    }
  },
  created() {
    this.fetchLatestResult()
    this.timer = setInterval(this.fetchLatestResult, 10000) // 每 10 秒轮询一次
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer)
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
</style>
