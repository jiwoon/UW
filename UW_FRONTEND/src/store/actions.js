import {materialAddUrl} from "../config/globalUrl";
import {userTypeList} from "./getters";

export const setLoginToken = ({commit}, token) => {
  commit('setLoginToken', token)
};

export const setLogsRouter = ({commit}, logsRouterData) => {
  commit('setLogsRouter', logsRouterData)
};

export const setLoading = ({commit}, isLoading) => {
  commit('setLoading', isLoading)
};

export const setMaterialAdding = ({commit}, isMaterialAdding) => {
  commit('setMaterialAdding', isMaterialAdding)
};
export const setMaterialEditing = ({commit}, isMaterialEditing) => {
  commit('setMaterialEditing', isMaterialEditing)
};
export const setEditData = ({commit}, editData) => {
  commit('setEditData', editData)
};
export const setCopyData = ({commit}, copyData) => {
  commit('setCopyData', copyData)
};
export const setDetailsActiveState = ({commit}, isDetailsActive) => {
  commit('setDetailsActiveState', isDetailsActive)
};
export const setDetailsData = ({commit}, materialDetails) => {
  commit('setDetailsData', materialDetails)
};
export const setTaskActiveState = ({commit}, isTaskDetailsActive) => {
  commit('setTaskActiveState', isTaskDetailsActive)
};
export const setTaskData = ({commit}, taskDetails) => {
  commit('setTaskData', taskDetails)
};

export const setUserTypeList = ({commit}, userTypeList) => {
  commit('setUserTypeList', userTypeList)
};
