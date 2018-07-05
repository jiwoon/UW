<!--表单查看页面的统一侧边栏导航-->

<template>
  <div class="mt-3 mb-3">
    <nav>
      <div class="sidebar-items">
        <!--透传数据-->
        <div class="sidebar-title">
          <a class="subtitle" draggable="false">日志详情</a>
        </div>
        <div>
          <div @click="toggleState('task_log')">
            <div class="sidebar-link" @click="linkTo('task_log')"
               :class="activeItem === 'task_log' ? 'active' : ''">任务日志</div>
          </div>
          <div @click="toggleState('action_log')">
            <div class="sidebar-link" @click="linkTo('action_log')"
               :class="activeItem === 'action_log' ? 'active' : ''">接口调用日志</div>
          </div>
          <div @click="toggleState('position_log')">
            <div class="sidebar-link"  @click="linkTo('position_log')"
               :class="activeItem === 'position_log' ? 'active' : ''">物料位置转移日志</div>
          </div>
        </div>
      </div>
    </nav>

  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex'

  export default {
    data() {
      return {


        //控制列表active状态，当前已激活的项目
        activeItem: ""

      }
    },
    mounted: function () {

    },
    computed: {
      ...mapGetters([
        'tableRouterApi',
        'isLoading'
      ]),
    },
    methods: {
      ...mapActions(['setTableRouter', 'setLoading']),
      toggleState: function (item) {
        this.activeItem = item;

      },
      linkTo: function (obj) {
        //this.setLoading(true);
        this.$router.replace('/_empty');
        this.$router.push({
          path: '/logs/' + obj,
          query: {
            page: obj
          }
        })
      }
    }


  }
</script>

<style scoped>
  a {
    text-decoration: none;
    color: #000;
  }

  .sidebar {
  }

  .sidebar-items {
    /*border: 1px solid #eeeeee;*/
    /*border-top: none;*/
    /*border-bottom: none;*/
    border: none;
    height: 100%;
    /*border-radius: 8px;*/
  }

  .sidebar-title {
    height: 2em;
    line-height: 2em;
    font-size: 1.2em;
    font-weight: 500;
    padding-left: 0.5em;
    border-bottom: 1px solid #eeeeee;
    background-color: #458aff;
    color: #fff;
    border-radius: 8px;
  }

  .sidebar-title a {
    color: #fff;
  }

  .sidebar-link {
    text-decoration: none;
    display: block;
    height: 2em;
    line-height: 2em;
    padding-left: 1.4em;
    font-size: 1em;
    border-bottom: 1px solid #eeeeee;
    font-weight: normal;
    background: #fff;
    cursor: pointer;
  }

  .sidebar-link:hover {
    background-color: #8bdaff;
    color: #fff;
    border-radius: 5px;
  }

  .sidebar-items .active {
    background-color: #7bbfff;
    box-shadow: 2px 4px 10px 1px #e5e7eb;
    color: #fff;
    border-radius: 5px;
  }

  .subtitle {
    cursor: default;
    display: block;
    width: 100%;
    height: 100%;
  }
</style>
