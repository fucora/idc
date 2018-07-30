/**
 * Created by xkwu on 2016/5/13.
 */
angular.module('MetronicApp').controller('TaskController', ['$scope', '$http', '$uibModal', "$stateParams", "$state", "NgTableParams", 'exchangeService', function ($scope, $http, $uibModal, $stateParams, $state, NgTableParams, exchangeService) {
    $scope.taskTypeOptions = [{key: ''}, {key: '定时任务', value: 11}, {key: '流程子任务', value: 12}, {key: '简单任务', value: 13}, {key: '流程任务', value: 21}];
    $scope.$stateParams = $stateParams;
    $scope.queryCondition={};
    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            var queryCondition = angular.extend({
                appId: $stateParams.appId,
                length: params.count(),
                begin: (params.page() - 1) * params.count()
            }, $scope.queryCondition);
            var req = {
                method: 'post',
                url: '/task/taskTable',
                data: queryCondition
            };

            var b = $http(req).success(function (data, status, headers, config) {
                if (data.statusCode === "SUCCESS") {
                    $scope.rows = data.dataList;
                    $scope.tableParams.total(data.page.count);
                } else {
                    toastr.error(data.msg, "获取任务列表数据失败！！！")
                }
            }).error(function (data, status, headers, config) {
                toastr.error("获取任务列表数据失败！！！")
            });
            return b.then(function (data) {
                return data.data.dataList;
            });
        }
    });

    $scope.showTaskModal = function (row) {

        var modalInstance = $uibModal.open({
            templateUrl: 'taskModal.html',
            controller: 'taskModalCtrl',
            size: 'lg',
            backdrop: 'static',
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

    $scope.modifyTaskStatus = function (row, enable) {
        if (row.taskType == 21 && !row.workflowId && enable) {
            toastr.warning("流程任务需要先创建任务流程，才能启用！！！");
            return;
        }
        var title;
        if (enable) {
            title = "启用";
        } else {
            title = "停用";
        }
        bootbox.confirm("确认" + title + "该任务？？？", function (o) {
            if (o) {
                var req = {
                    method: 'post',
                    url: '/task/modifyTaskStatus',
                    data: {appId: $stateParams.appId, taskId: row.taskId, enable: enable}
                };

                $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        toastr.success("更新任务状态成功！！！")
                        $scope.tableParams.reload();
                    } else {
                        toastr.error(data.msg, "更新任务状态失败！！！");
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error("更新任务状态失败！！！");
                });
            }
        });
    };

    $scope.executeTask = function (row) {
        if (row.taskType == 21 && !row.workflowId) {
            toastr.warning("流程任务需要先创建任务流程，才能执行！！！");
            return;
        }
        bootbox.confirm("确认执行该任务？？？", function (o) {
                if (o) {
                    var req = {
                        method: 'post',
                        url: '/task/executeTask',
                        data: {appId: $stateParams.appId, taskId: row.taskId},
                    };

                    $http(req).success(function (data, status, headers, config) {
                        if (data.statusCode === "SUCCESS") {
                            toastr.success("执行任务成功！！！")
                            $scope.tableParams.reload();
                        } else {
                            toastr.error(data.msg, "执行任务失败！！！");
                        }
                    }).error(function (data, status, headers, config) {
                        toastr.error("执行任务失败！！！");
                    });
                }

            }
        );


    };

    $scope.deleteTask = function (taskId) {
        bootbox.confirm("确认删除？？？", function (o) {
                if (o) {
                    var req = {
                        method: 'post',
                        url: '/task/deleteTask',
                        data: {appId: $stateParams.appId, taskId: taskId}
                    };

                    $http(req).success(function (data, status, headers, config) {
                        if (data.statusCode === "SUCCESS") {
                            toastr.success("删除任务成功！！！")
                            $scope.tableParams.reload();
                        } else {
                            toastr.error(data.msg, "删除任务失败！！！");
                        }
                    }).error(function (data, status, headers, config) {
                        toastr.error("删除任务失败！！！");
                    });
                }

            }
        );
    }

    $scope.showWorkFlow = function (row) {

        var req = {
            method: 'post',
            url: '/task/getWorkFlow',
            data: {appId: $stateParams.appId, taskId: row.taskId}
        };

        $http(req).success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {


                var modalInstance = $uibModal.open({
                    templateUrl: 'workFlowModal.html',
                    controller: 'workFlowModalCtrl',
                    size: 'full',
                    resolve: {
                        data: function () {
                            return data.dataMap;
                        },
                        row: function () {
                            return row;
                        }
                    },
                    backdrop: 'static'
                });
                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            } else {
                toastr.error(data.msg, "获取工作流程数据失败！！！");
            }

        });
    }


    $scope.onHistoryClick = function (row) {
        exchangeService.set("taskHistory", row);
        $state.go('taskHistory', {appId: $stateParams.appId, taskId: row.taskId, taskType: row.taskType});
    }

    var originQueryCondition = angular.copy($scope.queryCondition);
    $scope.resetForm = function () {
        $scope.queryCondition = angular.copy(originQueryCondition);
    };


    $scope.showMonitor = false;
    $scope.showMonitorPage = function (row) {
        $scope.showMonitor = true;
        $scope.monitor = {taskName: row.taskName, taskId: row.taskId};
        loadData("hour", 1);
    };

    $scope.showHistory = false;
    $scope.showHistoryPage = function (row) {
        $scope.showHistory = true;
        $scope.history = {taskName: row.taskName, taskId: row.taskId};


        $scope.historyTableParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                var queryCondition = angular.extend({
                    taskId: row.taskId,
                    length: params.count(),
                    begin: (params.page() - 1) * params.count()
                }, $scope.queryCondition);
                var req = {
                    method: 'post',
                    url: '/task/taskUpdateHistoryTable',
                    data: queryCondition
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    if (data.statusCode === "SUCCESS") {
                        $scope.rows = data.dataList;
                        $scope.historyTableParams.total(data.page.count);
                    } else {
                        toastr.error(data.msg, "获取变更记录数据失败！！！")
                    }
                }).error(function (data, status, headers, config) {
                    toastr.error("获取变更记录数据失败！！！")
                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });
    };

    $scope.back = function () {
        $scope.showMonitor = false;
        $scope.showHistory = false;
    };

    $scope.loadData = function (type, num) {
        loadData(type, num);
    }

    function updateChart(data) {
        var succeedArray = [], failedArray = [], timeOutArray = [],timeArray=[],maxDurationArray=[],minDurationArray=[],durationArray=[];

        for (var x in data){
            succeedArray.push(data[x].executeSucceed);
            failedArray.push(data[x].executeFailed);
            timeOutArray.push(data[x].executeTimeOut);
            maxDurationArray.push(data[x].executeMaxDuration);
            minDurationArray.push(data[x].executeMinDuration);
            durationArray.push(data[x].executeDuration);
            timeArray.push(moment(data[x].dataTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        // 基于准备好的dom，初始化echarts实例
        var myChart1 = echarts.init(document.getElementById('main1'),'macarons');

        // 指定图表的配置项和数据
        var option1 = {

            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:['成功','失败','超时']
            },
            toolbox: {
                feature: {
                    saveAsImage: {},
                    magicType: {
                        type: ['line', 'bar', 'stack', 'tiled']
                    }
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : timeArray
                }
            ],
            yAxis : [
                {
                    name : '执行次数(次)',
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'成功',
                    type:'bar',
                    stack: '总量',
                    itemStyle: {normal: {
                        color: "#36D7B7"}},
                    barMaxWidth:40,
                    data:succeedArray,
                },
                {
                    name:'失败',
                    type:'bar',
                    stack: '总量',
                    itemStyle: {normal: {
                        color: "#E35B5A"}},
                    barMaxWidth:40,
                    data:failedArray
                },
                {
                    name:'超时',
                    type:'bar',
                    stack: '总量',
                    itemStyle: {normal: {
                        color: "#F4D03F"}},
                    barMaxWidth:40,
                    data:timeOutArray
                }
            ]
        };


        // 使用刚指定的配置项和数据显示图表。
        myChart1.setOption(option1);

        // 基于准备好的dom，初始化echarts实例
        var myChart2 = echarts.init(document.getElementById('main2'),'macarons');

        // 指定图表的配置项和数据
        var option2 = {

            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:['平均执行时间','最大执行时间','最小执行时间']
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    // boundaryGap : false,
                    data : timeArray
                }
            ],
            yAxis : [
                {
                    name : '时长(秒)',
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'平均执行时间',
                    type:'line',
                    data:durationArray
                },
                {
                    name:'最大执行时间',
                    type:'line',
                    data:maxDurationArray
                },
                {
                    name:'最小执行时间',
                    type:'line',
                    data:minDurationArray
                }
            ]
        };


        // 使用刚指定的配置项和数据显示图表。
        myChart2.setOption(option2);
    }

    function loadData(type, num) {
        if (type == "hour") {
            $scope.title = num + "小时";
        } else if (type == "day") {
            $scope.title = num + "天";
        }
        var req = {
            method: 'post',
            url: '/task/aggregate',
            data: {taskId: $scope.monitor.taskId, appId: $stateParams.appId, type: type, num: num}
        };

        $http(req).success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                $scope.dataMap = data.dataMap;
                updateChart(data.dataMap.executeStatistic)
            } else {
                toastr.error(data.msg, "获取实例列表数据失败！！！")
            }
        }).error(function (data, status, headers, config) {
            toastr.error("获取实例列表数据失败！！！")
        });
    }
}]).controller('taskModalCtrl', ["$scope", "$uibModalInstance", "$uibModal", "$http", "row", "stateParams", function ($scope, $uibModalInstance, $uibModal, $http, row, stateParams) {
    if (row) {
        $scope.postData = angular.copy(row);
        delete $scope.postData.taskImplementType;
        delete $scope.postData.dataLimit;
        // delete $scope.postData.workflowId;
        delete $scope.postData.createUser;
        delete $scope.postData.createTime;
        delete $scope.postData.updateUser;
        delete $scope.postData.updateTime;
        delete $scope.postData.lastExecuteStartTime;
        delete $scope.postData.lastExecuteEndTime;
        delete $scope.postData.lastExecuteIp;
        delete $scope.postData.lastExecutePort;
        delete $scope.postData.lastExecuteNodeCode;
        delete $scope.postData.lastExecuteStatus;
        delete $scope.postData.lastExecuteMessage;
        delete $scope.postData.nextFireTime;
        $scope.headerTitle = "编辑任务";
        $scope.readonly = true;
    } else {
        $scope.postData = {
            concurrency: false,
            mutithread: false,
            failedRetry: false,
            timeoutRetry: false,
            taskStatus: 0,
            timeoutRetryTimes: 1,
            timeout: 60,
            failedRetryTimes: 1,
            failedRetryInterval: 5,
            taskCategoty: 1,
            threads: 1,
            waitTime: 0,
            simpleRepeatCount: 1,
            simpleRepeatInterval: 5
        };
        $scope.headerTitle = "创建任务";
        $scope.readonly = false;
    }

    $scope.selectClassNameOptions = [];

    var req = {
        method: 'post',
        url: '/node/getRencentActiveNode',
        data: {appId: stateParams.appId}
    };

    $http(req).success(function (data, status, headers, config) {
        if (data.statusCode === "SUCCESS") {
            if (data.dataMap.node) {
                $scope.selectClassNameOptions = data.dataMap.node.classNames.split(",");
            }
        } else {
            toastr.error(data.msg, "获取最近活动node失败！！！");
        }

    });

    $scope.$watch('postData.taskCategoty', function (newValue, oldValue) {
        taskCategorySelect(newValue, oldValue);
    });

    function taskCategorySelect(value, oldValue) {
        switch (value) {
            case "1":
            case 1:
                $scope.taskTypeOptions = [{key: "定时任务", value: 11}, {key: "流程子任务", value: 12}, {key: '简单任务', value: 13}];
                break;
            case "2":
            case 2:
                $scope.taskTypeOptions = [{key: "流程任务", value: 21}];
                delete $scope.waitTime;
                break;
            default:
                $scope.taskTypeOptions = [];
                break;
        }
        if ($scope.taskTypeOptions.length >= 1) {
            if (row) {
                if (oldValue != value) {
                    $scope.postData.taskType = $scope.taskTypeOptions[0].value;
                }
            } else {
                $scope.postData.taskType = $scope.taskTypeOptions[0].value;
            }

        }
    }

    $scope.$watch('postData.taskType', function (newValue, oldValue) {
        if (newValue !=oldValue) {
            switch (newValue) {
                case 11:
                    $scope.postData.taskStatus = 0;
                    break;                
                case 12:
                    $scope.postData.taskStatus = 1;
                    break;
                case 13:
                    $scope.postData.taskStatus = 0;
                    break;
                case 21:
                    $scope.postData.taskStatus = 0;
                    break;
            }
        }
    });


    $scope.ok = function () {
        var postData = angular.extend({appId: stateParams.appId}, $scope.postData);
        for (var x in postData) {
            if (postData[x] === false) {
                postData[x] = 0;
            } else if (postData[x] === true) {
                postData[x] = 1;
            }
        }

        switch (postData.taskType) {
            case 11:
                delete postData.waitTime;
                delete postData.simpleStartTime;
                delete postData.simpleEndTime;
                delete postData.simpleRepeatCount;
                delete postData.simpleRepeatInterval;
                break;
            case 12:
                delete postData.cron;
                delete postData.simpleStartTime;
                delete postData.simpleEndTime;
                delete postData.simpleRepeatCount;
                delete postData.simpleRepeatInterval;
                break;
            case 13:
                delete postData.cron;
                delete postData.waitTime;
                break;
            case 21:
                delete postData.className;
                delete postData.parameters;
                delete postData.timeout;
                delete postData.timeoutRetry;
                delete postData.timeoutRetryTimes;
                delete postData.failedRetry;
                delete postData.failedRetryTimes;
                delete postData.failedRetryInterval;
                delete postData.mutithread;
                delete postData.threads;
                delete postData.waitTime;
                delete postData.simpleStartTime;
                delete postData.simpleEndTime;
                delete postData.simpleRepeatCount;
                delete postData.simpleRepeatInterval;
                break;
        }

        var req = {
            method: 'post',
            url: '/task/createOrUpdateTask',
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

}]).controller('workFlowModalCtrl', ["$scope", "$uibModalInstance", "$uibModal", "$http", "row", "$stateParams", "data", function ($scope, $uibModalInstance, $uibModal, $http, row, stateParams, data) {

    if (data.subTask) {
        $scope.subTask = data.subTask;
    }
    if (data.json) {
        $scope.workFlowData = data.json;
    } else {
        $scope.workFlowData = {
            "nodeKeyProperty": "id",
            "nodeDataArray": [{"id": -1, "key": -1, "category": "Start", "loc": "-800 640", "text": "开始"},
                {"id": -2, "key": -2, "category": "End", "loc": "175 640", "text": "结束"}],
            "linkDataArray": []
        };
    }

    $scope.ok = function () {
        var postData = $.extend({taskId: row.taskId}, $scope.workFlowData, true);
        for (var x in postData.nodeDataArray) {
            delete  postData.nodeDataArray[x].__gohashid;
        }
        for (var x in postData.linkDataArray) {
            delete  postData.linkDataArray[x].__gohashid;
            delete  postData.linkDataArray[x].points;
        }
        var req = {
            method: 'post',
            url: '/task/saveWorkFlow',
            data: {json: angular.toJson(postData)}
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