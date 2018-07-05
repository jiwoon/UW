let url;
if (process.env.NODE_ENV === 'production') {
  url = window.g.API_URL
} else {
  url = window.g.LOCAL_URL
}
//0入库 1出库 2盘点 3位置优化
export const getCheckConfig = (name) => {
  switch (name) {
    case 0:
      return STORAGE_CONFIG;
    case 1:
      return STORAGE_CONFIG;
    case 2:
      return INVENTORY_CONFIG;
    case 3:
      return POSITION_CONFIG;
  }
};

const STORAGE_CONFIG = [
  {title: '料号', field: 'materialNo', colStyle: {width: '100px'}},
  {title: '请求数量', field: 'requestQuantity', colStyle: {width: '60px'}},
  {title: '实际数量', field: 'actualQuantity', colStyle: {width: '60px'}},
  {title: '完成时间', field: 'finishTime', colStyle: {width: '120px'}}
];

const INVENTORY_CONFIG = [
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
