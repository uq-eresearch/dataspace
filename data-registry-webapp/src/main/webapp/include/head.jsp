<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<title><%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
</title>
<link rel="stylesheet" type="text/css" href="css/style.css"/>
<script type="text/javascript" src="jquery/jquery.js"></script>
<script type="text/javascript" src="jquery/jquery.jfeed.js"></script>
<script type="text/javascript" src="jquery/login.js"></script>
<script type="text/javascript">
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
                    html += '<div class="record">'
                    if (item.isDraft == "yes") {
                        html += '<span class="draft">(draft)</span> ';
                    }
                    html += '<a href="'
                            + item.link
                            + '">'
                            + item.title
                            + '</a><span class="author"> (' + item.author + ')</span><br/>';
                    html += item.description.substring(0, 200) + ' ';
                    html += '<span class="record-date">' + item.updated
                            + '</span></div>';

                }

                jQuery('#result').append(html);
            }
        });
    });
</script>
