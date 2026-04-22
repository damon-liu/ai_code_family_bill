<template>
  <el-container>
    <el-header>
      <div class="header-content">
        <h1>家庭记账系统</h1>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
</script>

<style scoped>
.el-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content h1 {
  font-size: 24px;
  font-weight: 500;
}

.el-main {
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>


