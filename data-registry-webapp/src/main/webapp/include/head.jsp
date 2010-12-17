<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<title><%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
</title>
<link rel="stylesheet" type="text/css" href="css/style.css"/>
<script type="text/javascript" src="jquery/jquery.js"></script>
<script type="text/javascript" src="jquery/jquery.jfeed.pack.js"></script>
<script type="text/javascript">
    jQuery(function() {

        jQuery.getFeed({
            url: '/collections?repr=application/atom+xml',
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
