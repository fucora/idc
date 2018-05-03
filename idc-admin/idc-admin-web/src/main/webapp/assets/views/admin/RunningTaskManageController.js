/**
 * Created by xkwu on 2016/11/1.
 */
/**
 * Created by xkwu on 2016/6/20.
 */
angular.module('MetronicApp').controller('RunningTaskManageController', ['$scope', '$http', function ($scope, $http) {

    $scope.loadData = function loadData() {
        $http.post("/admin/runningTaskManage/listRunningTaskTable").success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                $scope.rows = data.dataList;
            } else {
                toastr.error(data.msg, "获取正在运行任务列表失败！！！")
            }
        });

    };
    $scope.loadData();
    $scope.deleteTask = function (id) {
        bootbox.confirm("确认删除该执行记录？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/admin/runningTaskManage/deleteRunningTask',
                    data: {id: id}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("删除成功！！！");
                        $scope.loadData();
                    } else {
                        toastr.error(data.msg, "删除失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error(data.msg, "删除失败！！！");
                });
            }
        });
    }
}]);