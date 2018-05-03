/**
 * Created by xkwu on 2016/5/23.
 */
angular.module('MetronicApp').controller('TaskStatisticController', ['$scope', '$http', '$uibModal', "$stateParams", "NgTableParams", function ($scope, $http, $uibModal, $stateParams, NgTableParams) {
    $scope.taskTypeOptions = [{key: '定时任务', value: 11}, {key: '简单任务', value: 13}, {key: '流程子任务', value: 12}, {key: '流程任务', value: 21}];
    var now = moment().format("YYYY-MM-DD");
    $scope.queryCondition = {
        beginTime: moment().hour(0).minutes(0).seconds(0).format("YYYY-MM-DD HH:mm:ss"),
        endTime: moment().hour(23).minutes(59).seconds(59).format("YYYY-MM-DD HH:mm:ss"),
        task: {taskType: $scope.taskTypeOptions[0].value}
    };
    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            var queryCondition = $.extend($scope.queryCondition, {
                page: {
                    length: params.count(),
                    begin: (params.page() - 1) * params.count()
                }
            });
            queryCondition.task.appId = $stateParams.appId;
            var req = {
                method: 'post',
                url: '/taskStatistic/taskStatisticTable',
                data: queryCondition,
                headers: {
                    'Content-Type': "application/json"
                },
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            };

            var b = $http(req).success(function (data, status, headers, config) {
                $scope.rows = data.dataList;
                $scope.tableParams.total(data.page.count);
            }).error(function (data, status, headers, config) {
                toastr.error("获取实例列表数据失败！！！")
            });
            return b.then(function (data) {
                return data.data.dataList;
            });
        }
    });

    var originQueryCondition = angular.copy($scope.queryCondition);
    $scope.resetForm = function () {
        $scope.queryCondition = angular.copy(originQueryCondition);
    };
}]);