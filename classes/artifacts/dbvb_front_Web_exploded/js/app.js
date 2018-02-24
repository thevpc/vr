
$(function () {
        updateDocument();
    }
);

function updateDocument() {
    setInterval(function(){
        updateContent(['dashboard-live','dashboard-temporal'],function (obj) {
            $("#box1Val" ).text( obj.myName );
        })
    }, 5000);

    //$("#box1Label" ).text( "Hot Fuzz" );
}

function updateContent( items, successEvent, errorEvent) {
    console.log('updateContent');
    var resultText = "{\"s\":\"s\",\"r\":{\"myName\":\"MyValue\"}}";
    var obj=JSON.parse(resultText);
    if(obj.s='s') {
        successEvent(obj.r);
    }else{
        errorEvent(obj.r);
    }
}
