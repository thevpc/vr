$(function () {
    var n = $("#pull"), e = $("nav ul");
    $(n).on("click", function (n) {
        n.preventDefault(), e.slideToggle()
    })
}), $(window).resize(function () {
    var n = $("nav ul"), e = $(window).width();
    e > 320 && n.is(":hidden") && n.removeAttr("style")
});