$(document).ready(function() {
    $(".signin").click(function(e) {
        e.preventDefault();
        $('#login-error').html('');
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

function login() {
    var username = $('#username').val();
    var password = $('#password').val();
    $.ajax({
        type: 'POST',
        url: '/login',
        data: 'username=' + username + '&password=' + password,
        success: function(data) {
            $('#signin-link').html('Welcome ' + username + ', Sign out');
            $('a#signin-link').attr('href', '/logout');
            $('a#signin-link').attr('class', 'signout');
            $("fieldset#signin_menu").hide();
            $(".signout").click(function(e) {
                e.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '/logout',
                    success: function(data) {
                        $('#signin-link').html('Sign in');
                        $('a#signin-link').attr('href', 'logins');
                        $('a#signin-link').attr('class', 'signin');
                        $("fieldset#signin_menu").hide();
                    }
                });
            });
        },
        error: function(xhr, textStatus, errorThrown) {
            $('#login-error').html('Authentication Failed: incorrect username or password');
        }
    });
    return false;
}