/**
 * Created by xkwu on 2016/5/12.
 */
angular.module('MetronicApp').controller('NodeController', ['$scope', '$http', '$uibModal', "$stateParams", "NgTableParams", function ($scope, $http, $uibModal, $stateParams, NgTableParams) {

    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            $scope.queryCondition = {
                appId: $stateParams.appId,
                length: params.count(),
                begin: (params.page() - 1) * params.count()
            };
            var req = {
                method: 'post',
                url: '/node/queryAllNodeTable',
                data: $scope.queryCondition
            };

            var b = $http(req).success(function (data, status, headers, config) {
                if (data.statusCode === "SUCCESS") {
                    $scope.rows = data.dataList;
                    $scope.tableParams.total(data.page.count);
                } else {
                    toastr.error(data.msg, "获取实例列表数据失败！！！")
                }
            }).error(function (data, status, headers, config) {
                toastr.error("获取实例列表数据失败！！！")
            });
            return b.then(function (data) {
                return data.data.dataList;
            });
        }
    });
    $scope.modifyNodeStatus = function (id, status) {
        var title;
        if (status) {
            title = "启用";
        } else {
            title = "禁用";
        }
        bootbox.confirm("确认" + title + "该实例？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/node/modifyNodeStatus',
                    data: {appId: $stateParams.appId, id: id, status: status}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("修改实例状态成功！！！");
                        $scope.tableParams.reload();
                    } else {
                        toastr.error(data.msg, "修改实例状态失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error(data.msg, "修改实例状态失败！！！");
                });
            }
        });

    };
    $scope.showDetailModal = function (row) {

        var modalInstance = $uibModal.open({
            templateUrl: 'detail.html',
            controller: 'detailModalCtrl',
            size: 'md',
            // backdrop: 'static',
            resolve: {
                row: function () {
                    return row;
                },
                stateParams: function () {
                    return $stateParams;
                }
            }
        });
        modalInstance.result.then(function () {
            $scope.tableParams.reload();
        });
    };

    $scope.deleteNode = function (row) {
        bootbox.confirm("确认删除该实例？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/node/deleteNode',
                    data: {appId: row.appId, id:row.id}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("删除实例成功！！！");
                        $scope.tableParams.reload();
                    } else {
                        toastr.error(data.msg, "删除实例失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error(data.msg, "删除实例失败！！！");
                });
                $scope.tableParams.reload();
            }
        });
    }
}]).controller('detailModalCtrl', ["$scope", "$uibModalInstance", "row", function ($scope, $uibModalInstance, row) {
    $scope.row = row;
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);