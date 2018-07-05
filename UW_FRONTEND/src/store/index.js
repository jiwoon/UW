import Vue from 'vue'
import Vuex from 'vuex'
import * as mutations from './mutations'
import * as actions from './actions'
import * as getters from './getters'

Vue.use(Vuex);

const state = {
  token: '',
  isLoading: false,
  logsRouterApi: 'default',
  isMaterialAdding: false,
  isMaterialEditing: false,
  editData: [],
  copyData: [],
  materialDetails: '',
  isDetailsActive: false,
  taskDetails: '',
  isTaskDetailsActive: false
};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations
});

export default store;
