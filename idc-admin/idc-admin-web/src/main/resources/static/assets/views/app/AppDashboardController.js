/**
 * Created by xkwu on 2016/5/12.
 */
angular.module('MetronicApp').controller('AppDashboardController', ['$scope', '$http', "$stateParams", "$state", function ($scope, $http, $stateParams, $state) {
    $scope.appId = $stateParams.appId;
    var req = {
        method: 'post',
        url: '/appDashboard/dashboard',
        data: {appId: $stateParams.appId}
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

    function updateChart(data) {
        var succeedArray = [], failedArray = [], timeOutArray = [], timeArray = [];

        for (var x in data) {
            succeedArray.push(data[x].executeSucceed);
            failedArray.push(data[x].executeFailed);
            timeOutArray.push(data[x].executeTimeOut);
            timeArray.push(moment(data[x].dataTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'), 'macarons');

        // 指定图表的配置项和数据
        var option = {
            title: {
                text: '最近24小时任务执行情况'
            },
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['成功', '失败', '超时']
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
            xAxis: [
                {
                    type: 'category',
                    data: timeArray
                }
            ],
            yAxis: [
                {
                    name: '执行次数(次)',
                    type: 'value'
                }
            ],
            series: [
                {
                    name: '成功',
                    type: 'bar',
                    stack: '总量',
                    itemStyle: {
                        normal: {
                            color: "#36D7B7"
                        }
                    },
                    barMaxWidth: 40,
                    data: succeedArray
                },
                {
                    name: '失败',
                    type: 'bar',
                    stack: '总量',
                    itemStyle: {
                        normal: {
                            color: "#E35B5A"
                        }
                    },
                    barMaxWidth: 40,
                    data: failedArray
                },
                {
                    name: '超时',
                    type: 'bar',
                    stack: '总量',
                    itemStyle: {
                        normal: {
                            color: "#F4D03F"
                        }
                    },
                    barMaxWidth: 40,
                    data: timeOutArray
                }
            ]
        };


        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        myChart.on('dblclick', function (params) {
            var result;
            if (params.seriesName == "成功") {
                result = "SUCCEED";
            } else if (params.seriesName == "失败") {
                result = "FAILED";
            } else if (params.seriesName == "超时") {
                result = "EXECUTE_TIMEOUT";
            }
            $state.go("taskHistory", {appId: $stateParams.appId, beginTime: params.name,result:result})
        });
    }


}]);