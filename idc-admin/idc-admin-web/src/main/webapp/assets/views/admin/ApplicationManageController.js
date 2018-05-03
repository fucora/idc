/**
 * Created by xkwu on 2016/6/20.
 */
angular.module('MetronicApp').controller('ApplicationManageController', ['$scope', '$http','NgTableParams', function ($scope, $http,NgTableParams) {
    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            var queryCondition = angular.extend({
                length: params.count(),
                begin: (params.page() - 1) * params.count()
            }, $scope.queryCondition);
            var req = {
                method: 'post',
                url: '/admin/application/listAppTable',
                data: queryCondition
            };

            var b = $http(req).success(function (data, status, headers, config) {
                if (data.statusCode === "SUCCESS") {
                    $scope.rows = data.dataList;
                    $scope.tableParams.total(data.page.count);
                } else {
                    toastr.error(data.msg, "获取应用列表数据失败！！！")
                }
            }).error(function (data, status, headers, config) {
                toastr.error("获取应用列表数据失败！！！")
            });
            return b.then(function (data) {
                return data.data.dataList;
            });
        }
    });


    $scope.operationLabel = function (row) {
        for(var x in $scope.system.apps){
            if ( $scope.system.apps[x].appId == row.appId){
                if($scope.system.apps[x].createUser == $scope.system.loginUser.userName){
                    return "creater";
                }
                else {
                    return "leave";
                }
            }
        }
        return "join";
    };

    $scope.joinApplication = function (row) {
        bootbox.confirm("确认加入该应用？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/admin/application/joinApplication',
                    data: {appId: row.appId}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("成功加入该应用！！！")
                        $scope.tableParams.reload();
                        $scope.$emit('systemInfoChanged');
                    } else {
                        toastr.error(data.msg, "加入失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error("加入失败！！！");
                });
            }
        });
    };

    $scope.leaveApplication = function (row) {
        bootbox.confirm("确认退出该应用？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/admin/application/leaveApplication',
                    data: {appId: row.appId}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("成功退出应用！！！")
                        $scope.tableParams.reload();
                        $scope.$emit('systemInfoChanged');
                    } else {
                        toastr.error(data.msg, "退出失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error("退出失败！！！");
                });
            }
        });
    };

    var originQueryCondition = angular.copy($scope.queryCondition);
    $scope.resetForm = function ()
    {
        $scope.queryCondition = angular.copy(originQueryCondition);
    };
}]);