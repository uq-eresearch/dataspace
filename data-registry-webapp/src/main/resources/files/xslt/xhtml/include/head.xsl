<htmlcode>
    <link rel="stylesheet" type="text/css" href="/css/style.css"/>
    <script type="text/javascript" src="/jquery/jquery.js">;</script>
    <script type="text/javascript" src="/jquery/jquery.jfeed.js">;</script>
    <script type="text/javascript">
        $(document).ready(function() {
        $(".signin").click(function(e) {
        e.preventDefault();
        $("fieldset#signin_menu").toggle();
        $(".signin").toggleClass("menu-open");
        });

        $("fieldset#signin_menu").mouseup(function() {
        return false
        });
        $(document).mouseup(function(e) {
        if ($(e.target).parent("a.signin").length == 0) {
        $(".signin").removeClass("menu-open");
        $("fieldset#signin_menu").hide();
        }
        });
        });
    </script>
</htmlcode>