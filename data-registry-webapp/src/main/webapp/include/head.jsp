<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<title><%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
</title>
<link rel="stylesheet" type="text/css" href="css/style.css"/>
<script type="text/javascript" src="jquery/jquery.js"></script>
<script type="text/javascript" src="jquery/jquery.jfeed.pack.js"></script>
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

    jQuery(function() {
        jQuery.getFeed({
            url: '<%= request.getParameter("path") %>?repr=application/atom+xml',
            success: function(feed) {

                jQuery('#result').append('<h2>'
                        + '<a href="'
                        + feed.link
                        + '">'
                        + feed.title
                        + '</a>'
                        + '</h2>');

                var html = '';

                for (var i = 0; i < feed.items.length && i < 5; i++) {

                    var item = feed.items[i];
                    html += '<div style="border-top:1px dashed #BE87E9;">'
                    html += '<a href="'
                            + item.link
                            + '">'
                            + item.title
                            + '</a>' + '<br/>';
                    html += item.description + ' ';
                    html += '<span style="font-size: 0.8em;">' + item.updated
                            + '</span></div>';

                }

                jQuery('#result').append(html);
            }
        });
    });
</script>
