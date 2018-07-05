let url;
if (process.env.NODE_ENV === 'production') {
  url = window.g.API_URL
} else {
  url = window.g.LOCAL_URL
}

//material manage
export const materialCountUrl = url + '/manage/material/count';
export const materialEntityUrl = url + '/manage/material/getEntities';
export const materialAddUrl = url + '/manage/material/add';
export const materialUpdateUrl = url + '/manage/material/update';


//logs
export const logsUrl = url + '/log/select';


//users
export const loginUrl = url + '/manage/user/login';
export const logoutUrl = url + '/manage/user/logout';

//tasks
export const taskUrl = url + '/task';
export const taskSelectUrl = url + '/task/select';
export const taskCreateUrl = url + '/task/create';
export const taskCheckUrl = url + '/task/check';
export const taskWindowsUrl = url + '/task/getWindows';

//robot
export const robotSelectUrl = url + '/manage/robot/select';
export const robotSwitchUrl = url + '/manage/robot/switch';
