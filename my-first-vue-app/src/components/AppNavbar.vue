<template>
  <nav class="navbar">
    <div class="logo">🛠 工业异常检测平台</div>
    <div class="nav-links">
      <router-link to="/" exact-active-class="active" exact>主页</router-link>
      <router-link to="/chat" exact-active-class="active" exact>聊天功能</router-link>
      <router-link to="/detect" active-class="active">检测页</router-link>
      <router-link to="/history" active-class="active">历史记录</router-link>
<!--      <router-link to="/upload" active-class="active">上传图片</router-link>-->
    </div>
  </nav>
</template>

<script>
export default {
  name: "AppNavbar"
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: saturate(180%) blur(12px);
  box-shadow: 0 4px 10px rgba(0, 234, 255, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
  color: #00eaff;
  font-weight: 600;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  z-index: 1000;
  user-select: none;
  overflow: hidden;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 1.2px;
  text-shadow: 0 0 10px #00eaffaa;
  white-space: nowrap;
  flex-shrink: 0;
}

.nav-links {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  justify-content: flex-end;
  overflow: hidden;
}

.nav-links a {
  position: relative;
  color: #ccefff;
  text-decoration: none;
  font-size: 15px;
  white-space: nowrap;
  padding: 5px 0;
  transition: color 0.3s ease, text-shadow 0.3s ease;
}

.nav-links a::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: -6px;
  width: 0%;
  height: 2.5px;
  background: linear-gradient(90deg, #00eaff, #0072ff);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.nav-links a:hover {
  color: #00eaff;
  text-shadow: 0 0 12px #00eaffbb;
}

.nav-links a:hover::after {
  width: 100%;
}

.nav-links a.active {
  color: #00ccff;
  font-weight: 700;
  text-shadow: 0 0 14px #00ccffcc;
}

.nav-links a.active::after {
  width: 100%;
  background: linear-gradient(90deg, #00ccff, #0099ff);
  box-shadow: 0 0 12px #00ccffcc;
}

/* 🔁 小屏兜底策略 */
@media screen and (max-width: 768px) {
  .navbar {
    padding: 0 10px;
  }
  .logo {
    font-size: 16px;
  }
  .nav-links a {
    font-size: 14px;
  }
}
</style>
