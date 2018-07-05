<template>
  <div class="user-options form-row">
    <div class="btn pl-1 pr-1" title="详细" @click="checkMaterialDetails(row)">
      <icon name="list" scale="1.8"></icon>
    </div>
    <div class="btn pl-1 pr-1" title="更新" @click="isEditing = true">
      <icon name="edit" scale="1.8"></icon>
    </div>
    <div v-if="isEditing" id="edit-window">
      <edit-material :editData="row"/>
    </div>

  </div>
</template>

<script>
  import EditMaterial from './EditMaterial'
  import eventBus from '@/utils/eventBus'
  import {mapActions, mapGetters} from 'vuex'

  export default {
    name: "OperationOptions",
    props: ['row'],
    components: {
      EditMaterial
    },
    data() {
      return {
        isEditing: false
      }
    },
    mounted() {
      eventBus.$on('closeEditPanel', () => {
        this.isEditing = false;
      })
    },
    computed: {
      //...mapGetters['isDetailsActive']
    },
    methods: {
      ...mapActions(['setDetailsActiveState', 'setDetailsData', 'setLoading']),
      checkMaterialDetails: function (val) {
        this.setLoading(true);
        this.setDetailsActiveState(true);
        this.setDetailsData('');
        this.setDetailsData(val.id)
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
