/*vue-router 配置*/

import Vue from 'vue'
import Router from 'vue-router'
import store from '../store'
import Main from '../pages/Main'
import MaterialMain from '../pages/material/MaterialMain'
import LogsMain from '../pages/logs/LogsMain'
import LogsModule from '../pages/logs/details/TableModule'
import TasksMain from '../pages/tasks/TaskMain'
import RobotMain from '../pages/robot/RobotMain'
import Login from '../pages/user/Login'
import UserConfig from '../pages/user/UserConfig'
import IoMain from '../pages/io/IoMain'
import IoNow from '../pages/io/details/IoNow'
import IoPreview from '../pages/io/details/IoPreview'



Vue.use(Router);

const router = new Router({
  routes: [
    {
      path: '/_empty'
    },

    {
      path: '/',
      component: Main,
      redirect: '/material',
      meta: {
        requireAuth: true
      },
      children: [
        {
          path: '/material',
          name: 'Material',
          component: MaterialMain,
          children: [

          ]
        },
        {
          path: 'logs',
          component: LogsMain,
          children: [
            {
              path: 'action_log',
              component: LogsModule
            },
            {
              path: 'task_log',
              component: LogsModule
            },
            {
              path: 'position_log',
              component: LogsModule
            }
          ]
        },
        {
          path: 'tasks',
          name: 'Tasks',
          component: TasksMain,
        },
        {
          path: 'robot',
          name: 'Robot',
          component: RobotMain
        },
        {
          path: 'user',
          name: 'UserConfig',
          component: UserConfig
        },
        {
          path: 'io',
          name: 'IoMain',
          component: IoMain,
          children: [
            {
              path: 'now',
              component: IoNow
            },
            {
              path: 'preview',
              component: IoPreview
            }
          ]
        }
      ]
    },

    {
      path: '/login',
      name: 'Login',
      component: Login
    }
  ]
});

if (localStorage.getItem('token')) {
  store.commit('setLoginToken', localStorage.getItem('token'))
}


router.beforeEach((to, from, next) => {
  for (let index in window.g.ROBOT_INTERVAL){
    clearInterval(window.g.ROBOT_INTERVAL[index])
  }
  if (to.matched.some(r => r.meta.requireAuth)) {
    if (store.state.token) {
      next();
    } else {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    }
  } else {
    next();
  }
});

export default router;
