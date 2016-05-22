$(document).ready(function(){
    $(".filtersButton").click(function() {
        var filterWindow = $(".filterWindow");
        if (filterWindow.is(':visible')) {
            $(".filtersButton span").addClass("glyphicon-chevron-down").removeClass("glyphicon-chevron-up");
        }
        else {
            $(".filtersButton span").addClass("glyphicon-chevron-up").removeClass("glyphicon-chevron-down");
        }
        filterWindow.slideToggle();
    });
});