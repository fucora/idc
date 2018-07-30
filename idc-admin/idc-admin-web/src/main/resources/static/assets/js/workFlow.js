/**
 * Created by xkwu on 2016/5/17.
 */
MetronicApp.directive('goDiagram', function () {
    return {
        restrict: 'E',
        // template: '<div></div>',  // just an empty DIV element
        replace: true,
        templateUrl: "workFlow.html",
        scope: {model: "=workFlowData", subTask: "="},
        link: function (scope, element, attrs) {
            var $$ = go.GraphObject.make;
            var diagram =  // create a Diagram for the given HTML DIV element
                $$(go.Diagram, "myDiagramDiv",
                    {
                        // start everything in the middle of the viewport
                        initialContentAlignment: go.Spot.Center,
                        // have mouse wheel events zoom in and out instead of scroll up and down
                        // "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
                        // support double-click in background creating a new node
                        // "clickCreatingTool.archetypeNodeData": {text: "new node"},
                        "ModelChanged": updateAngular,
                        // enable undo & redo
                        "undoManager.isEnabled": true,
                        allowDrop: true
                    });

// define the Node templates for regular nodes
            var lightText = 'whitesmoke';
            diagram.nodeTemplateMap.add("",  // the default category
                $$(go.Node, "Spot", nodeStyle(),
                    // the main object is a Panel that surrounds a TextBlock with a rectangular Shape
                    $$(go.Panel, "Auto",
                        $$(go.Shape, "Rectangle",
                            {fill: "#00A9C9", stroke: null},
                            new go.Binding("figure", "figure")),
                        $$(go.TextBlock,
                            {
                                font: "bold 11pt Helvetica, Arial, sans-serif",
                                stroke: lightText,
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,
                                // editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    ),
                    // four named ports, one on each side:
                    makePort("T", go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, true, true),
                    makePort("R", go.Spot.Right, true, true),
                    makePort("B", go.Spot.Bottom, true, false)
                ));


            diagram.nodeTemplateMap.add("Start",
                $$(go.Node, "Spot", nodeStyle(),
                    $$(go.Panel, "Auto",
                        $$(go.Shape, "Circle",
                            {minSize: new go.Size(40, 40), fill: "#79C900", stroke: null}),
                        $$(go.TextBlock, "Start",
                            {font: "bold 11pt Helvetica, Arial, sans-serif", stroke: lightText},
                            new go.Binding("text"))
                    ),
                    // three named ports, one on each side except the top, all output only:
                    makePort("L", go.Spot.Left, true, false),
                    makePort("R", go.Spot.Right, true, false),
                    makePort("B", go.Spot.Bottom, true, false),
                    makePort("T", go.Spot.Top, true, false)
                ));

            diagram.nodeTemplateMap.add("End",
                $$(go.Node, "Spot", nodeStyle(),
                    $$(go.Panel, "Auto",
                        $$(go.Shape, "Circle",
                            {minSize: new go.Size(40, 40), fill: "#DC3C00", stroke: null}),
                        $$(go.TextBlock, "End",
                            {font: "bold 11pt Helvetica, Arial, sans-serif", stroke: lightText},
                            new go.Binding("text"))
                    ),
                    // three named ports, one on each side except the bottom, all input only:
                    makePort("T", go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, false, true),
                    makePort("R", go.Spot.Right, false, true),
                    makePort("T", go.Spot.Top, false, true)
                ));

            diagram.commandHandler.canDeleteSelection = function () {
                var node = diagram.model.selectedNodeData;
                if (!node) {
                    return true;
                } else if (node.category === "Start") {  // could also check for e.control or e.shift
                    toastr.warning("不能删除【开始】节点");
                } else if (node.category === "End") {  // could also check for e.control or e.shift
                    toastr.warning("不能删除【结束】节点");
                } else {
                    // call base method with no arguments
                    return true;
                }
            };

            // Make all ports on a node visible when the mouse is over the node
            function showPorts(node, show) {
                var diagram = node.diagram;
                if (!diagram || diagram.isReadOnly || !diagram.allowLink) return;
                node.ports.each(function (port) {
                    port.stroke = (show ? "white" : null);
                });
            }

            // Define a function for creating a "port" that is normally transparent.
            // The "name" is used as the GraphObject.portId, the "spot" is used to control how links connect
            // and where the port is positioned on the node, and the boolean "output" and "input" arguments
            // control whether the user can draw links from or to the port.
            function makePort(name, spot, output, input) {
                // the port is basically just a small circle that has a white stroke when it is made visible
                return $$(go.Shape, "Circle",
                    {
                        fill: "transparent",
                        stroke: null,  // this is changed to "white" in the showPorts function
                        desiredSize: new go.Size(8, 8),
                        alignment: spot, alignmentFocus: spot,  // align the port on the main Shape
                        portId: name,  // declare this object to be a "port"
                        fromSpot: spot, toSpot: spot,  // declare where links may connect at this port
                        fromLinkable: output, toLinkable: input,  // declare whether the user may draw links to/from here
                        toLinkableDuplicates: false,
                        fromLinkableDuplicates: false,
                        cursor: "pointer"  // show a different cursor to indicate potential link point
                    });
            }

            // helper definitions for node templates
            function nodeStyle() {
                return [
                    // The Node.location comes from the "loc" property of the node data,
                    // converted by the Point.parse static method.
                    // If the Node.location is changed, it updates the "loc" property of the node data,
                    // converting back using the Point.stringify static method.
                    new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
                    {
                        // the Node.location is at the center of each node
                        locationSpot: go.Spot.Center,
                        //isShadowed: true,
                        //shadowColor: "#888",
                        // handle mouse enter/leave events to show/hide the ports
                        mouseEnter: function (e, obj) {
                            showPorts(obj.part, true);
                        },
                        mouseLeave: function (e, obj) {
                            showPorts(obj.part, false);
                        }
                    }
                ];
            }


            // replace the default Link template in the linkTemplateMap
            diagram.linkTemplate =
                $$(go.Link,  // the whole link panel
                    {
                        routing: go.Link.AvoidsNodes,
                        curve: go.Link.JumpOver,
                        corner: 5, toShortLength: 4,
                        relinkableFrom: true,
                        relinkableTo: true,
                        reshapable: true,
                        resegmentable: true,
                        // mouse-overs subtly highlight links:
                        mouseEnter: function (e, link) {
                            link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
                        },
                        mouseLeave: function (e, link) {
                            link.findObject("HIGHLIGHT").stroke = "transparent";
                        }
                    },
                    new go.Binding("points").makeTwoWay(),
                    $$(go.Shape,  // the highlight shape, normally transparent
                        {isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT"}),
                    $$(go.Shape,  // the link path shape
                        {isPanelMain: true, stroke: "gray", strokeWidth: 2}),
                    $$(go.Shape,  // the arrowhead
                        {toArrow: "standard", stroke: null, fill: "gray"}),
                    $$(go.Panel, "Auto",  // the link label, normally not visible
                        {visible: true, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5},
                        new go.Binding("visible", "visible").makeTwoWay(),
                        $$(go.Shape, "RoundedRectangle",  // the label shape
                            {fill: "#F8F8F8", stroke: null}),
                        $$(go.TextBlock, "备注",  // the label
                            {
                                textAlign: "center",
                                font: "10pt helvetica, arial, sans-serif",
                                stroke: "#333333",
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    )
                );


            // whenever a GoJS transaction has finished modifying the model, update all Angular bindings
            function updateAngular(e) {
                if (e.isTransactionFinished) {
                    scope.$apply();
                    //update();
                }
            }


            // notice when the value of "model" changes: update the Diagram.model
            scope.$watch("model", function (json) {
                var newmodel = go.Model.fromJson(json);
                var oldmodel = diagram.model;
                if (oldmodel !== newmodel) {
                    diagram.removeDiagramListener("ChangedSelection", updateSelection);
                    diagram.model = newmodel;
                    diagram.addDiagramListener("ChangedSelection", updateSelection);
                    //update();
                }
            });

            diagram.addModelChangedListener(function(evt) {
                // ignore unimportant Transaction events
                if (!evt.isTransactionFinished) return;
                var txn = evt.object;  // a Transaction
                if (txn === null) return;
                // iterate over all of the actual ChangedEvents of the Transaction
                txn.changes.each(function(e) {
                    // ignore any kind of change other than adding/removing a node
                    if (e.modelChange !== "nodeDataArray") return;
                    if (e.change === go.ChangedEvent.Insert) {
                        myPalette.model.removeNodeData(myPalette.model.findNodeDataForKey(e.newValue.key));
                        console.log(evt.propertyName + " added node with key: " + e.newValue.key);
                    } else if (e.change === go.ChangedEvent.Remove) {
                        //myPalette.model.addNodeData(diagram.model.findNodeDataForKey(e.oldValue.key));
                        console.log(evt.propertyName + " removed node with key: " + e.oldValue.key);
                        if(evt.propertyName == 'FinishedUndo'){
                            myPalette.model.removeNodeData(myPalette.model.findNodeDataForKey(e.oldValue.key));
                            myPalette.rebuildParts();
                        }

                        if(evt.propertyName == 'FinishedRedo' || evt.propertyName == 'CommittedTransaction'){
                            myPalette.model.addNodeData(findNodeDataForKey(e.oldValue.key));
                        }
                    }
                });
            });

            function findNodeDataForKey(key){
                for(var i in scope.subTask){
                    if(scope.subTask[i].taskId ===key){
                        return {
                            text: scope.subTask[i].taskName,
                            id: scope.subTask[i].taskId,
                            key: scope.subTask[i].taskId,
                            createTime:scope.subTask[i].createTime,
                            figure: "Octagon"
                        };
                    }
                }
            }

            function updatePalette() {
                var json = scope.model;
                var filterSubTask = [];
                for (var i in scope.subTask) {
                    var hasIn = false;
                    for (var x in json.nodeDataArray) {
                        if (json.nodeDataArray[x].id === scope.subTask[i].taskId) {
                            hasIn = true;
                            break;
                        }
                    }

                    if (!hasIn) {
                        filterSubTask.push({
                            text: scope.subTask[i].taskName,
                            id: scope.subTask[i].taskId,
                            key: scope.subTask[i].taskId,
                            createTime:scope.subTask[i].createTime,
                            figure: "Octagon"
                        });
                    }
                }
                myPalette.model = new go.GraphLinksModel(filterSubTask);
            }

            // update the model when the selection changes
            function updateSelection(e) {
                var selnode = diagram.selection.first();
                diagram.model.selectedNodeData = (selnode instanceof go.Node ? selnode.data : null);
                scope.$apply();
            }

            diagram.addDiagramListener("ChangedSelection", updateSelection);

            diagram.commandHandler.redo()

            // initialize the Palette that is in a floating, draggable HTML container
            var myPalette = new go.Palette("myPaletteDiv");  // must name or refer to the DIV HTML element
            myPalette.nodeTemplateMap = diagram.nodeTemplateMap;
            //myPalette.model.nodeKeyProperty = "id";
            myPalette.layout.comparer = function(a, b) {
                // A and B are Parts
                var av = a.data.createTime;
                var bv = b.data.createTime;
                if (av < bv) return -1;
                if (av > bv) return 1;
                return 0;
            };
            updatePalette();
        }
    };
}).filter('dup', function () {
    return function (input) {
        return input;
    }
});