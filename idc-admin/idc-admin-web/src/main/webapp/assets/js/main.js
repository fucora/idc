/***
 Metronic AngularJS App Main Script
 ***/

/* Metronic App */
var MetronicApp = angular.module("MetronicApp", [
    "ui.router",
    "ui.bootstrap",
    "oc.lazyLoad",
    "ngSanitize",
    "ngTable",
    "ngCookies",
    'frapontillo.bootstrap-switch'
]);

/* Configure ocLazyLoader(refer: https://github.com/ocombe/ocLazyLoad) */
MetronicApp.config(['$ocLazyLoadProvider', function ($ocLazyLoadProvider) {
    $ocLazyLoadProvider.config({
        // global configs go here
    });
}]);

/********************************************
 BEGIN: BREAKING CHANGE in AngularJS v1.3.x:
 *********************************************/
/**
 `$controller` will no longer look for controllers on `window`.
 The old behavior of looking on `window` for controllers was originally intended
 for use in examples, demos, and toy apps. We found that allowing global controller
 functions encouraged poor practices, so we resolved to disable this behavior by
 default.

 To migrate, register your controllers with modules rather than exposing them
 as globals:

 Before:

 ```javascript
 function MyController() {
  // ...
}
 ```

 After:

 ```javascript
 angular.module('myApp', []).controller('MyController', [function() {
  // ...
}]);

 Although it's not recommended, you can re-enable the old behavior like this:

 ```javascript
 angular.module('myModule').config(['$controllerProvider', function($controllerProvider) {
  // this option might be handy for migrating old apps, but please don't use it
  // in new ones!
  $controllerProvider.allowGlobals();
}]);
 **/

//AngularJS v1.3.x workaround for old style controller declarition in HTML
MetronicApp.config(['$controllerProvider', function ($controllerProvider) {
    // this option might be handy for migrating old apps, but please don't use it
    // in new ones!
    $controllerProvider.allowGlobals();
}]);

/********************************************
 END: BREAKING CHANGE in AngularJS v1.3.x:
 *********************************************/

/* Setup global settings */
MetronicApp.factory('settings', ['$rootScope', function ($rootScope) {
    // supported languages
    var settings = {
        layout: {
            pageSidebarClosed: false, // sidebar menu state
            pageContentWhite: true, // set page content layout
            pageBodySolid: false, // solid body color state
            pageAutoScrollOnLoad: 1000 // auto scroll to top on page load
        },
        assetsPath: '../assets',
        globalPath: '../assets/global',
        layoutPath: '../assets/layouts/layout'
    };

    $rootScope.settings = settings;

    return settings;
}]);
MetronicApp.factory('ajaxInterceptor', function ($q) {
    var count = 0;
    var interceptor = {
        'request': function (config) {
            App.blockUI({
                target: "body",
                zIndex: 2000,
            });
            count++;
// 成功的请求方法
            return config; // 或者 $q.when(config);
        },
        'response': function (response) {
            count--;
            if (count <= 0) {
                App.unblockUI("body")
            }
// 响应成功
            return response; // 或者 $q.when(config);
        },
        'requestError': function (rejection) {
            count--;
            if (count <= 0) {
                App.unblockUI("body")
            }
// 请求发生了错误，如果能从错误中恢复，可以返回一个新的请求或promise
            return rejection; // 或新的promise
// 或者，可以通过返回一个rejection来阻止下一步
// return $q.reject(rejection);
        },
        'responseError': function (rejection) {
            if (rejection.status === 666) {

                window.location.href = rejection.data.dataMap.redirectUrl;
                return rejection;
            }
            count--;
            if (count <= 0) {
                App.unblockUI("body")
            }
// 请求发生了错误，如果能从错误中恢复，可以返回一个新的响应或promise
            return rejection; // 或新的promise
// 或者，可以通过返回一个rejection来阻止下一步
// return $q.reject(rejection);
        }
    };
    return interceptor;
});

MetronicApp.factory('systemService', ['$http', '$q', function ($http, $q) {
    var loginUserData;
    var service = {
        getLoginUser: function (reload) {
            var deferred = $q.defer();
            if (loginUserData && !reload) {
                deferred.resolve(loginUserData);
            }
            else {
                $http.get('/system/getLoginUser').success(function (data) {
                    loginUserData = data;
                    deferred.resolve(loginUserData);
                }).error(function (data, status, headers, config, a, v, c) {
                    // 当响应以错误状态返回时调用
                });
            }
            return deferred.promise;
        }
    };
    return service;
}]);

MetronicApp.factory('exchangeService', [function () {
    var store = {}
    var service = {
        get: function (key) {
            return store[key];
        },
        set: function (key, value) {
            store[key] = value;
        }
    };
    return service;
}]);


/* Setup App Main Controller */
MetronicApp.controller('AppController', ['$scope', '$rootScope', 'systemService', "$timeout", function ($scope, $rootScope, systemService, $timeout) {
    $scope.$on('$includeContentLoaded', function () {
        //App.initComponents(); // init core components
        //Layout.init(); //  Init entire layout(header, footer, sidebar, etc) on page load if the partials included in server side instead of loading with ng-include directive
    });
    $rootScope.$on("systemInfoChanged", function () {

        systemService.getLoginUser(true).then(function (data) {
            $timeout(function () {
                $scope.$apply(function () {
                    $scope.system = data.dataMap;
                    console.log(data);

                });
            }, 0);
        });

    });
}]);

/***
 Layout Partials.
 By default the partials are loaded through AngularJS ng-include directive. In case they loaded in server side(e.g: PHP include function) then below partial
 initialization can be disabled and Layout.init() should be called on page load complete as explained above.
 ***/

/* Setup Layout Part - Header */
MetronicApp.controller('HeaderController', ['$scope', "$state", "$cookies","$uibModal", function ($scope, $state, $cookies,$uibModal) {
    $scope.$on('$includeContentLoaded', function () {
        Layout.initHeader(); // init header
        $scope.logout = function () {
            $cookies.remove("ticketName");
            $cookies.remove("UYBFEWAEE", {expires: -1, path: '/', domain: 'dmall.com', secure: true});
            window.location.reload();
        }

        $scope.showUserModal = function (row) {
           $uibModal.open({
                templateUrl: 'userModal.html',
                controller: 'userModalCtrl',
                size: 'md',
                resolve: {
                    row: function () {
                        return row;
                    }
                }
            });
        };
    });
}]);

MetronicApp.controller('userModalCtrl', ["$scope", "$uibModalInstance", "$http", "row", function ($scope, $uibModalInstance, $http, row) {

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
                $scope.$emit('systemInfoChanged');
            } else {
                toastr.error(data.msg, "保存数据失败！！！");
            }

        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };


}]);

/* Setup Layout Part - Sidebar */
MetronicApp.controller('SidebarController', ['$scope', "$rootScope", function ($scope, $rootScope) {
    $scope.$on('$includeContentLoaded', function () {
        Layout.initSidebar(); // init sidebar
    });
    $scope.$on('$locationChangeSuccess', function () {
        Layout.setSidebarMenuActiveLink('match'); // activate selected link in the sidebar menu
    });
    // $scope.$on("$ngRepeatFinished",function(){
    //     Layout.setSidebarMenuActiveLink('match');
    // })
}]);

/* Setup Layout Part - Quick Sidebar */
MetronicApp.controller('QuickSidebarController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        setTimeout(function () {
            QuickSidebar.init(); // init quick sidebar
        }, 2000)
    });
}]);

/* Setup Layout Part - Theme Panel */
MetronicApp.controller('ThemePanelController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        Demo.init(); // init theme panel
    });
}]);

/* Setup Layout Part - Footer */
MetronicApp.controller('FooterController', ['$scope', function ($scope) {
    $scope.$on('$includeContentLoaded', function () {
        Layout.initFooter(); // init footer
    });
}]);

/* Setup Rounting For All Pages */
MetronicApp.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', function ($stateProvider, $urlRouterProvider, $httpProvider) {
    // Redirect any unmatched url
    $urlRouterProvider.otherwise("/dashboard.html");
    $stateProvider

    // Dashboard
        .state('dashboard', {
            url: "/dashboard.html",
            templateUrl: "/assets/views/dashboard.html",
            data: {pageTitle: '总览'},
            controller: "DashboardController",
            resolve: {
                deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                    return $ocLazyLoad.load({
                        name: 'MetronicApp',
                        insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                        files: [
                            '/assets/views/DashboardController.js',
                            '/assets/global/plugins/jquery.quicksearch.js',
                            '/assets/global/plugins/multi-select/js/jquery.multi-select.js',
                            '/assets/global/plugins/multi-select/css/multi-select.css'
                        ]
                    });
                }]
            }
        }).state('node', {
        url: '/node/{appId:[0-9]{1,10}}',
        templateUrl: "/assets/views/app/node.html",
        data: {pageTitle: '实例管理'},
        controller: "NodeController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/NodeController.js'
                    ]
                });
            }]
        }
    }).state('task', {
        url: '/task/{appId:[0-9]{1,10}}',
        templateUrl: "/assets/views/app/task.html",
        data: {pageTitle: '任务管理'},
        controller: "TaskController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    serie:true,
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/TaskController.js',
                        '/assets/global/plugins/fuelux/js/spinner.min.js',
                        '/assets/global/plugins/gentleSelect/jquery-gentleSelect-min.js',
                        '/assets/global/plugins/gentleSelect/jquery-gentleSelect.css',
                        '/assets/global/plugins/cron/jquery-cron.js',
                        '/assets/global/plugins/cron/jquery-cron.css',
                        '/assets/js/workFlow.js',
                        // '/assets/global/plugins/jquery-ui/jquery-ui.min.js',
                        '/assets/global/plugins/goJs/go.js',
                        '/assets/global/plugins/goJs/goSamples.css',
                        '/assets/global/plugins/goJs/goSamples.js',
                        '/assets/global/plugins/goJs/highlight.css',
                        '/assets/global/plugins/goJs/DataInspector.js',
                        '/assets/global/plugins/goJs/DataInspector.css',
                        '/assets/global/plugins/angularjs/plugins/ui-select/select.min.css',
                        '/assets/global/plugins/angularjs/plugins/ui-select/select.min.js',
                        '/assets/global/plugins/echart/echarts.js',
                        '/assets/global/plugins/echart/macarons.js'
                    ]
                });
            }]
        }
    }).state('task.detail',{
        url: '/detail',
        views: {
            // So this one is targeting the unnamed view within the parent state's template.
            '': { templateUrl: "/assets/views/app/detail.html"}
            }
    }).state('taskHistory', {
        url: '/taskHistory/{appId:[0-9]{1,10}}?taskType&taskId&beginTime&result',
        templateUrl: "/assets/views/app/taskHistory.html",
        data: {pageTitle: '任务执行历史'},
        controller: "TaskHistoryController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/TaskHistoryController.js'
                    ]
                });
            }]
        }, params: {
            taskId: ''
        }
    }).state('taskStatistic', {
        url: '/taskStatistic/{appId:[0-9]{1,10}}',
        templateUrl: "/assets/views/app/taskStatistic.html",
        data: {pageTitle: '任务执行统计'},
        controller: "TaskStatisticController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/TaskStatisticController.js',
                    ]
                });
            }]
        }
    }).state('doc', {
        url: '/doc',
        templateUrl: "/assets/views/intro/doc.html",
        data: {pageTitle: '接入文档'}
    }).state('skills', {
        url: '/skills',
        templateUrl: "/assets/views/intro/skills.html",
        data: {pageTitle: '接入技巧'}
    }).state('server', {
        url: '/server',
        templateUrl: "/assets/views/admin/system.html",
        data: {pageTitle: '服务器监控'},
        controller: "SystemController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/admin/SystemController.js'
                    ]
                });
            }]
        }
    }).state('applicationManage', {
        url: '/applicationManage',
        templateUrl: "/assets/views/admin/applicationManage.html",
        data: {pageTitle: '应用管理'},
        controller: "ApplicationManageController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/admin/ApplicationManageController.js'
                    ]
                });
            }]
        }
    }).state('appDashboard',{

        url: '/appDashboard/{appId:[0-9]{1,10}}',
        templateUrl: "/assets/views/app/appDashboard.html",
        data: {pageTitle: '应用总览'},
        controller: "AppDashboardController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    serie:true,
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/AppDashboardController.js',
                        '/assets/global/css/pricing.css',
                        '/assets/global/plugins/echart/echarts.js',
                        '/assets/global/plugins/echart/macarons.js'
                    ]
                });
            }]
        }
    }).state('alarmHistory',{

        url: '/alarmHistory/{appId:[0-9]{1,10}}',
        templateUrl: "/assets/views/app/alarmHistory.html",
        data: {pageTitle: '报警历史'},
        controller: "AlarmHistoryController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    serie:true,
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/app/AlarmHistoryController.js'
                    ]
                });
            }]
        }
    }).state('alarmHistoryManage',{

        url: '/alarmHistoryManage',
        templateUrl: "/assets/views/admin/alarmHistoryManage.html",
        data: {pageTitle: '报警历史'},
        controller: "AlarmHistoryManageController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    serie:true,
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/admin/AlarmHistoryManageController.js'
                    ]
                });
            }]
        }
    }).state('runningTaskManage', {
        url: '/runningTaskManage',
        templateUrl: "/assets/views/admin/runningTaskManage.html",
        data: {pageTitle: '正在执行任务'},
        controller: "RunningTaskManageController",
        resolve: {
            deps: ['$ocLazyLoad', function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    serie: true,
                    name: 'MetronicApp',
                    insertBefore: '#ng_load_plugins_before', // load the above css files before a LINK element with this ID. Dynamic CSS files must be loaded between core and theme css files
                    files: [
                        '/assets/views/admin/RunningTaskManageController.js'
                    ]
                });
            }]
        }
    });


    // Every POST data becomes jQuery style
    $httpProvider.defaults.transformRequest.push(
        function (data) {
            var requestStr;
            if (data) {
                data = JSON.parse(data);
                for (var key in data) {
                    if (requestStr) {
                        requestStr += '&' + key + '=' + data[key];
                    } else {
                        requestStr = key + '=' + data[key];
                    }
                }
            }
            return requestStr;
        });
    // Set the content type to be FORM type for all post requests
    // This does not add it for GET requests.
    $httpProvider.defaults.headers.post['Content-Type'] =
        'application/x-www-form-urlencoded';

    $httpProvider.interceptors.push('ajaxInterceptor');
    jQuery(document).ready();
    toastr.options = {
        "closeButton": true,
        "debug": false,
        "positionClass": "toast-top-center",
        "onclick": null,
        "showDuration": "1000",
        "hideDuration": "1000",
        "timeOut": "5000",
        "extendedTimeOut": "1000",
        "showEasing": "swing",
        "hideEasing": "linear",
        "showMethod": "fadeIn",
        "hideMethod": "fadeOut"
    };
    bootbox.setLocale("zh_CN");
}]);

/* Init global settings and run the app */
MetronicApp.run(["$rootScope", "settings", "$state", 'systemService', function ($rootScope, settings, $state, systemService) {
    $rootScope.$state = $state; // state to be accessed from view
    $rootScope.$settings = settings; // state to be accessed from view
    // $rootScope.$ddc = {
    //     taskTypeOptions: [{key: ''},{key: '定时任务', value: 11}, {key: '流程任务', value: 21}, {key: '流程子任务', value: 12}]
    // };
    systemService.getLoginUser().then(function (data) {
        $rootScope.system = data.dataMap;
    });
}]);