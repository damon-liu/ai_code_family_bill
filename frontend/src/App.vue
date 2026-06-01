<template>
  <div class="shell">
    <!-- Masthead / 报头 -->
    <header class="masthead">
      <div class="masthead-inner">
        <div class="brand">
          <div class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 40 40" fill="none">
              <circle cx="20" cy="20" r="18" stroke="currentColor" stroke-width="1.2" />
              <path d="M10 28V12h6c3 0 5 1.8 5 4.5S19 21 16 21h-6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
              <path d="M22 28l8-16" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" />
            </svg>
          </div>
          <div class="brand-text">
            <div class="brand-overline">No. {{ issueNo }} · 私家账册</div>
            <h1 class="brand-title">
              <span class="brand-cn">家庭账册</span>
              <span class="brand-en">The Family Ledger</span>
            </h1>
          </div>
        </div>

        <div class="masthead-meta">
          <div class="meta-block">
            <span class="meta-label">本期</span>
            <span class="meta-value">{{ todayLabel }}</span>
          </div>
          <div class="meta-rule" aria-hidden="true"></div>
          <div class="meta-block">
            <span class="meta-label">栏目</span>
            <span class="meta-value">流水 · Statement</span>
          </div>
          <div class="meta-rule" aria-hidden="true"></div>
          <div class="meta-block">
            <span class="meta-label">主笔</span>
            <span class="meta-value">户主 · 家人</span>
          </div>
        </div>
      </div>
      <div class="rule-double" aria-hidden="true">
        <span></span><span></span>
      </div>
    </header>

    <main class="page">
      <router-view />
    </main>

    <footer class="footstamp">
      <span>© Ledger · {{ year }}</span>
      <span class="dot">·</span>
      <span>记录琐碎，亦是家的注脚</span>
      <span class="dot">·</span>
      <span>Vol. {{ year }}</span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import dayjs from 'dayjs'

const now = dayjs()
const year = now.year()
const issueNo = computed(() => String(now.dayOfYear?.() ?? Math.ceil((now.valueOf() - dayjs(`${year}-01-01`).valueOf()) / 86400000)).padStart(3, '0'))
const todayLabel = computed(() => now.format('YYYY · MM · DD'))
</script>

<style scoped>
.shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.masthead {
  padding: 36px 48px 0;
  color: var(--ink);
}

.masthead-inner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 40px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 18px;
}

.brand-mark {
  width: 56px;
  height: 56px;
  color: var(--ink);
  border: 1px solid var(--ink);
  border-radius: 50%;
  display: grid;
  place-items: center;
  padding: 6px;
  background: var(--paper-tint);
  box-shadow: inset 0 0 0 4px var(--paper-tint), 0 0 0 1px var(--ink);
}

.brand-mark svg {
  width: 100%;
  height: 100%;
}

.brand-overline {
  font-family: var(--font-sans);
  font-size: 11px;
  letter-spacing: 0.34em;
  text-transform: uppercase;
  color: var(--ink-3);
  margin-bottom: 4px;
}

.brand-title {
  display: flex;
  align-items: baseline;
  gap: 14px;
  line-height: 1;
}

.brand-cn {
  font-family: var(--font-serif);
  font-weight: 600;
  font-size: 38px;
  letter-spacing: 0.06em;
  color: var(--ink);
}

.brand-en {
  font-family: var(--font-serif);
  font-style: italic;
  font-weight: 300;
  font-size: 18px;
  color: var(--ink-3);
  letter-spacing: 0.02em;
}

.masthead-meta {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-bottom: 6px;
}

.meta-block {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 10px;
  letter-spacing: 0.28em;
  text-transform: uppercase;
  color: var(--ink-mute);
}

.meta-value {
  font-family: var(--font-serif);
  font-size: 14px;
  color: var(--ink-2);
  letter-spacing: 0.04em;
}

.meta-rule {
  width: 1px;
  height: 26px;
  background: var(--rule);
}

.rule-double {
  max-width: 1400px;
  margin: 24px auto 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.rule-double span {
  display: block;
  height: 1px;
  background: var(--ink);
}

.rule-double span:last-child {
  height: 2px;
}

.page {
  flex: 1;
  padding: 36px 48px 60px;
}

.footstamp {
  padding: 22px 48px 34px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-serif);
  font-style: italic;
  font-size: 13px;
  color: var(--ink-mute);
}

.dot {
  color: var(--brass);
}

@media (max-width: 880px) {
  .masthead {
    padding: 24px 22px 0;
  }
  .masthead-inner {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
  }
  .brand-cn {
    font-size: 30px;
  }
  .brand-en {
    font-size: 15px;
  }
  .page {
    padding: 24px 22px 60px;
  }
  .footstamp {
    padding: 16px 22px 28px;
    flex-wrap: wrap;
  }
}
</style>
