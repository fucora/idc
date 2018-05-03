/**
 * Created by xkwu on 2016/5/13.
 */
angular.module('MetronicApp').controller('TaskHistoryController', ['$scope', '$http', '$uibModal', "$stateParams", "NgTableParams", function ($scope, $http, $uibModal, $stateParams, NgTableParams) {
    $scope.taskTypeOptions = [{key: '定时任务', value: 11}, {key: '简单任务', value: 13}, {key: '流程子任务', value: 12}, {key: '流程任务', value: 21}];
    if ($stateParams.beginTime && $stateParams.result) {
        $scope.queryCondition = {
            executeResult: $stateParams.result,
            beginTime: $stateParams.beginTime,
            endTime: moment($stateParams.beginTime).add(5, "m").subtract(1, "s").format("YYYY-MM-DD HH:mm:ss"),
            task: {
                appId: $stateParams.appId, taskId: $stateParams.taskId, taskType: ''
            }
        };
    }
    else {
        $scope.queryCondition = {
            beginTime: moment().hour(0).minutes(0).seconds(0).format("YYYY-MM-DD HH:mm:ss"),
            endTime: moment().hour(23).minutes(59).seconds(59).format("YYYY-MM-DD HH:mm:ss"),
            task: {
                appId: $stateParams.appId,
                taskId: $stateParams.taskId,
                taskType: $stateParams.taskType ? Number($stateParams.taskType) : 11
            }
        };
    }
    function loadTaskHistory() {
        $scope.tableParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                var queryCondition = $.extend({
                    page: {
                        length: params.count(),
                        begin: (params.page() - 1) * params.count()
                    }
                }, $scope.queryCondition);
                var req = {
                    method: 'post',
                    url: '/taskHistory/taskHistoryTable',
                    data: queryCondition,
                    headers: {
                        'Content-Type': "application/json"
                    },
                    transformRequest: function (data) {
                        return angular.toJson(data);
                    }
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        $scope.tableParams.total(data.page.count);
                    } else {
                        toastr.error(data.msg, "获取任务执行历史列表数据失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error("任务执行历史列表数据失败！！！")
                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });
    }

    $scope.subTaskHistory = function (row) {
        $scope.tabStack.push($scope.activeTab);
        $scope.activeTab = 'subTaskHistory';
        loadSubTaskHistory(row);
    }

    function loadSubTaskHistory(row) {
        $scope.subTaskHistoryParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                if (row) {
                    var queryCondition = angular.extend({
                        workflowExecuteId: row.executeId,
                        page: {
                            length: params.count(),
                            begin: (params.page() - 1) * params.count()
                        },
                        task: {
                            appId: $stateParams.appId
                        }
                    });
                } else {
                    var queryCondition = angular.extend({
                        page: {
                            length: params.count(),
                            begin: (params.page() - 1) * params.count()
                        }
                    }, $scope.queryCondition);
                }
                var req = {
                    method: 'post',
                    url: '/taskHistory/subTaskHistoryTable',
                    data: queryCondition,
                    headers: {
                        'Content-Type': "application/json"
                    },
                    transformRequest: function (data) {
                        return angular.toJson(data);
                    }
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        $scope.subTaskHistoryParams.total(data.page.count);
                    } else {
                        toastr.error(data.msg, "获取任务执行历史列表数据失败！！！");
                    }

                }).error(function (data, status, headers, config) {
                    toastr.error("获取任务执行历史列表数据失败！！！")
                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });
    }

    $scope.taskStatusTable = function (taskId, executeId, workflowExecuteId) {
        $scope.tabStack.push($scope.activeTab);
        $scope.activeTab = 'taskStatus';
        $scope.taskStatusTableParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                var queryCondition = angular.extend({
                    taskId: taskId,
                    executeId: executeId,
                    workflowExecuteId: workflowExecuteId,
                    length: params.count(),
                    begin: (params.page() - 1) * params.count()
                }, $scope.queryCondition);
                var req = {
                    method: 'post',
                    url: '/taskStatus/taskStatusTable',
                    params: queryCondition,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    $scope.taskStatusTableParams.total(data.page.count);
                }).error(function (data, status, headers, config) {
                    toastr.error("任务执行历史列表数据失败！！！")
                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });
    }

    $scope.back = function () {
        $scope.activeTab = $scope.tabStack.pop();
    };

    $scope.$watch("queryCondition.task.taskType", function (value) {
        switch (value) {
            case '12':
            case 12:
                $scope.tabStack = [];
                $scope.activeTab = 'subTaskHistory';
                loadSubTaskHistory();
                break;
            default:
                $scope.tabStack = [];
                $scope.activeTab = 'taskHistory';
                loadTaskHistory();
        }

    })

    $scope.onSearch = function () {
        if ($scope.activeTab == 'taskHistory') {
            $scope.tableParams.search();
        }
        if ($scope.activeTab == 'subTaskHistory') {
            $scope.subTaskHistoryParams.search();
        }
    }
    var originQueryCondition = angular.copy($scope.queryCondition);
    $scope.resetForm = function () {
        $scope.queryCondition = angular.copy(originQueryCondition);
    };
}]);