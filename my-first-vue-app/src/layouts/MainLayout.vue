<template>
  <div>
    <Navbar />
    <main class="main-container">
      <router-view />
    </main>
  </div>
</template>

<script>
import Navbar from '../components/AppNavbar.vue'

export default {
  components: { Navbar }
}
</script>

<style scoped>
.main-container {
  padding: 20px;
  margin-top: 100px; /* 避免被导航栏遮住 */
}
</style>
