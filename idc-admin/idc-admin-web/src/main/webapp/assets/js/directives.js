/***
 GLobal Directives
 ***/

// Route State Load Spinner(used on page or content load)
MetronicApp.directive('ngSpinnerBar', ['$rootScope',
    function ($rootScope) {
        return {
            link: function (scope, element, attrs) {
                // by defult hide the spinner bar
                element.addClass('hide'); // hide spinner bar by default

                // display the spinner bar whenever the route changes(the content part started loading)
                $rootScope.$on('$stateChangeStart', function () {
                    element.removeClass('hide'); // show spinner bar
                });

                // hide the spinner bar on rounte change success(after the content loaded)
                $rootScope.$on('$stateChangeSuccess', function () {
                    element.addClass('hide'); // hide spinner bar
                    $('body').removeClass('page-on-load'); // remove page loading indicator
                    Layout.setSidebarMenuActiveLink('match'); // activate selected link in the sidebar menu

                    // auto scorll to page top
                    setTimeout(function () {
                        App.scrollTop(); // scroll to the top on content load
                    }, $rootScope.settings.layout.pageAutoScrollOnLoad);
                });

                // handle errors
                $rootScope.$on('$stateNotFound', function () {
                    element.addClass('hide'); // hide spinner bar
                });

                // handle errors
                $rootScope.$on('$stateChangeError', function () {
                    element.addClass('hide'); // hide spinner bar
                });
            }
        };
    }
])

// Handle global LINK click
MetronicApp.directive('a', function () {
    return {
        restrict: 'E',
        link: function (scope, elem, attrs) {
            if (attrs.ngClick || attrs.href === '' || attrs.href === '#') {
                elem.on('click', function (e) {
                    e.preventDefault(); // prevent link click for above criteria
                });
            }
        }
    };
});

// Handle Dropdown Hover Plugin Integration
MetronicApp.directive('dropdownMenuHover', function () {
    return {
        link: function (scope, elem) {
            elem.dropdownHover();
        }
    };
});

MetronicApp.directive('onFinishRenderFilters', function ($timeout) {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            if (scope.$last === true) {
                $timeout(function () {
                    scope.$emit('$ngRepeatFinished');
                });
            }
        }
    };
});

MetronicApp.directive('spinner', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attr, ngModel) {
            ngModel.$render = function () {
                var option = {value: ngModel.$viewValue};
                angular.extend(option, attr);
                $(element).spinner(option);
                $(element).on("changed", function (event, value) {
                    scope.$apply(function () {
                        ngModel.$setViewValue(value);
                    });
                });
            }
        }
    };
});
MetronicApp.directive('bootstrapSwitch', function () {
    return {
        require: '?ngModel',
        restrict: 'E',
        replace: true,
        template: " <input type='checkbox'  class='make-switch' data-label-width='40px'>",
        scope: {
            offText: '@',
            onText: '@',
            onColor: '@',
            offColor: '@',
            onReadonly: '@'
        },
        link: function (scope, element, attrs, ngModel) {
            ngModel.$render = function () {
                $(element).bootstrapSwitch("state", ngModel.$modelValue == 1 ? true : false);
                if (scope.offText) {
                    $(element).bootstrapSwitch("offText", scope.offText);
                }
                if (scope.onText) {
                    $(element).bootstrapSwitch("onText", scope.onText);
                }
                if (scope.onColor) {
                    $(element).bootstrapSwitch("onColor", scope.onColor);
                }
                if (scope.offColor) {
                    $(element).bootstrapSwitch("offColor", scope.offColor);
                }
                if (scope.onReadonly) {
                    if (scope.onReadonly === 'true') {
                        $(element).bootstrapSwitch("readonly", true);
                    } else {
                        $(element).bootstrapSwitch("readonly", false);
                    }
                }
                $(element).on('switchChange.bootstrapSwitch', function (event, state) {
                    if (!scope.$$phase) {
                        scope.$apply(function () {
                            ngModel.$setViewValue(state ? 1 : 0);
                            ngModel.$modelValue == state ? 1 : 0;
                        });
                    }
                });
                scope.$watch('onReadonly', function (newValue) {
                    if (newValue === 'true') {
                        $(element).bootstrapSwitch("readonly", true);
                    } else {
                        $(element).bootstrapSwitch("readonly", false);
                    }
                });
            }
        }
    };
});

MetronicApp.directive('cron', function () {
    return {
        require: '?ngModel',
        restrict: 'AE',
        replace: true,
        template: " <div></div>",
        link: function (scope, element, attrs, ngModel) {
            ngModel.$render = function () {
                $(element).cron({
                    initial: ngModel.$viewValue ? ngModel.$viewValue : '* * * * *',
                    onChange: function () {
                        if (!scope.$$phase) {
                            scope.$apply(function () {
                                ngModel.$setViewValue($(element).cron("value"));
                            });
                        }
                    },
                    useGentleSelect: true
                });
            };


        },
        // compile: function (templateElement, templateAttrs) {
        //     return {
        //         pre: function (scope, instanceElement, instanceAttrs, controller) {
        //             // pre-linking function
        //         },
        //         post: function (scope, instanceElement, instanceAttrs, controller) {
        //             // post-linking function
        //         }
        //     }
        // }
    };
});

MetronicApp.directive('tips', function () {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            tipsTitle: "="
        },
        template: " <a  class='tooltips' data-original-title='{{ tipsTitle }}'>{{ tipsTitle }}</a>",
        link: function (scope, element, attrs) {
            $(element).tooltip();
        }
    }
});

MetronicApp.directive('ddcTaskTypeSelect', function () {
    return {
        restrict: 'AE',
        replace: true,
        scope: {
            tipsTitle: "="
        },
        template: " <a  class='tooltips' data-original-title='{{ tipsTitle }}'>{{ tipsTitle }}</a>",
        link: function (scope, element, attrs) {
            $(element).tooltip();
        }
    }
});

MetronicApp.directive('dateRange', function ($timeout) {
    return {
        require: '?ngModel',
        restrict: 'E',
        replace: true,
        scope: {
            startTime: '=startTime',
            endTime: '=endTime'
        },
        templateUrl: "dateRange.html",
        link: function (scope, element, attrs, ngModel) {
            moment.locale('zh_cn')
            $(element).daterangepicker({
                opens: App.isRTL() ? "left" : "right",
                format: "YYYY-MM-DD",
                separator: " to ",
                startDate: scope.startTime,
                endDate: scope.endTime,
                ranges: {
                    "今天": [moment().hour(0).minutes(0).seconds(0), moment().hour(23).minutes(59).seconds(59)],
                    "昨天": [moment().subtract("days", 1).hour(0).minutes(0).seconds(0), moment().subtract("days", 1).hour(23).minutes(59).seconds(59)],
                    "最近7天": [moment().subtract("days", 6).hour(0).minutes(0).seconds(0), moment().hour(23).minutes(59).seconds(59)]
                },
                minDate: "2016-01-01",
                maxDate: "2030-01-01",
                "timePicker": true,
                "timePicker24Hour": true,
                "timePickerSeconds": true,
                locale: {
                    cancelLabel: '取消',
                    applyLabel: '确认',
                    customRangeLabel: '日期选择',
                    format: "YYYY-MM-DD HH:mm:ss"
                }
            }, function (t, e) {
                scope.$evalAsync(function () {
                    scope.startTime = t.format("YYYY-MM-DD HH:mm:ss");
                    scope.endTime = e.format("YYYY-MM-DD HH:mm:ss");
                });
            });
        }
    };
});

MetronicApp.directive('ddcDataTable', function () {
    return {
        // require: '?ngModel',
        restrict: 'A',
        template: '',
        scope: {
            queryCondition: "@"
        },
        // templateUrl: "dateRange.html",
        link: function (scope, element, attrs) {
            var table = $(element).on('preXhr.dt', function (e, settings, data) {
                console.log(scope.queryCondition)
            }).DataTable({
                "processing": true,
                "serverSide": true,
                "ajax": {
                    "url": "/task/test",
                    "type": "POST",
                    "dataSrc": "dataMap.data",
                    // "dataType": "json",
                    // "contentType": "application/json",
                },
                "columns": [
                    {"data": "name", className: "all"},
                    {"data": "position", className: "min-phone-l"},
                    {"data": "office", className: "min-tablet"},
                    {"data": "start_date", className: "never"},
                    {"data": "salary", className: "desktop"},
                    {"data": "extn", className: "none"}
                ],
                "fnServerData": function (sSource, aoData, fnCallback, oSettings) {
                    oSettings.jqXHR = $.ajax({
                        "dataType": 'json',
                        "type": "POST",
                        "url": sSource,
                        "data": aoData,
                        "success": fnCallback
                    });
                }
            });

        }
    };
});
MetronicApp.directive('timePulsate', function () {
    return {
        restrict: 'A',
        replace: true,
        template: "<div>{{time|date:'yyyy-MM-dd HH:mm:ss'}}</div>",
        scope: {
            time: "=", pulsate: "="

        },
        link: function (scope, element, attr) {
            if (scope.pulsate == 2) {
                $(element).pulsate({
                    color: "#bf1c56"
                });
            }

        }
    };
});

MetronicApp.directive('multiSelect', function ($timeout) {
    return {
        restrict: 'AE',
        replace: true,
        templateUrl: "test.html",
        scope: {
            selectable: '=',
            selection: '='
        },
        link: function (scope, element, attr) {

            scope.$watch('selectable+selection', function (newValue,oldValue) {
                if(newValue!=oldValue){
                    $timeout(function() {
                        init();
                    },0,false);
                }

            });

            function init() {
                $(element).multiSelect({
                    selectableHeader: "<input type='text' class='search-input form-control ' autocomplete='on' placeholder='搜索...'>",
                    selectionHeader: "<input type='text' class='search-input form-control ' autocomplete='on' placeholder='搜索...'>",
                    selectableFooter: "<div class='custom-header'>可添加人员</div>",
                    selectionFooter: "<div class='custom-header'>已添加人员</div>",
                    afterInit: function (ms) {
                        var that = this,
                            $selectableSearch = that.$selectableUl.prev(),
                            $selectionSearch = that.$selectionUl.prev(),
                            selectableSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selectable:not(.ms-selected)',
                            selectionSearchString = '#' + that.$container.attr('id') + ' .ms-elem-selection.ms-selected';

                        that.qs1 = $selectableSearch.quicksearch(selectableSearchString)
                            .on('keydown', function (e) {
                                if (e.which === 40) {
                                    that.$selectableUl.focus();
                                    return false;
                                }
                            });

                        that.qs2 = $selectionSearch.quicksearch(selectionSearchString)
                            .on('keydown', function (e) {
                                if (e.which == 40) {
                                    that.$selectionUl.focus();
                                    return false;
                                }
                            });
                    },
                    afterSelect: function (values) {
                        scope.$apply(function () {
                            for (var x in values) {
                                scope.selection[values[x]] = true;
                            }
                        });

                        this.qs1.cache();
                        this.qs2.cache();
                    },
                    afterDeselect: function (values) {
                        scope.$apply(function () {
                            for (var x in values) {
                                delete  scope.selection[values[x]];
                            }
                        });
                        this.qs1.cache();
                        this.qs2.cache();
                    }
                });

                $(element).multiSelect('addOption', scope.selectable ? scope.selectable : {});
               if(scope.selection){
                   for(var x in scope.selection){
                       $(element).multiSelect('select',x);
                   }
               }
               for(var x in scope.selectable){
                    if(scope.selectable[x].disabled==true){
                        $(element).multiSelect('disabled',scope.selectable[x].value+"");
                    }
               }
                // var items = [];
                // for (var x in scope.selection) {
                //     items.push(x);
                // }
                // $(element).multiSelect('select', items);
            }

        }
    };
});

MetronicApp.run(['$templateCache', function ($templateCache) {

}]);