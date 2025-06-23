const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 👈 你的 Spring Boot 后端地址
        changeOrigin: true,
        pathRewrite: {
          '^/api': '' // 👈 请求时去掉 `/api` 前缀
        }
      }
    }
  }
})
