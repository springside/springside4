"use strict";
var javasimon=window.javasimon||{};
window.javasimon=javasimon;

$.getScript("resource/js/javasimon-util.js", function(data, textStatus, jqxhr) {
    console.log("javasimon-utils were loaded");
});

javasimon.onTableData = function(json, timeUnit) {
    $.each(json, function(index, sample) {
        var $sampleRow = javasimon.DOMUtil.fnGetSampleRow(sample.name);
        var maxTime = javasimon.TimeUtils.toMillis(sample.max, timeUnit);
        if (maxTime > 20) {
            $sampleRow.css('background-color','#ff8888');
        }
    });
};

function isLeaf(treeElement) {
    return treeElement.bHasChildren === false;
}

javasimon.onTreeElementDrawn = function(treeElement, timeUnit) {
    if (isLeaf(treeElement)) {
        var $sampleRow = javasimon.DOMUtil.fnGetSampleRow(treeElement.oData.name);
        var maxTime = javasimon.TimeUtils.toMillis(treeElement.oData.max, timeUnit);
        if (maxTime > 20) {
            $sampleRow.css('background-color','#ff8888');
        }
    }
};