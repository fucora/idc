/**
 * Created by xkwu on 2016/6/20.
 */
angular.module('MetronicApp').controller('SystemController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
    var timer;

    function loadData() {
        $http.post("/admin/server/listServerTable").success(function (data, status, headers, config) {
            if (data.statusCode === "SUCCESS") {
                $scope.rows = data.dataList;
            } else {
                toastr.error(data.msg, "获取服务器列表数据失败！！！")
            }
        });
        timer = $timeout(
            function () {
                loadData();
            },
            5000
        );
    };
    loadData();
    $scope.$on(
        "$destroy",
        function (event) {
            if (timer) {

                $timeout.cancel(timer);
            }

        }
    );
}]);