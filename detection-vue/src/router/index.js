import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import HomePage from '../views/HomePage.vue'
import DetectionPage from '../views/DetectPage.vue'
import HistoryPage from '../views/HistoryPage.vue'
import ResultDetail from '../views/ResultDetail.vue'
import ChatWithAI from '../views/ChatWithAI.vue'

const routes = [
    {
        path: '/',
        component: MainLayout,
        children: [
            { path: '', name: 'Home', component: HomePage },
            { path: 'chat', name: 'chat', component: ChatWithAI },
            { path: 'detect', name: 'Detect', component: DetectionPage },
            { path: 'history', name: 'History', component: HistoryPage },
            { path: 'result/:id', name: 'ResultDetail', component: ResultDetail }
]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
