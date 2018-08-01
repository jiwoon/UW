export const getTaskDetailsConfig = (name) => {
  if (name === 'io'){
    return IO_CONFIG;
  }
};


const IO_CONFIG = [
  {field: 'id', title: 'ID', colStyle: {'width': '60px'}},
  {field: 'materialNo', title: '料号', colStyle: {'width': '120px'}},
  {field: 'planQuantity', title: '计划数量', colStyle: {'width': '90px'}},
  {field: 'actualQuantity', title: '实际数量', colStyle: {'width': '90px'}},
  {field: 'finishTime', title: '完成时间', colStyle: {'width': '120px'}},
  {field: 'operation', title: '操作', tdComp: 'SubsOperation', colStyle: {'width': '90px'}},

];
