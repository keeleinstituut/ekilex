$(document).ready(function () {
    horisontalScrollIndicators();
});

function horisontalScrollIndicators() {
    const listParent = $('.homonym-panel');

    if(listParent!== undefined && listParent.is(':visible')){

        const list = $(".homonym-list");
        const listItemW = 240;
        const listElem = list.get(0);
        let overFlow =  listElem.scrollWidth >  listElem.clientWidth;
        if(overFlow){
            listParent.addClass('overflow');
            checkScrollPositions();
        }

        $(list).scroll(function () {
            checkScrollPositions();
        });
        $(window).resize(function () {
           checkScrollPositions();
        });

        function checkScrollPositions() {
            if (listElem.scrollLeft < listItemW)
            {
                listParent.addClass('overflow-left-end');
            }
            if(listElem.scrollLeft > listItemW){
                listParent.removeClass('overflow-left-end');
            }
            if(listElem.scrollWidth - listElem.scrollLeft - listItemW > listElem.clientWidth ){
                listParent.removeClass('overflow-right-end');
            }
            if (listElem.scrollWidth - listElem.scrollLeft - listItemW < listElem.clientWidth)
            {
                listParent.addClass('overflow-right-end');
            }
        }
    }

}
