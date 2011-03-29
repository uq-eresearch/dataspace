<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<script type="text/javascript">
    function submit() {
        $.ajax({
            type: "POST",
            url: $("#lookup-form").attr('action'),
            data: $("#lookup-form").serializeArray(),
            dataType: 'html',
            success: function(data) {
                $("#lookup-result").append('<li>Result 1</li>');
                $("#lookup-result").append('<li>Result 2</li>');
            }
        });
    }
</script>
<form id="lookup-form" method="post" action="lookup" onsubmit="submit();return false;">
    <table width="100%">
        <tbody>
        <tr>
            <td><input type="text" id="keyword" name="keyword" value=""/></td>
            <td><input type="submit" name="lookup-submit" id="lookup-submit" value="Search"/></td>
        </tr>
        </tbody>
    </table>
</form>
<ul id="lookup-result">
    <li><input type="button" value="Select"/></li>
</ul>