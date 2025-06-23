<template>
  <div class="home-page">
    <section class="intro">
      <h1>欢迎使用智能工业异常检测平台</h1>
      <p>
        本平台利用深度学习和大模型技术，实现对工业零部件图像的异常识别与缺陷定位，
        提供高效、精准、可视化的检测能力，助力智能制造升级。
      </p>
      <router-link to="/chat" class="cta-button">立即开始检测</router-link>
    </section>
    <div class="background-decor"></div>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped>
/* 🧩 主体内容紧贴导航栏下方且铺满整个剩余区域 */
.home-page {
  position: absolute;
  top: 60px; /* 避开 navbar 的高度 */
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to right, #232526, #414345);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

/* 🎯 中间介绍文字居中 */
.intro {
  max-width: 800px;
  text-align: center;
  padding: 20px;
  box-sizing: border-box;
}

.intro h1 {
  font-size: 48px;
  margin-bottom: 20px;
  text-shadow: 1px 1px 4px #000;
}

.intro p {
  font-size: 18px;
  line-height: 1.6;
  margin-bottom: 40px;
  color: #ddd;
}

.cta-button {
  display: inline-block;
  padding: 14px 32px;
  background: linear-gradient(135deg, #00eaff, #0072ff);
  border-radius: 30px;
  color: #fff;
  font-weight: bold;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.cta-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.4);
}

/* 🌌 背景光影装饰 */
.background-decor {
  position: absolute;
  top: 0;
  right: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle at center, rgba(0, 234, 255, 0.4), transparent);
  filter: blur(120px);
  pointer-events: none;
  user-select: none;
}
</style>
