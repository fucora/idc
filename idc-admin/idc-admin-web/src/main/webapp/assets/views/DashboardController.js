angular.module('MetronicApp').controller('DashboardController', ['$scope', '$http', 'NgTableParams', '$uibModal', 'systemService', '$rootScope', function ($scope, $http, NgTableParams, $uibModal, systemService, $rootScope) {
    $scope.$on('$viewContentLoaded', function () {
        $scope.tableParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                $scope.queryCondition = {length: params.count(), begin: (params.page() - 1) * params.count()};
                var req = {
                    method: 'post',
                    url: '/application/listAppTable',
                    data: $scope.queryCondition
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        $scope.rows = data.dataList;
                        $scope.tableParams.total(data.page.count);
                    } else {
                        toastr.error(data.msg, "获取应用列表失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error(data, "获取应用列表失败！！！");
                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });


        $scope.showApplicationModal = function (row) {

            var modalInstance = $uibModal.open({
                templateUrl: 'applicationModal.html',
                controller: 'applicationModalCtrl',
                size: 'lg',
                resolve: {
                    row: function () {
                        return row;
                    }
                }
            });
            modalInstance.result.then(function () {
                $scope.tableParams.reload();
                $scope.$emit('systemInfoChanged');
            });
        };

        $scope.showUserManageModal = function (row) {

            var modalInstance = $uibModal.open({
                templateUrl: 'userManageModal.html',
                controller: 'userManageModalCtrl',
                size: 'lg',
                resolve: {
                    row: function () {
                        return row;
                    }
                }
            });
            modalInstance.result.then(function () {
            });
        };

        $scope.deleteApplication = function (row) {
            bootbox.confirm("确认删除该应用？？？", function (o) {
                    if (o) {
                        var req = {
                            method: 'post',
                            url: '/application/deleteApp',
                            data: {appId: row.appId},
                        };

                        $http(req).success(function (data, status, headers, config) {
                            if (data.statusCode === "SUCCESS") {
                                toastr.success("删除数据成功！！！");
                                $scope.tableParams.reload();
                                $scope.$emit('systemInfoChanged');
                            } else {
                                toastr.error(data.msg, "删除数据失败！！！");
                            }

                        });
                    }

                }
            );
        }
    });


    $scope.enableAlarm = function (appId, status) {
        var title;
        if (status) {
            title = "接收报警";
        } else {
            title = "不接收报警";
        }
        bootbox.confirm("确认" + title + "？？？", function (o) {
                if (o) {
                    var req = {
                        method: 'post',
                        url: '/application/enableAlarm',
                        data: {appId: appId, status: status},
                    };

                    $http(req).success(function (data, status, headers, config) {
                        if (data.statusCode === "SUCCESS") {
                            toastr.success("修改成功！！！");
                            $scope.tableParams.reload();
                        } else {
                            toastr.error(data.msg, "修改失败！！！");
                        }

                    });
                }

            }
        );
    }

}]).controller('applicationModalCtrl', ["$scope", "$uibModalInstance", "$uibModal", "$http", "row", "systemService", function ($scope, $uibModalInstance, $uibModal, $http, row) {
    if (row) {
        $scope.app = {appId: row.appId, appName: row.appName, appKey: row.appKey, appDescription: row.appDescription};
        $scope.headerTitle = "编辑应用";
    } else {
        $scope.headerTitle = "创建应用";
    }
    $scope.ok = function () {
        var app = angular.copy($scope.app);
        delete app.appKey;
        var req = {
            method: 'post',
            url: '/application/createOrUpdateApplication',
            data: app,
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
        };

        $http(req).success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                toastr.success("保存数据成功！！！");
                $uibModalInstance.close();
            } else {
                toastr.error(data.msg, "保存数据失败！！！");
            }

        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

}]).controller('userManageModalCtrl', ["$scope", "$uibModalInstance", "$uibModal", "NgTableParams", "$http", "row", function ($scope, $uibModalInstance, $uibModal, NgTableParams, $http, row) {
    if (row) {
        $scope.app = {
            appId: row.appId,
            appName: row.appName,
            appKey: row.appKey,
            appDescription: row.appDescription,
            createUser: row.createUser
        };
    }
    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            $scope.queryCondition = {
                appId: row.appId,
                length: params.count(),
                begin: (params.page() - 1) * params.count()
            };
            var req = {
                method: 'post',
                url: '/application/listAppUser',
                data: $scope.queryCondition
            };

            var b = $http(req).success(function (data, status, headers, config) {
                if (data.statusCode === "SUCCESS") {
                    $scope.rows = data.dataList;
                    $scope.tableParams.total(data.page.count);
                } else {
                    toastr.error(data.msg, "获取人员管理信息失败！！！");
                }
            }).error(function (data, status, headers, config) {
                toastr.error("获取人员管理信息失败！！！")
            });
            return b.then(function (data) {
                return data.data.dataList;
            });
        }
    });

    $scope.showAddUserModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: 'addUserModal.html',
            controller: 'addUserModalCtrl',
            size: 'md',
            resolve: {
                row: function () {
                    return row;
                }
            }
        });
        modalInstance.result.then(function () {
            $scope.tableParams.reload();
        });
    };

    $scope.showEditUserModal = function (row) {
        var modalInstance = $uibModal.open({
            templateUrl: 'editUserModal.html',
            controller: 'editUserModalCtrl',
            size: 'md',
            resolve: {
                row: function () {
                    return row;
                }
            }
        });
        modalInstance.result.then(function () {
            $scope.tableParams.reload();
        });
    };

    $scope.deleteUser = function (item) {
        bootbox.confirm("确认删除？？？", function (o) {
                if (o) {
                    var req = {
                        method: 'post',
                        url: '/application/delAppUser',
                        data: {appId: row.appId, userId: item.userId}
                    };

                    $http(req).success(function (data, status, headers, config) {
                        if (data.statusCode === "SUCCESS") {
                            toastr.success("删除数据成功！！！");
                            $scope.tableParams.reload();
                            $scope.$emit('systemInfoChanged');
                        } else {
                            toastr.error(data.msg, "删除数据失败！！！");
                        }

                    });
                }

            }
        );
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

}]).controller('addUserModalCtrl', ["$scope", "$uibModalInstance", "$http", "row", function ($scope, $uibModalInstance, $http, row) {
    $scope.selection={};
    var oldSelection ={};
    $scope.ok = function () {
        var deleteItems = [];
        var addItems = [];
        for(var x in $scope.selection){
            var hasIn =false;
            for(var y in oldSelection){
                if(x == y){
                    hasIn = true;
                    break;
                }
            }
            if(!hasIn){
                addItems.push(x);
            }
        }
        for(var x in oldSelection){
            var hasIn =false;
            for(var y in $scope.selection){
                if(x == y){
                    hasIn = true;
                    break;
                }
            }
            if(!hasIn){
                deleteItems.push(x);
            }
        }
        console.log(addItems);
        console.log(deleteItems);
        var postData ={appId: row.appId,addItems:addItems,deleteItems:deleteItems}
        var req = {
            method: 'post',
            url: '/application/modifyUserAppInfo',
            data: postData
        };

        $http(req).success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                toastr.success("保存数据成功！！！");
                $uibModalInstance.close();
            } else {
                toastr.error(data.msg, "保存数据失败！！！");
            }

        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    var req = {
        method: 'post',
        url: '/application/querUserAppInfo',
        data: {appId: row.appId}
    };

    $http(req).success(function (data, status, headers, config) {
        if (data.statusCode === "SUCCESS") {
            $scope.selectable = [];
            for (var x in data.dataMap.list) {
                var hasIn = false;
                for (var y in data.dataMap.userApp) {
                    if (data.dataMap.userApp[y].userId == data.dataMap.list[x].userId) {
                        hasIn = true;
                        break;
                    }
                }
                $scope.selectable.push({
                    text: data.dataMap.list[x].userName+'['+data.dataMap.list[x].userId+']',
                    value: data.dataMap.list[x].userId,
                    disabled:(row.createUser == data.dataMap.list[x].userName)&&hasIn,
                    selected:hasIn
                });
                if (hasIn) {
                    $scope.selection[data.dataMap.list[x].userId] = true;
                }
            }
            oldSelection = angular.copy($scope.selection);
        } else {
            toastr.error(data.msg, "查询数据失败！！！");
        }

    });

}]).controller('editUserModalCtrl', ["$scope", "$uibModalInstance", "$http", "row", function ($scope, $uibModalInstance, $http, row) {

    $scope.user = angular.copy(row);
    $scope.ok = function () {
        var postData = angular.extend({}, {
            id: $scope.user.id,
            userPhone: $scope.user.userPhone,
            userEmail: $scope.user.userEmail
        });
        var req = {
            method: 'post',
            url: '/application/updateAppUser',
            data: postData
        };

        $http(req).success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                toastr.success("保存数据成功！！！");
                $uibModalInstance.close();
            } else {
                toastr.error(data.msg, "保存数据失败！！！");
            }

        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };


}]);