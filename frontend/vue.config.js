const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8416',
        changeOrigin: true
      }
    },
    client: {
      overlay: false
    }
  }
})
