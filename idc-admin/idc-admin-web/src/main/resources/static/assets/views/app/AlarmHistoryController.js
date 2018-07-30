/**
 * Created by xkwu on 2016/5/12.
 */
angular.module('MetronicApp').controller('AlarmHistoryController', ['$scope', '$http', "$stateParams", 'NgTableParams', function ($scope, $http, $stateParams, NgTableParams) {
    $scope.queryCondition = {
        beginTime: moment().hour(0).minutes(0).seconds(0).format("YYYY-MM-DD HH:mm:ss"),
        endTime: moment().hour(23).minutes(59).seconds(59).format("YYYY-MM-DD HH:mm:ss"),
        appId: Number($stateParams.appId)
    };
    $scope.tableParams = new NgTableParams({count: 20}, {
        counts: [20, 50, 100],
        getData: function (params) {
            var queryCondition = $.extend($scope.queryCondition, {
                length: params.count(),
                begin: (params.page() - 1) * params.count()
            });
            var req = {
                method: 'post',
                url: '/alarmHistory/alarmHistoryTable',
                data: queryCondition
            };

            var b = $http(req).success(function (data, status, headers, config) {
                $scope.rows = data.dataList;
                $scope.tableParams.total(data.page.count);
            }).error(function (data, status, headers, config) {
                toastr.error("获取报警历史列表数据失败！！！")
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