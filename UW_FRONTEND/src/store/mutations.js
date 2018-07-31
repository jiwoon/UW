
export const setLoginToken = (state, token) => {
  state.token = token;
};
export const setLogsRouter = (state, logsRouterData) => {
  state.logsRouterApi = logsRouterData;
};

export const setLoading = (state, isLoading) => {
  state.isLoading = isLoading;
};

export const setMaterialAdding = (state, isMaterialAdding) => {
  state.isMaterialAdding = isMaterialAdding
};
export const setMaterialEditing = (state, isMaterialEditing) => {
  state.isMaterialEditing = isMaterialEditing
};
export const setEditData = (state, editData) => {
  state.editData = editData
};
export const setCopyData = (state, copyData) => {
  state.copyData = copyData
};
export const setDetailsActiveState = (state, isDetailsActive) => {
  state.isDetailsActive = isDetailsActive
};
export const setDetailsData = (state, materialDetails) => {
  state.materialDetails = materialDetails
};
export const setTaskActiveState = (state, isTaskDetailsActive) => {
  state.isTaskDetailsActive = isTaskDetailsActive
};
export const setTaskData = (state, taskDetails) => {
  state.taskDetails = taskDetails
};

export const setUserTypeList = (state, userTypeList) => {
  state.userTypeList = userTypeList
};

export const setCurrentWindow = (state, currentWindowId) => {
  state.currentWindowId = currentWindowId
};
