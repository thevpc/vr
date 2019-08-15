/* 
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
function makeStruct(names) {
    var names = names.split(' ');
    var count = names.length;

    function constructor() {
        for (var i = 0; i < Math.min(count, arguments.length); i++) {
            this[names[i]] = arguments[i];
        }
        for (var i = Math.min(count, arguments.length); i < count; i++) {
            this[names[i]] = null;
        }
    }

    return constructor;
}


//angular.module('app', ['dynform']);
//angular.module('app', ['ngWig','dynform']);

$(document).ready(
    function()
    {
        $("div.ui-selectonemenu.full-width").each(
            function()
            {
                $(this).css("width",$(this).parent().width());
            }
        );
    }
);