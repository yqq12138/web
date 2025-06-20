<template>
  <div class="history-page">
    <section class="history-section">
      <h1>历史检测记录</h1>

      <div v-if="history.length === 0" class="no-history">
        暂无检测记录，请先上传并检测图片。
      </div>

      <div v-else class="record-list">
        <div class="record-item" v-for="record in history" :key="record.id">
          <div class="record-info">
            <p><strong>部件：</strong>{{ record.partName }}</p>
            <p><strong>检测时间：</strong>{{ record.detectTime }}</p>
            <p><strong>结果：</strong>{{ record.result }}</p>
          </div>
          <button class="detail-button" @click="goToDetail(record.id)">了解详情</button>
        </div>
      </div>
    </section>

    <div class="background-decor"></div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HistoryPage',
  data() {
    return {
      history: []
    }
  },
  created() {
    this.fetchHistory()
  },
  methods: {
    fetchHistory() {
      axios.get('/api/detect/history')
          .then(res => {
            this.history = res.data
          })
          .catch(err => {
            console.error('获取历史失败', err)

          })
    },
    goToDetail(id) {
      this.$router.push({ name: 'ResultDetail', params: { id } })
    }
  }
}
</script>

<style scoped>
.history-page {
  position: absolute;
  top: 60px;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to right, #232526, #414345);
  color: #fff;
  overflow: auto;
  padding: 40px 20px;
}

.history-section {
  max-width: 1000px;
  margin: auto;
  text-align: center;
}

.history-section h1 {
  font-size: 40px;
  margin-bottom: 30px;
  text-shadow: 1px 1px 4px #000;
}

.no-history {
  color: #ccc;
  font-size: 20px;
  font-style: italic;
  margin-top: 100px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid #00eaff33;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px #00eaff22;
}

.record-info p {
  margin: 4px 0;
  color: #e0f7ff;
  text-align: left;
}

.detail-button {
  padding: 10px 20px;
  background: linear-gradient(135deg, #00eaff, #0072ff);
  border: none;
  border-radius: 20px;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s ease;
}

.detail-button:hover {
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
</style>
