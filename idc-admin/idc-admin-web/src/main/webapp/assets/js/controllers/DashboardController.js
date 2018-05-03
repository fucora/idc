angular.module('MetronicApp').controller('DashboardController',['$scope','$http','NgTableParams',function($scope, $http,NgTableParams) {
    $scope.$on('$viewContentLoaded', function() {
        $scope.tableParams = new NgTableParams({count: 20}, {
            counts: [20, 50, 100],
            getData: function (params) {
                $scope.queryCondition = {length:params.count(),begin:params.page() - 1};
                var req = {
                    method: 'GET',
                    url: '/application/listApp',
                    params: $scope.queryCondition,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
                };

                var b = $http(req).success(function (data, status, headers, config) {
                    $scope.rows = data.dataList;
                    $scope.tableParams.total(data.page.count);
                }).error(function (data, status, headers, config) {

                });
                return b.then(function (data) {
                    return data.data.dataList;
                });
            }
        });
    });

}]);