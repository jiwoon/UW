<template>
  <div class="user-options form-row">
    <div class="btn pl-1 pr-1" title="详细" @click="checkTaskDetails(row)">
      <icon name="list" scale="1.8"></icon>
    </div>
    <div class="btn pl-1 pr-1" title="状态" @click="isEditing = true">
      <icon name="menu" scale="1.8"></icon>
    </div>
    <div v-if="isEditing" id="edit-window">
      <edit-status :editData="row"/>
    </div>

  </div>
</template>

<script>
  import EditStatus from './EditStatus'
  import eventBus from '@/utils/eventBus'
  import {mapActions, mapGetters} from 'vuex'

  export default {
    name: "OperationOptions",
    props: ['row'],
    components: {
      EditStatus
    },
    data() {
      return {
        isEditing: false
      }
    },
    mounted() {
      eventBus.$on('closeTaskStatusPanel', () => {
        this.isEditing = false;
      })
    },
    computed: {
      //...mapGetters['isDetailsActive']
    },
    methods: {
      ...mapActions(['setTaskActiveState', 'setTaskData', 'setLoading']),
      checkTaskDetails: function (val) {
        this.setLoading(true);
        this.setTaskActiveState(true);
        this.setTaskData('');
        this.setTaskData(val.id)
      }
    }
  }
</script>

<style scoped>
  #edit-window {
    z-index: 100;
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
