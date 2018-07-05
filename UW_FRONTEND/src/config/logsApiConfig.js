let url;
if (process.env.NODE_ENV === 'production') {
  url = window.g.API_URL
} else {
  url = window.g.LOCAL_URL
}
export const getLogsConfig = (name) => {
  if (name === 'task_log') {
    return TASK_CONFIG;
  } else if (name === 'action_log') {
    return ACTION_CONFIG;
  } else if (name === 'position_log') {
    return POSITION_CONFIG;
  }
};

const TASK_CONFIG = [
  {field: "id", title: '序号', visible: false},
  {field: 'showId', title: '序号', colStyle: {'width': '70px'}},
  {field: "taskId", title: '任务ID', colStyle: {'width': '70px'}},
  {field: "taskType", title: '任务类型', colStyle: {'width': '70px'}},
  {field: "materialId", title: '物料ID', colStyle: {'width': '70px'}},
  {field: "materialNo", title: '料号', colStyle: {'width': '120px'}},
  {field: "quantity", title: '数量', colStyle: {'width': '70px'}},
  {field: "operator", title: '操作员ID', colStyle: {'width': '70px'}},
  {field: "operatorName", title: '操作员', colStyle: {'width': '70px'}},
  {field: "auto", title: '是否自动', colStyle: {'width': '70px'}},
  {field: "time", title: '时间', colStyle: {'width': '170px'}}
];

const ACTION_CONFIG = [
  {field: 'showId', title: '序号', colStyle: {'width': '70px'}},
  {field: 'id', title: '序号', visible: false},
  {field: 'ip', title: 'IP地址', colStyle: {'width': '120px'}},
  {field: 'uid', title: '用户', colStyle: {'width': '80px'}},
  {field: 'action', title: '操作', colStyle: {'width': '200px'}},
  {field: 'time', title: '时间', colStyle: {'width': '170px'}}

];

const POSITION_CONFIG = [
  {field: "id", title: '序号', colStyle: {'width': '70px'}},
  {field: "taskId", title: '任务ID', colStyle: {'width': '70px'}},
  {field: "materialId", title: '物料ID', colStyle: {'width': '70px'}},
  {field: "materialNo", title: '料号', colStyle: {'width': '120px'}},
  {field: "oldArea", title: '原区域', colStyle: {'width': '70px'}},
  {field: "oldRow", title: '原行号', colStyle: {'width': '70px'}},
  {field: "oldCol", title: '原列号', colStyle: {'width': '70px'}},
  {field: "oldHeight", title: '原高度', colStyle: {'width': '70px'}},
  {field: "newArea", title: '新区域', colStyle: {'width': '70px'}},
  {field: "newRow", title: '新行号', colStyle: {'width': '70px'}},
  {field: "newCol", title: '新列号', colStyle: {'width': '70px'}},
  {field: "newHeight", title: '新高度', colStyle: {'width': '70px'}},
  {field: "time",title: '时间',  colStyle: {'width': '170px'}}
];
