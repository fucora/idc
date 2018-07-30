/**
 * Created by xkwu on 2016/5/17.
 */
MetronicApp.filter("taskStatusLabel", function () {
    return function (u) {
        switch (u) {
            case 2:
                return "<span class='label label-warning label-sm'> 已删除 </span>";
            case 1 :
                return "<span class='label label-success label-sm'> 启用 </span>";
            case 0 :
                return "<span class='label label-danger label-sm'> 停用 </span>";
            default:
                return u;
        }
    };
});

MetronicApp.filter("executeResultLabel", function () {
    return function (u) {
        switch (u) {
            case 'SUCCEED' :
                return "<span class='label label-success label-sm'> SUCCEED </span>";
            case 'FAILED' :
                return "<span class='label label-danger label-sm'> FAILED </span>";
            case 'EXECUTE_TIMEOUT':
                return "<span class='label label-warning label-sm'> EXECUTE_TIMEOUT </span>";
            default:
                return u;
        }
    };
});

MetronicApp.filter("taskTypeLabel", function () {
    return function (u) {
        switch (u) {
            case 11:
                return "<span class='label label-warning label-sm'> 定时任务 </span>";
            case 12 :
                return "<span class='label label-success label-sm'> 流程子任务 </span>";
            case 13 :
                return "<span class='label label-info label-sm'> 简单任务 </span>";
            case 21 :
                return "<span class='label  label-info label-sm'> 流程任务 </span>";
            default:
                return u;
        }
    };
});

MetronicApp.directive('cronCheck', function () {
    /*使用方法：
     * <input type="tel" class="form-control" name="age" required ng-model="age" integer/>
     *  <div class="help-block" ng-show="MyForm.age.$error.integer">该项必须为整数</div>
     *  tips：
     * 自定义指令用来校验是不是整数，记得这个时候不要使用input的number不然你输入abc等值的时候无法触发watch可以改用tel，
     * 如果不想使用这个指令也可以使用ng-pattern配合正则完成
     * */
    return {
        restrict: "A",
        require: 'ngModel',
        link: function (scope, iElement, iAttrs, ngController) {
            var key = iAttrs.name ? iAttrs.name : "cronCheck";

            var reg = /{\d}{3}/;
            scope.$watch(iAttrs.ngModel, function (newVal) {
                if (!newVal) {
                    ngController.$setValidity(key, true);
                    return;
                }
                if (!reg.test(newVal)) {
                    ngController.$setValidity(key, false);
                } else {
                    ngController.$setValidity(key, true);
                }
            });
        }
    }
});


MetronicApp.filter('trustHtml', function ($sce) {
    return function (input) {
        return $sce.trustAsHtml(input);
    }
});


MetronicApp.filter("timeDiff", function () {
    return function (time) {
        var unit = ['毫秒', '秒', '分', '小时', '天'];
        var unitRatio = [1000, 60, 60, 24, 10000];

        function timeUnit(time, i, result) {
            if (i == 0) {
                if (time < 1000) {
                    return "小于1秒";
                } else {
                    return timeUnit(time / unitRatio[0], i + 1, result);
                }
            } else {
                result = time % unitRatio[i] + unit[i] + result;
                time = Math.floor(time / unitRatio[i]);
                if (time > 0) {
                    return timeUnit(time, i + 1, result);
                } else return result;
            }
        };

        return timeUnit(time, 0, '');
    };
});